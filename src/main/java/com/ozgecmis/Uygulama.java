package com.ozgecmis;

import com.ozgecmis.model.IsDeneyimi;
import com.ozgecmis.model.Ozgecmis;
import com.ozgecmis.pdf.OzgecmisPdfOlusturucu;
import com.ozgecmis.veri.OrnekOzgecmisVerisi;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

/**
 * Uygulamanın giriş noktası.
 *
 * <p>{@code main} metodu yalnızca akışı yönetir: veriyi alır, PDF oluşturucu nesnesini
 * oluşturur ve işi ona devreder. PDF'in nasıl çizildiği {@link OzgecmisPdfOlusturucu}
 * sınıfının sorumluluğundadır.</p>
 *
 * <p>Kullanım: {@code java -jar ozgecmis.jar [çıktı-dosyası.pdf]}</p>
 */
public class Uygulama {

    private static final Path VARSAYILAN_CIKTI = Path.of("cikti", "ozgecmis.pdf");

    public static void main(String[] args) {
        Path ciktiYolu = args.length > 0 ? Path.of(args[0]) : VARSAYILAN_CIKTI;

        System.out.println("[1/3] Özgeçmiş verileri hazırlanıyor...");
        Ozgecmis ozgecmis = OrnekOzgecmisVerisi.olustur();
        System.out.println("      Kişi        : " + ozgecmis.getKisi());
        for (IsDeneyimi deneyim : ozgecmis.getIsDeneyimleri()) {
            System.out.println("      İş deneyimi : " + deneyim);
        }

        System.out.println("[2/3] PDF oluşturuluyor...");
        // try-with-resources: blok bitince pdf.close() otomatik çağrılır.
        try (OzgecmisPdfOlusturucu pdf = new OzgecmisPdfOlusturucu(ciktiYolu)) {
            Path kaydedilen = pdf.olustur(ozgecmis);
            System.out.println("[3/3] Tamamlandı: " + kaydedilen.toAbsolutePath()
                    + " (" + pdf.getSayfaSayisi() + " sayfa)");
        } catch (IOException | UncheckedIOException e) {
            System.err.println("PDF oluşturulamadı: " + e.getMessage());
            System.exit(1);
        }
        // Bu noktada pdf değişkeninin kapsamı bitti; nesne kapatıldı ve
        // çöp toplayıcı tarafından toplanmaya uygun hâle geldi.
    }
}
