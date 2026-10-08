package com.ozgecmis.model;

/**
 * Bilinen bir yabancı dili ve seviyesini tutar (ör. "İngilizce – B2").
 */
public class Dil {

    private final String ad;
    private final String seviye;

    /**
     * @param ad     dilin adı
     * @param seviye ör. "Anadil", "B2 – Orta üstü"
     */
    public Dil(String ad, String seviye) {
        this.ad = Dogrula.bosOlamaz(ad, "Dil adı");
        this.seviye = Dogrula.bosOlamaz(seviye, "Dil seviyesi");
    }

    public String getAd() {
        return ad;
    }

    public String getSeviye() {
        return seviye;
    }

    @Override
    public String toString() {
        return ad + " – " + seviye;
    }
}
