package com.ozgecmis.model;

/**
 * Bir yeteneğin ustalık seviyesini 1–5 arası puanla ifade eden sabitler kümesi.
 *
 * <p>{@code enum} kullanmak, seviyelerin rastgele bir sayı veya yazım hatalı bir
 * metin yerine yalnızca bu beş değerden biri olmasını derleme aşamasında garanti eder.</p>
 */
public enum YetenekSeviyesi {

    BASLANGIC(1, "Başlangıç"),
    TEMEL(2, "Temel"),
    ORTA(3, "Orta"),
    IYI(4, "İyi"),
    UZMAN(5, "Uzman");

    private final int puan;
    private final String etiket;

    /** Enum yapıcıları her zaman örtük olarak {@code private}'dır. */
    YetenekSeviyesi(int puan, String etiket) {
        this.puan = puan;
        this.etiket = etiket;
    }

    /** @return 1 (en düşük) ile 5 (en yüksek) arasında puan */
    public int getPuan() {
        return puan;
    }

    /** @return seviyenin Türkçe adı */
    public String getEtiket() {
        return etiket;
    }
}
