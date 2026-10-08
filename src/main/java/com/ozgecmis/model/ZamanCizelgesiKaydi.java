package com.ozgecmis.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Özgeçmişte başlangıç–bitiş tarihi olan her kaydın (iş deneyimi, eğitim)
 * ortak <b>soyut üst sınıfı</b>.
 *
 * <p>Kalıtım (inheritance) ve çok biçimlilik (polymorphism) burada devreye girer:
 * PDF oluşturucu {@link IsDeneyimi} ile {@link Egitim} arasındaki farkı bilmeden,
 * her ikisini de bu sınıfın metotları üzerinden aynı şekilde çizer.</p>
 *
 * <p>Tarih alanları {@code protected} tanımlanmıştır: alt sınıflar doğrudan
 * erişebilir, paket dışındaki diğer sınıflar erişemez.</p>
 */
public abstract class ZamanCizelgesiKaydi {

    private static final DateTimeFormatter AY_YIL =
            DateTimeFormatter.ofPattern("MMM yyyy", Locale.forLanguageTag("tr-TR"));

    /** Kaydın başladığı ay. Alt sınıflar erişebilsin diye {@code protected}. */
    protected final YearMonth baslangic;

    /** Kaydın bittiği ay; {@code null} ise kayıt hâlâ devam ediyordur. */
    protected final YearMonth bitis;

    /**
     * Soyut sınıfın yapıcısı {@code protected}'dır: yalnızca alt sınıflar
     * {@code super(...)} ile çağırabilir. Alt sınıfın nesnesi oluşturulurken
     * <b>önce bu yapıcı</b>, sonra alt sınıfın yapıcısının geri kalanı çalışır.
     */
    protected ZamanCizelgesiKaydi(YearMonth baslangic, YearMonth bitis) {
        this.baslangic = Dogrula.nullOlamaz(baslangic, "Başlangıç tarihi");
        if (bitis != null && bitis.isBefore(baslangic)) {
            throw new IllegalArgumentException("Bitiş tarihi başlangıçtan önce olamaz.");
        }
        this.bitis = bitis;
    }

    /** @return kartın ana başlığı (ör. pozisyon adı veya bölüm adı) */
    public abstract String getBaslik();

    /** @return başlığın altındaki satır (ör. şirket adı veya okul adı) */
    public abstract String getAltBaslik();

    /** @return madde işaretli açıklamalar; yoksa boş liste */
    public abstract List<String> getMaddeler();

    /** @return "Haz 2024 – Devam ediyor" biçiminde tarih aralığı */
    public String getTarihAraligi() {
        String bas = buyukHarfleBaslat(baslangic.format(AY_YIL));
        String son = devamEdiyorMu() ? "Devam ediyor" : buyukHarfleBaslat(bitis.format(AY_YIL));
        return bas + " – " + son;
    }

    /** @return bitiş tarihi tanımlı değilse {@code true} */
    public boolean devamEdiyorMu() {
        return bitis == null;
    }

    public YearMonth getBaslangic() {
        return baslangic;
    }

    /**
     * Kaydın kaç ay sürdüğünü hesaplar. Devam eden kayıtlar için bugüne kadar sayar.
     * Alt sınıfların kullanabilmesi için {@code protected} bırakılmıştır.
     */
    protected long sureyiAyOlarakHesapla() {
        YearMonth son = devamEdiyorMu() ? YearMonth.now() : bitis;
        return baslangic.until(son, java.time.temporal.ChronoUnit.MONTHS) + 1;
    }

    /** Sınıf içi yardımcı metot; dışarıya açılmasına gerek olmadığı için {@code private}. */
    private static String buyukHarfleBaslat(String metin) {
        if (metin.isEmpty()) {
            return metin;
        }
        return metin.substring(0, 1).toUpperCase(Locale.forLanguageTag("tr-TR")) + metin.substring(1);
    }
}
