package com.ozgecmis.model;

import java.time.YearMonth;
import java.util.List;

/**
 * Tek bir iş deneyimini temsil eder.
 *
 * <p>{@link ZamanCizelgesiKaydi} sınıfından kalıtım alır (is-a ilişkisi:
 * "İş deneyimi bir zaman çizelgesi kaydıdır").</p>
 */
public class IsDeneyimi extends ZamanCizelgesiKaydi {

    private final String pozisyon;
    private final String sirket;
    private final String sehir;
    private final List<String> sorumluluklar;

    /**
     * @param pozisyon      çalışılan pozisyon
     * @param sirket        şirket adı
     * @param sehir         çalışılan şehir
     * @param baslangic     başlangıç ayı
     * @param bitis         bitiş ayı; hâlâ çalışılıyorsa {@code null}
     * @param sorumluluklar yapılan işler (madde madde)
     */
    public IsDeneyimi(String pozisyon, String sirket, String sehir,
                      YearMonth baslangic, YearMonth bitis, List<String> sorumluluklar) {
        super(baslangic, bitis); // önce üst sınıfın yapıcısı çalışır
        this.pozisyon = Dogrula.bosOlamaz(pozisyon, "Pozisyon");
        this.sirket = Dogrula.bosOlamaz(sirket, "Şirket");
        this.sehir = Dogrula.bosOlamaz(sehir, "Şehir");
        // List.copyOf: dışarıdan gelen listenin değiştirilemez bir kopyasını alır
        // (savunmacı kopya / defensive copy).
        this.sorumluluklar = List.copyOf(Dogrula.nullOlamaz(sorumluluklar, "Sorumluluklar"));
    }

    @Override
    public String getBaslik() {
        return pozisyon;
    }

    @Override
    public String getAltBaslik() {
        return sirket + " · " + sehir;
    }

    @Override
    public List<String> getMaddeler() {
        return sorumluluklar;
    }

    public String getPozisyon() {
        return pozisyon;
    }

    public String getSirket() {
        return sirket;
    }

    public String getSehir() {
        return sehir;
    }

    /** Üst sınıftaki {@code protected} metodu alt sınıftan kullanma örneği. */
    public long getSureAy() {
        return sureyiAyOlarakHesapla();
    }

    @Override
    public String toString() {
        return pozisyon + " @ " + sirket + " (" + getTarihAraligi() + ")";
    }
}
