package com.ozgecmis.model;

/**
 * Bir teknik yeteneği ve seviyesini tutar (ör. "Java – İyi").
 */
public class Yetenek {

    private final String ad;
    private final YetenekSeviyesi seviye;

    public Yetenek(String ad, YetenekSeviyesi seviye) {
        this.ad = Dogrula.bosOlamaz(ad, "Yetenek adı");
        this.seviye = Dogrula.nullOlamaz(seviye, "Yetenek seviyesi");
    }

    public String getAd() {
        return ad;
    }

    public YetenekSeviyesi getSeviye() {
        return seviye;
    }

    @Override
    public String toString() {
        return ad + " (" + seviye.getEtiket() + ")";
    }
}
