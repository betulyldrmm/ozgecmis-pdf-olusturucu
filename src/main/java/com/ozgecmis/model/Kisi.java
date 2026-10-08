package com.ozgecmis.model;

/**
 * Özgeçmiş sahibinin kişisel bilgilerini temsil eder.
 *
 * <p>{@link IletisimBilgisi} nesnesini bir alan olarak içerir. Bu
 * <b>bileşim (composition)</b> ilişkisidir: "Kişi'nin bir iletişim bilgisi vardır"
 * (has-a).</p>
 */
public class Kisi {

    private final String ad;
    private final String soyad;
    private final String unvan;
    private final String fotografYolu;
    private final IletisimBilgisi iletisim;

    /**
     * @param ad           kişinin adı
     * @param soyad        kişinin soyadı
     * @param unvan        meslek unvanı (ör. "Junior Java Geliştirici")
     * @param fotografYolu {@code src/main/resources} altındaki fotoğrafın yolu
     *                     (ör. "foto/profil.jpg")
     * @param iletisim     iletişim bilgileri
     */
    public Kisi(String ad, String soyad, String unvan, String fotografYolu,
                IletisimBilgisi iletisim) {
        this.ad = Dogrula.bosOlamaz(ad, "Ad");
        this.soyad = Dogrula.bosOlamaz(soyad, "Soyad");
        this.unvan = Dogrula.bosOlamaz(unvan, "Unvan");
        this.fotografYolu = Dogrula.bosOlamaz(fotografYolu, "Fotoğraf yolu");
        this.iletisim = Dogrula.nullOlamaz(iletisim, "İletişim bilgisi");
    }

    /** @return ad ve soyadın birleşimi (ör. "Elif Kaya") */
    public String getTamAd() {
        return ad + " " + soyad;
    }

    public String getAd() {
        return ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public String getUnvan() {
        return unvan;
    }

    public String getFotografYolu() {
        return fotografYolu;
    }

    public IletisimBilgisi getIletisim() {
        return iletisim;
    }

    @Override
    public String toString() {
        return getTamAd() + " (" + unvan + ")";
    }
}
