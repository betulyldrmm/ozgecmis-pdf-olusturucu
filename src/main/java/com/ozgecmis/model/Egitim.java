package com.ozgecmis.model;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Bir eğitim kaydını (lise, lisans vb.) temsil eder.
 *
 * <p>{@link IsDeneyimi} gibi {@link ZamanCizelgesiKaydi}'ndan türer; böylece
 * PDF tarafında ikisi de aynı kod ile çizilebilir (çok biçimlilik).</p>
 */
public class Egitim extends ZamanCizelgesiKaydi {

    private final String okul;
    private final String bolum;
    private final String derece;
    private final String notOrtalamasi;

    /**
     * @param okul          okul adı
     * @param bolum         bölüm adı
     * @param derece        derece (ör. "Lisans")
     * @param baslangic     başlangıç ayı
     * @param bitis         mezuniyet ayı; öğrenim sürüyorsa {@code null}
     * @param notOrtalamasi ör. "3.42 / 4.00"; isteğe bağlı
     */
    public Egitim(String okul, String bolum, String derece,
                  YearMonth baslangic, YearMonth bitis, String notOrtalamasi) {
        super(baslangic, bitis);
        this.okul = Dogrula.bosOlamaz(okul, "Okul");
        this.bolum = Dogrula.bosOlamaz(bolum, "Bölüm");
        this.derece = Dogrula.bosOlamaz(derece, "Derece");
        this.notOrtalamasi = Dogrula.istegeBagli(notOrtalamasi);
    }

    @Override
    public String getBaslik() {
        return bolum + " (" + derece + ")";
    }

    @Override
    public String getAltBaslik() {
        return okul;
    }

    @Override
    public List<String> getMaddeler() {
        List<String> maddeler = new ArrayList<>();
        if (notOrtalamasi != null) {
            maddeler.add("Genel not ortalaması: " + notOrtalamasi);
        }
        return List.copyOf(maddeler);
    }

    public String getOkul() {
        return okul;
    }

    public String getBolum() {
        return bolum;
    }

    public String getDerece() {
        return derece;
    }

    @Override
    public String toString() {
        return getBaslik() + " – " + okul;
    }
}
