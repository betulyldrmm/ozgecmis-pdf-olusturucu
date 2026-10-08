package com.ozgecmis.pdf;

import com.ozgecmis.model.Dil;
import com.ozgecmis.model.IletisimBilgisi;
import com.ozgecmis.model.Kisi;
import com.ozgecmis.model.Ozgecmis;
import com.ozgecmis.model.Yetenek;
import com.ozgecmis.model.ZamanCizelgesiKaydi;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Bir {@link Ozgecmis} nesnesini iki sütunlu, fotoğraflı bir A4 PDF belgesine dönüştürür.
 *
 * <h2>Nesne yaşam döngüsü</h2>
 * <ol>
 *   <li><b>Oluşturma:</b> Yapıcı metot boş bir {@link PDDocument} açar ve yazı tiplerini yükler.</li>
 *   <li><b>Kullanım:</b> {@link #olustur(Ozgecmis)} sayfaları çizer ve dosyayı kaydeder.</li>
 *   <li><b>Kapatma:</b> {@link #close()} PDF kaynaklarını serbest bırakır.</li>
 *   <li><b>Yok edilme:</b> Nesneye referans kalmayınca çöp toplayıcı (GC) belleği geri alır.</li>
 * </ol>
 *
 * <p>Sınıf {@link AutoCloseable} uyguladığı için <i>try-with-resources</i> bloğunda
 * kullanılır; blok bittiğinde (hata olsa bile) {@code close()} otomatik çağrılır:</p>
 *
 * <pre>{@code
 * try (OzgecmisPdfOlusturucu pdf = new OzgecmisPdfOlusturucu(Path.of("ozgecmis.pdf"))) {
 *     pdf.olustur(ozgecmis);
 * }
 * }</pre>
 */
public class OzgecmisPdfOlusturucu implements AutoCloseable {

    private static final Locale TR = Locale.forLanguageTag("tr-TR");
    private static final String NORMAL_FONT = "/fonts/DejaVuSans.ttf";
    private static final String KALIN_FONT = "/fonts/DejaVuSans-Bold.ttf";

    private final Path ciktiYolu;
    private final PDDocument belge;
    private final PdfKalem kalem;

    /** Ana (sağ) sütunda bir sonraki öğenin çizileceği dikey konum. */
    private float imlecY;
    private boolean kapatildi;

    /**
     * PDF belgesini hazırlar ve Türkçe karakterleri destekleyen yazı tiplerini yükler.
     *
     * @param ciktiYolu PDF'in kaydedileceği dosya yolu
     * @throws IOException yazı tipi dosyaları okunamazsa
     */
    public OzgecmisPdfOlusturucu(Path ciktiYolu) throws IOException {
        this.ciktiYolu = Objects.requireNonNull(ciktiYolu, "Çıktı yolu null olamaz.");
        this.belge = new PDDocument();
        try {
            PDType0Font normal = fontYukle(NORMAL_FONT);
            PDType0Font kalin = fontYukle(KALIN_FONT);
            this.kalem = new PdfKalem(belge, normal, kalin);
        } catch (IOException | RuntimeException e) {
            // Yapıcı yarıda kalırsa nesne hiç kullanılamaz; açılan belgeyi burada kapatırız.
            belge.close();
            throw e;
        }
    }

    /**
     * Özgeçmişi çizer ve PDF dosyasını diske kaydeder.
     *
     * @return kaydedilen dosyanın yolu
     * @throws IOException          dosya yazılamazsa veya fotoğraf okunamazsa
     * @throws IllegalStateException nesne daha önce kapatılmışsa
     */
    public Path olustur(Ozgecmis ozgecmis) throws IOException {
        if (kapatildi) {
            throw new IllegalStateException("Bu PDF oluşturucu kapatılmış; yeni bir nesne oluşturun.");
        }
        Objects.requireNonNull(ozgecmis, "Özgeçmiş null olamaz.");

        belgeBilgileriniAyarla(ozgecmis.getKisi());

        sayfaBaslat();
        kenarCubugunuCiz(ozgecmis);

        baslikCiz(ozgecmis.getKisi());
        bolumBasligiCiz("Hakkımda");
        paragrafCiz(ozgecmis.getHakkimda(), Tema.BOYUT_NORMAL, Tema.METIN);

        bolumBasligiCiz("İş Deneyimi");
        ozgecmis.getIsDeneyimleri().forEach(this::kayitCizSarili);

        bolumBasligiCiz("Eğitim");
        ozgecmis.getEgitimler().forEach(this::kayitCizSarili);

        if (!ozgecmis.getSertifikalar().isEmpty()) {
            bolumBasligiCiz("Sertifikalar");
            for (String sertifika : ozgecmis.getSertifikalar()) {
                maddeCiz(sertifika);
            }
        }

        kalem.close(); // son sayfanın çizim akışını kaydetmeden önce kapat
        Path ustKlasor = ciktiYolu.toAbsolutePath().getParent();
        if (ustKlasor != null) {
            Files.createDirectories(ustKlasor);
        }
        belge.save(ciktiYolu.toFile());
        return ciktiYolu;
    }

    /** @return oluşturulan belgedeki sayfa sayısı */
    public int getSayfaSayisi() {
        return kalem.getSayfaSayisi();
    }

    /**
     * PDF belgesinin tuttuğu bellek ve dosya kaynaklarını serbest bırakır.
     * Birden fazla çağrılması güvenlidir (idempotent).
     */
    @Override
    public void close() throws IOException {
        if (kapatildi) {
            return;
        }
        kapatildi = true;
        try {
            kalem.close();
        } finally {
            belge.close();
        }
    }

    // ==================================================================
    // Sol kenar çubuğu: fotoğraf, iletişim, yetenekler, diller, ilgi alanları
    // ==================================================================

    private void kenarCubugunuCiz(Ozgecmis ozgecmis) throws IOException {
        Kisi kisi = ozgecmis.getKisi();
        float merkezX = Tema.KENAR_GENISLIK / 2f;
        float yaricap = Tema.FOTOGRAF_YARICAP;
        float merkezY = kalem.sayfaYuksekligi() - Tema.UST_BOSLUK - yaricap + 6;

        kalem.daire(merkezX, merkezY, yaricap + 4, Tema.VURGU);
        kalem.yuvarlakResim(fotografYukle(kisi.getFotografYolu()), merkezX, merkezY, yaricap);

        float x = Tema.KENAR_IC_BOSLUK;
        float genislik = Tema.KENAR_GENISLIK - 2 * Tema.KENAR_IC_BOSLUK;
        float y = merkezY - yaricap - 36;

        // İletişim
        y = kenarBaslikCiz("İletişim", x, y, genislik);
        IletisimBilgisi iletisim = kisi.getIletisim();
        y = kenarBilgiCiz("E-posta", iletisim.getEposta(), x, y, genislik);
        y = kenarBilgiCiz("Telefon", iletisim.getTelefon(), x, y, genislik);
        y = kenarBilgiCiz("Konum", iletisim.getKonum(), x, y, genislik);
        if (iletisim.getLinkedin() != null) {
            y = kenarBilgiCiz("LinkedIn", iletisim.getLinkedin(), x, y, genislik);
        }
        if (iletisim.getGithub() != null) {
            y = kenarBilgiCiz("GitHub", iletisim.getGithub(), x, y, genislik);
        }

        // Yetenekler
        if (!ozgecmis.getYetenekler().isEmpty()) {
            y = kenarBaslikCiz("Yetenekler", x, y - 8, genislik);
            for (Yetenek yetenek : ozgecmis.getYetenekler()) {
                y = yetenekCiz(yetenek, x, y, genislik);
            }
        }

        // Diller
        if (!ozgecmis.getDiller().isEmpty()) {
            y = kenarBaslikCiz("Diller", x, y - 8, genislik);
            for (Dil dil : ozgecmis.getDiller()) {
                kalem.yaz(dil.getAd(), x, y, true, Tema.BOYUT_KUCUK, Tema.KENAR_METIN);
                kalem.yaz(dil.getSeviye(), x, y - 11, false, Tema.BOYUT_ETIKET + 0.5f, Tema.KENAR_SOLUK);
                y -= 26;
            }
        }

        // İlgi alanları
        if (!ozgecmis.getIlgiAlanlari().isEmpty()) {
            y = kenarBaslikCiz("İlgi Alanları", x, y - 8, genislik);
            for (String ilgi : ozgecmis.getIlgiAlanlari()) {
                kalem.daire(x + 2, y + 2.6f, 1.8f, Tema.VURGU);
                kalem.yaz(ilgi, x + 10, y, false, Tema.BOYUT_KUCUK, Tema.KENAR_METIN);
                y -= 14;
            }
        }
    }

    private float kenarBaslikCiz(String baslik, float x, float y, float genislik) throws IOException {
        kalem.yaz(baslik.toUpperCase(TR), x, y, true, Tema.BOYUT_BOLUM - 1, Tema.KENAR_METIN);
        kalem.dikdortgen(x, y - 7, 24, 2, Tema.VURGU);
        return y - 24;
    }

    private float kenarBilgiCiz(String etiket, String deger, float x, float y, float genislik)
            throws IOException {
        kalem.yaz(etiket.toUpperCase(TR), x, y, true, Tema.BOYUT_ETIKET, Tema.KENAR_SOLUK);
        y -= 11;
        for (String satir : kalem.satirlaraBol(deger, false, Tema.BOYUT_KUCUK, genislik)) {
            kalem.yaz(satir, x, y, false, Tema.BOYUT_KUCUK, Tema.KENAR_METIN);
            y -= Tema.BOYUT_KUCUK * Tema.SATIR_ARALIGI;
        }
        return y - 6;
    }

    /** Yetenek adını solda, 1–5 arası seviyesini sağda nokta olarak çizer. */
    private float yetenekCiz(Yetenek yetenek, float x, float y, float genislik) throws IOException {
        kalem.yaz(yetenek.getAd(), x, y, false, Tema.BOYUT_KUCUK, Tema.KENAR_METIN);
        float noktaAraligi = 8.5f;
        float ilkNoktaX = x + genislik - 4 * noktaAraligi - 3;
        for (int i = 1; i <= 5; i++) {
            Color renk = i <= yetenek.getSeviye().getPuan() ? Tema.VURGU : Tema.KENAR_PASIF;
            kalem.daire(ilkNoktaX + (i - 1) * noktaAraligi, y + 2.8f, 3f, renk);
        }
        return y - 16;
    }

    // ==================================================================
    // Sağ ana sütun: ad-unvan, hakkımda, deneyim, eğitim, sertifikalar
    // ==================================================================

    private void baslikCiz(Kisi kisi) throws IOException {
        float x = anaSolX();
        imlecY = kalem.sayfaYuksekligi() - Tema.UST_BOSLUK - Tema.BOYUT_AD + 6;
        kalem.yaz(kisi.getTamAd().toUpperCase(TR), x, imlecY, true, Tema.BOYUT_AD, Tema.METIN);
        imlecY -= Tema.BOYUT_UNVAN + 10;
        kalem.yaz(kisi.getUnvan(), x, imlecY, false, Tema.BOYUT_UNVAN, Tema.VURGU_KOYU);
        imlecY -= 18;
        kalem.dikdortgen(x, imlecY, anaGenislik(), 1.2f, Tema.AYIRAC);
        imlecY -= 8;
    }

    private void bolumBasligiCiz(String baslik) throws IOException {
        alanAyir(48); // başlık sayfanın en altında yalnız kalmasın
        imlecY -= 16;
        float x = anaSolX();
        kalem.dikdortgen(x, imlecY - 1, 3, Tema.BOYUT_BOLUM, Tema.VURGU);
        kalem.yaz(baslik.toUpperCase(TR), x + 9, imlecY, true, Tema.BOYUT_BOLUM, Tema.METIN);
        imlecY -= Tema.BOYUT_BOLUM + 9;
    }

    private void paragrafCiz(String metin, float boyut, Color renk) throws IOException {
        float satirYuksekligi = boyut * Tema.SATIR_ARALIGI;
        for (String satir : kalem.satirlaraBol(metin, false, boyut, anaGenislik())) {
            alanAyir(satirYuksekligi);
            kalem.yaz(satir, anaSolX(), imlecY, false, boyut, renk);
            imlecY -= satirYuksekligi;
        }
    }

    /**
     * İş deneyimi ve eğitim kayıtlarını <b>aynı metotla</b> çizer.
     * Parametre tipi soyut üst sınıf olduğu için hangi alt sınıfın geldiği önemli
     * değildir; doğru {@code getBaslik()} çalışma zamanında seçilir (çok biçimlilik).
     */
    private void kayitCiz(ZamanCizelgesiKaydi kayit) throws IOException {
        float x = anaSolX();
        String tarih = kayit.getTarihAraligi();
        float tarihGenisligi = kalem.genislik(tarih, false, Tema.BOYUT_KUCUK);

        List<String> baslikSatirlari = kalem.satirlaraBol(kayit.getBaslik(), true,
                Tema.BOYUT_KAYIT_BASLIK, anaGenislik() - tarihGenisligi - 12);
        alanAyir(baslikSatirlari.size() * 14 + 30); // başlık + alt başlık + ilk madde birlikte kalsın

        kalem.sagaYasliYaz(tarih, x + anaGenislik(), imlecY, false, Tema.BOYUT_KUCUK, Tema.METIN_SOLUK);
        for (String satir : baslikSatirlari) {
            kalem.yaz(satir, x, imlecY, true, Tema.BOYUT_KAYIT_BASLIK, Tema.METIN);
            imlecY -= 14;
        }
        kalem.yaz(kayit.getAltBaslik(), x, imlecY, false, Tema.BOYUT_NORMAL, Tema.VURGU_KOYU);
        imlecY -= 14;

        for (String madde : kayit.getMaddeler()) {
            maddeCiz(madde);
        }
        imlecY -= 7;
    }

    /** {@code forEach} içinde kullanılabilmesi için kontrollü istisnayı sarmalar. */
    private void kayitCizSarili(ZamanCizelgesiKaydi kayit) {
        try {
            kayitCiz(kayit);
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    private void maddeCiz(String metin) throws IOException {
        float x = anaSolX();
        float girinti = 11;
        float satirYuksekligi = Tema.BOYUT_NORMAL * Tema.SATIR_ARALIGI;
        List<String> satirlar = kalem.satirlaraBol(metin, false, Tema.BOYUT_NORMAL, anaGenislik() - girinti);
        for (int i = 0; i < satirlar.size(); i++) {
            alanAyir(satirYuksekligi);
            if (i == 0) {
                kalem.daire(x + 2.5f, imlecY + 3f, 1.7f, Tema.VURGU);
            }
            kalem.yaz(satirlar.get(i), x + girinti, imlecY, false, Tema.BOYUT_NORMAL, Tema.METIN);
            imlecY -= satirYuksekligi;
        }
        imlecY -= 1.5f;
    }

    // ==================================================================
    // Sayfa ve kaynak yardımcıları
    // ==================================================================

    /** Yeni sayfa açar ve sol kenar çubuğunun arka planını boyar. */
    private void sayfaBaslat() throws IOException {
        kalem.yeniSayfa();
        kalem.dikdortgen(0, 0, Tema.KENAR_GENISLIK, kalem.sayfaYuksekligi(), Tema.KENAR_ARKAPLAN);
        imlecY = kalem.sayfaYuksekligi() - Tema.UST_BOSLUK;
    }

    /** İstenen yükseklik sayfaya sığmıyorsa yeni sayfaya geçer. */
    private void alanAyir(float yukseklik) throws IOException {
        if (imlecY - yukseklik < Tema.ALT_BOSLUK) {
            sayfaBaslat();
        }
    }

    private float anaSolX() {
        return Tema.KENAR_GENISLIK + Tema.ANA_SOL_BOSLUK;
    }

    private float anaGenislik() {
        return kalem.sayfaGenisligi() - anaSolX() - Tema.ANA_SAG_BOSLUK;
    }

    private void belgeBilgileriniAyarla(Kisi kisi) {
        PDDocumentInformation bilgi = belge.getDocumentInformation();
        bilgi.setTitle(kisi.getTamAd() + " – Özgeçmiş");
        bilgi.setAuthor(kisi.getTamAd());
        bilgi.setSubject("Özgeçmiş");
        bilgi.setCreator("Özgeçmiş PDF Oluşturucu (Java + Apache PDFBox)");
        bilgi.setCreationDate(Calendar.getInstance());
    }

    private PDType0Font fontYukle(String kaynakYolu) throws IOException {
        try (InputStream girdi = kaynakAc(kaynakYolu)) {
            return PDType0Font.load(belge, girdi);
        }
    }

    /**
     * Profil fotoğrafını yükler, ortasından kare olarak kırpar ve PDF'e uygun hâle getirir.
     * Yol önce diskte, bulunamazsa {@code src/main/resources} içinde aranır.
     */
    private PDImageXObject fotografYukle(String yol) throws IOException {
        BufferedImage orijinal;
        Path dosya = Path.of(yol);
        if (Files.isRegularFile(dosya)) {
            orijinal = ImageIO.read(dosya.toFile());
        } else {
            try (InputStream girdi = kaynakAc("/" + yol)) {
                orijinal = ImageIO.read(girdi);
            }
        }
        if (orijinal == null) {
            throw new IOException("Fotoğraf okunamadı (desteklenen biçimler: JPG, PNG): " + yol);
        }
        return JPEGFactory.createFromImage(belge, kareyeKirp(orijinal, 480), 0.92f);
    }

    /** Resmi ortadan kare kırpar, istenen boyuta ölçekler ve RGB'ye çevirir. */
    private static BufferedImage kareyeKirp(BufferedImage resim, int hedefBoyut) {
        int kenar = Math.min(resim.getWidth(), resim.getHeight());
        int x = (resim.getWidth() - kenar) / 2;
        // Portre fotoğraflarda yüz genelde üstte olduğu için dikeyde üst kısmı tercih et.
        int y = Math.max(0, (resim.getHeight() - kenar) / 4);
        BufferedImage kare = new BufferedImage(hedefBoyut, hedefBoyut, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = kare.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, hedefBoyut, hedefBoyut);
            g.drawImage(resim, 0, 0, hedefBoyut, hedefBoyut, x, y, x + kenar, y + kenar, null);
        } finally {
            g.dispose(); // grafik bağlamı da bir kaynaktır; işi bitince serbest bırakılır
        }
        return kare;
    }

    private static InputStream kaynakAc(String kaynakYolu) throws IOException {
        InputStream girdi = OzgecmisPdfOlusturucu.class.getResourceAsStream(kaynakYolu);
        if (girdi == null) {
            throw new IOException("Kaynak dosya bulunamadı: src/main/resources" + kaynakYolu);
        }
        return girdi;
    }
}
