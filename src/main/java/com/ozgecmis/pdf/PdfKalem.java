package com.ozgecmis.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDFBox'ın düşük seviyeli çizim komutlarını sade metotların arkasına gizleyen
 * yardımcı sınıf (metin yazma, dikdörtgen, daire, yuvarlak fotoğraf, satır kaydırma).
 *
 * <p><b>Neden ayrı bir sınıf?</b> {@link OzgecmisPdfOlusturucu} "özgeçmişte ne nerede
 * duracak" sorusuyla ilgilenir; bu sınıf ise "PDF'e nasıl çizilir" sorusuyla.
 * Böylece her sınıfın tek bir sorumluluğu olur (Single Responsibility).</p>
 *
 * <p>Sınıf ve metotları <b>package-private</b>'tır; yalnızca {@code pdf} paketi içinden
 * kullanılır. {@link AutoCloseable} arayüzünü uygular çünkü açık bir
 * {@link PDPageContentStream} kaynağı tutar ve iş bitince kapatılması gerekir.</p>
 */
final class PdfKalem implements AutoCloseable {

    /** Bezier eğrileriyle daire çizmek için kullanılan sabit: 4/3 * (√2 − 1). */
    private static final float DAIRE_KATSAYISI = 0.552284749831f;

    private final PDDocument belge;
    private final PDFont normalFont;
    private final PDFont kalinFont;

    private PDPageContentStream akis;
    private PDPage sayfa;
    private int sayfaSayisi;

    PdfKalem(PDDocument belge, PDFont normalFont, PDFont kalinFont) {
        this.belge = belge;
        this.normalFont = normalFont;
        this.kalinFont = kalinFont;
    }

    // ------------------------------------------------------------------
    // Sayfa yönetimi
    // ------------------------------------------------------------------

    /** Önceki sayfanın çizim akışını kapatır ve yeni bir A4 sayfa açar. */
    void yeniSayfa() throws IOException {
        if (akis != null) {
            akis.close();
        }
        sayfa = new PDPage(PDRectangle.A4);
        belge.addPage(sayfa);
        akis = new PDPageContentStream(belge, sayfa);
        sayfaSayisi++;
    }

    float sayfaGenisligi() {
        return sayfa.getMediaBox().getWidth();
    }

    float sayfaYuksekligi() {
        return sayfa.getMediaBox().getHeight();
    }

    int getSayfaSayisi() {
        return sayfaSayisi;
    }

    // ------------------------------------------------------------------
    // Metin
    // ------------------------------------------------------------------

    /** Metni sol alt köşesi (x, y) olacak şekilde yazar. */
    void yaz(String metin, float x, float y, boolean kalin, float boyut, Color renk) throws IOException {
        akis.beginText();
        akis.setFont(font(kalin), boyut);
        akis.setNonStrokingColor(renk);
        akis.newLineAtOffset(x, y);
        akis.showText(metin);
        akis.endText();
    }

    /** Metni sağ kenarı {@code sagX} olacak şekilde yazar. */
    void sagaYasliYaz(String metin, float sagX, float y, boolean kalin, float boyut, Color renk)
            throws IOException {
        yaz(metin, sagX - genislik(metin, kalin, boyut), y, kalin, boyut, renk);
    }

    /** Metnin verilen font ve boyuttaki genişliğini (punto) döndürür. */
    float genislik(String metin, boolean kalin, float boyut) throws IOException {
        return font(kalin).getStringWidth(metin) / 1000f * boyut;
    }

    /**
     * Uzun bir metni, kelimeleri bölmeden {@code maxGenislik} içine sığacak
     * satırlara ayırır (word wrap).
     */
    List<String> satirlaraBol(String metin, boolean kalin, float boyut, float maxGenislik)
            throws IOException {
        List<String> satirlar = new ArrayList<>();
        StringBuilder satir = new StringBuilder();
        for (String kelime : metin.split("\\s+")) {
            String aday = satir.isEmpty() ? kelime : satir + " " + kelime;
            if (genislik(aday, kalin, boyut) <= maxGenislik || satir.isEmpty()) {
                satir.setLength(0);
                satir.append(aday);
            } else {
                satirlar.add(satir.toString());
                satir.setLength(0);
                satir.append(kelime);
            }
        }
        if (!satir.isEmpty()) {
            satirlar.add(satir.toString());
        }
        return satirlar;
    }

    // ------------------------------------------------------------------
    // Şekiller
    // ------------------------------------------------------------------

    void dikdortgen(float x, float y, float genislik, float yukseklik, Color renk) throws IOException {
        akis.setNonStrokingColor(renk);
        akis.addRect(x, y, genislik, yukseklik);
        akis.fill();
    }

    void cizgi(float x1, float y1, float x2, float y2, float kalinlik, Color renk) throws IOException {
        akis.setStrokingColor(renk);
        akis.setLineWidth(kalinlik);
        akis.moveTo(x1, y1);
        akis.lineTo(x2, y2);
        akis.stroke();
    }

    void daire(float merkezX, float merkezY, float yaricap, Color renk) throws IOException {
        akis.setNonStrokingColor(renk);
        dairePatikasi(merkezX, merkezY, yaricap);
        akis.fill();
    }

    /** Resmi daire şeklinde kırparak çizer (profil fotoğrafı için). */
    void yuvarlakResim(PDImageXObject resim, float merkezX, float merkezY, float yaricap)
            throws IOException {
        akis.saveGraphicsState();
        dairePatikasi(merkezX, merkezY, yaricap);
        akis.clip();
        akis.drawImage(resim, merkezX - yaricap, merkezY - yaricap, 2 * yaricap, 2 * yaricap);
        akis.restoreGraphicsState();
    }

    // ------------------------------------------------------------------
    // Yaşam döngüsü
    // ------------------------------------------------------------------

    /** Açık çizim akışını kapatır. Birden fazla çağrılması güvenlidir. */
    @Override
    public void close() throws IOException {
        if (akis != null) {
            akis.close();
            akis = null;
        }
    }

    // ------------------------------------------------------------------
    // Sınıf içi yardımcılar (private)
    // ------------------------------------------------------------------

    private PDFont font(boolean kalin) {
        return kalin ? kalinFont : normalFont;
    }

    /** Dört Bezier eğrisinden oluşan bir daire patikası tanımlar. */
    private void dairePatikasi(float cx, float cy, float r) throws IOException {
        float k = r * DAIRE_KATSAYISI;
        akis.moveTo(cx + r, cy);
        akis.curveTo(cx + r, cy + k, cx + k, cy + r, cx, cy + r);
        akis.curveTo(cx - k, cy + r, cx - r, cy + k, cx - r, cy);
        akis.curveTo(cx - r, cy - k, cx - k, cy - r, cx, cy - r);
        akis.curveTo(cx + k, cy - r, cx + r, cy - k, cx + r, cy);
        akis.closePath();
    }
}
