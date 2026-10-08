package com.ozgecmis.veri;

import com.ozgecmis.model.Egitim;
import com.ozgecmis.model.IletisimBilgisi;
import com.ozgecmis.model.IsDeneyimi;
import com.ozgecmis.model.Kisi;
import com.ozgecmis.model.Ozgecmis;
import com.ozgecmis.model.YetenekSeviyesi;

import java.time.YearMonth;
import java.util.List;

/**
 * Özgeçmişte kullanılan örnek (hayali) verileri üretir.
 *
 * <p><b>Kendi bilgilerinizi girmek için yalnızca bu dosyayı düzenlemeniz yeterlidir.</b>
 * Model ve PDF katmanı veriden bağımsız olduğu için başka hiçbir sınıfa dokunmanız
 * gerekmez.</p>
 *
 * <p>Sınıf {@code final}'dır (kalıtım alınamaz) ve yapıcısı {@code private}'dır
 * (nesnesi oluşturulamaz). Yalnızca {@code static} bir fabrika metodu sunan
 * bir yardımcı sınıftır.</p>
 */
public final class OrnekOzgecmisVerisi {

    private OrnekOzgecmisVerisi() {
    }

    /** @return hayali kişisel bilgiler ve üç iş deneyimi içeren özgeçmiş */
    public static Ozgecmis olustur() {
        IletisimBilgisi iletisim = new IletisimBilgisi(
                "elif.kaya@example.com",
                "+90 555 123 45 67",
                "İstanbul, Türkiye",
                "linkedin.com/in/elifkaya",
                "github.com/elifkaya");

        Kisi kisi = new Kisi("Elif", "Kaya", "Junior Java Geliştirici",
                "foto/profil.jpg", iletisim);

        return Ozgecmis.builder()
                .kisi(kisi)
                .hakkimda("Bilgisayar mühendisliği öğrencisiyim. Nesneye yönelik programlama, "
                        + "temiz kod ve backend geliştirme alanlarında kendimi geliştiriyorum. "
                        + "Staj ve yarı zamanlı işlerimde Java ve Spring Boot ile REST servisleri "
                        + "geliştirdim; ekip içinde Git ile çalışma ve kod incelemesi deneyimi "
                        + "kazandım. Öğrenmeye açık, sorumluluk alan ve ekip çalışmasına yatkın biriyim.")

                // ---- Hayali iş deneyimleri (en yeniden eskiye) ----
                .isDeneyimi(new IsDeneyimi(
                        "Yarı Zamanlı Java Geliştirici",
                        "Nova Yazılım A.Ş.",
                        "İstanbul",
                        YearMonth.of(2025, 9),
                        null, // devam ediyor
                        List.of(
                                "Spring Boot ile sipariş takip sistemi için REST API uç noktaları geliştirdim.",
                                "JUnit 5 ile birim testleri yazarak modülün test kapsamını %45'ten %78'e çıkardım.",
                                "Haftalık kod incelemelerine katılarak temiz kod ve SOLID ilkelerini uyguladım.")))

                .isDeneyimi(new IsDeneyimi(
                        "Yazılım Geliştirme Stajyeri",
                        "Mavi Bulut Teknoloji",
                        "Ankara",
                        YearMonth.of(2025, 6),
                        YearMonth.of(2025, 8),
                        List.of(
                                "Stok yönetimi uygulamasında PostgreSQL sorgularını optimize ederek rapor süresini kısalttım.",
                                "Apache PDFBox kullanarak otomatik fatura PDF'i üreten bir modül hazırladım.",
                                "Scrum ekibinde günlük toplantılara ve sprint planlamalarına katıldım.")))

                .isDeneyimi(new IsDeneyimi(
                        "Teknik Destek Asistanı",
                        "Atlas Bilişim Hizmetleri",
                        "İzmir",
                        YearMonth.of(2024, 7),
                        YearMonth.of(2024, 9),
                        List.of(
                                "Kullanıcılardan gelen donanım ve yazılım destek taleplerini çözüme kavuşturdum.",
                                "Sık sorulan sorunlar için bir bilgi bankası dokümanı hazırladım.",
                                "Ofis ağındaki bilgisayarların kurulum ve güncellemelerini yaptım.")))

                // ---- Eğitim ----
                .egitim(new Egitim(
                        "Örnek Üniversitesi",
                        "Bilgisayar Mühendisliği",
                        "Lisans",
                        YearMonth.of(2023, 9),
                        null,
                        "3.42 / 4.00"))
                .egitim(new Egitim(
                        "Atatürk Anadolu Lisesi",
                        "Sayısal",
                        "Lise",
                        YearMonth.of(2019, 9),
                        YearMonth.of(2023, 6),
                        null))

                // ---- Yetenekler ----
                .yetenek("Java", YetenekSeviyesi.IYI)
                .yetenek("Spring Boot", YetenekSeviyesi.ORTA)
                .yetenek("SQL / PostgreSQL", YetenekSeviyesi.ORTA)
                .yetenek("Git & GitHub", YetenekSeviyesi.IYI)
                .yetenek("Python", YetenekSeviyesi.TEMEL)
                .yetenek("HTML / CSS", YetenekSeviyesi.ORTA)

                // ---- Diller ----
                .dil("Türkçe", "Anadil")
                .dil("İngilizce", "B2 – Orta üstü")
                .dil("Almanca", "A2 – Temel")

                // ---- Sertifikalar ----
                .sertifika("Oracle Certified Foundations Associate, Java (2025)")
                .sertifika("Git ve GitHub ile Versiyon Kontrolü – Online Kurs (2024)")

                // ---- İlgi alanları ----
                .ilgiAlani("Açık kaynak projeler")
                .ilgiAlani("Satranç")
                .ilgiAlani("Fotoğrafçılık")

                .olustur();
    }
}
