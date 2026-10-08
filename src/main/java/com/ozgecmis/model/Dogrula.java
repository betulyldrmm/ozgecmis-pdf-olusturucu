package com.ozgecmis.model;

/**
 * Model sınıflarının ortak kullandığı küçük doğrulama yardımcıları.
 *
 * <p>Sınıf bilinçli olarak <b>erişim belirleyicisiz (default / package-private)</b>
 * tanımlanmıştır: yalnızca {@code com.ozgecmis.model} paketindeki sınıflar
 * kullanabilir. Paket dışına açılması gereken bir API değildir.</p>
 */
final class Dogrula {

    /** Yardımcı (utility) sınıftır; nesnesi oluşturulamaz. */
    private Dogrula() {
    }

    /**
     * Metnin {@code null} veya boş olmadığını kontrol eder.
     *
     * @return kenar boşlukları temizlenmiş metin
     * @throws IllegalArgumentException metin boşsa
     */
    static String bosOlamaz(String deger, String alanAdi) {
        if (deger == null || deger.isBlank()) {
            throw new IllegalArgumentException(alanAdi + " boş olamaz.");
        }
        return deger.strip();
    }

    /** Değerin {@code null} olmadığını kontrol eder. */
    static <T> T nullOlamaz(T deger, String alanAdi) {
        if (deger == null) {
            throw new IllegalArgumentException(alanAdi + " null olamaz.");
        }
        return deger;
    }

    /** İsteğe bağlı metinleri temizler; boşsa {@code null} döner. */
    static String istegeBagli(String deger) {
        return (deger == null || deger.isBlank()) ? null : deger.strip();
    }
}
