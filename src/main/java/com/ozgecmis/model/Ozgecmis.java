package com.ozgecmis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Bir özgeçmişin tamamını temsil eden ana model sınıfı.
 *
 * <p>Nesne, iç içe tanımlı {@link Builder} sınıfıyla adım adım oluşturulur
 * (<b>Builder tasarım deseni</b>). Yapıcı metot {@code private} olduğu için
 * {@code new Ozgecmis(...)} yazılamaz; nesne yalnızca {@code Ozgecmis.builder()}
 * üzerinden ve doğrulamadan geçerek oluşturulabilir.</p>
 *
 * <p>Oluşturulduktan sonra listeler değiştirilemez; dışarıdan bir deneyim
 * eklenip çıkarılamaz.</p>
 */
public class Ozgecmis {

    private final Kisi kisi;
    private final String hakkimda;
    private final List<IsDeneyimi> isDeneyimleri;
    private final List<Egitim> egitimler;
    private final List<Yetenek> yetenekler;
    private final List<Dil> diller;
    private final List<String> sertifikalar;
    private final List<String> ilgiAlanlari;

    /** Yalnızca {@link Builder#olustur()} çağırabilir. */
    private Ozgecmis(Builder b) {
        this.kisi = Dogrula.nullOlamaz(b.kisi, "Kişi");
        this.hakkimda = Dogrula.bosOlamaz(b.hakkimda, "Hakkımda");
        this.isDeneyimleri = List.copyOf(b.isDeneyimleri);
        this.egitimler = List.copyOf(b.egitimler);
        this.yetenekler = List.copyOf(b.yetenekler);
        this.diller = List.copyOf(b.diller);
        this.sertifikalar = List.copyOf(b.sertifikalar);
        this.ilgiAlanlari = List.copyOf(b.ilgiAlanlari);
    }

    /** Yeni bir özgeçmiş oluşturmaya başlar. */
    public static Builder builder() {
        return new Builder();
    }

    public Kisi getKisi() {
        return kisi;
    }

    public String getHakkimda() {
        return hakkimda;
    }

    public List<IsDeneyimi> getIsDeneyimleri() {
        return isDeneyimleri;
    }

    public List<Egitim> getEgitimler() {
        return egitimler;
    }

    public List<Yetenek> getYetenekler() {
        return yetenekler;
    }

    public List<Dil> getDiller() {
        return diller;
    }

    public List<String> getSertifikalar() {
        return sertifikalar;
    }

    public List<String> getIlgiAlanlari() {
        return ilgiAlanlari;
    }

    /**
     * {@link Ozgecmis} nesnesini adım adım kuran iç içe (static nested) sınıf.
     *
     * <p>Her metot {@code this} döndürdüğü için çağrılar zincirlenebilir:
     * {@code builder().kisi(k).hakkimda("...").isDeneyimi(d).olustur()}.</p>
     */
    public static class Builder {

        // Builder alanları private; Ozgecmis aynı dış sınıfta olduğu için erişebilir.
        private Kisi kisi;
        private String hakkimda;
        private final List<IsDeneyimi> isDeneyimleri = new ArrayList<>();
        private final List<Egitim> egitimler = new ArrayList<>();
        private final List<Yetenek> yetenekler = new ArrayList<>();
        private final List<Dil> diller = new ArrayList<>();
        private final List<String> sertifikalar = new ArrayList<>();
        private final List<String> ilgiAlanlari = new ArrayList<>();

        /** Dışarıdan {@code Ozgecmis.builder()} kullanılsın diye {@code private}. */
        private Builder() {
        }

        public Builder kisi(Kisi kisi) {
            this.kisi = kisi;
            return this;
        }

        public Builder hakkimda(String hakkimda) {
            this.hakkimda = hakkimda;
            return this;
        }

        public Builder isDeneyimi(IsDeneyimi deneyim) {
            isDeneyimleri.add(Dogrula.nullOlamaz(deneyim, "İş deneyimi"));
            return this;
        }

        public Builder egitim(Egitim egitim) {
            egitimler.add(Dogrula.nullOlamaz(egitim, "Eğitim"));
            return this;
        }

        public Builder yetenek(String ad, YetenekSeviyesi seviye) {
            yetenekler.add(new Yetenek(ad, seviye));
            return this;
        }

        public Builder dil(String ad, String seviye) {
            diller.add(new Dil(ad, seviye));
            return this;
        }

        public Builder sertifika(String sertifika) {
            sertifikalar.add(Dogrula.bosOlamaz(sertifika, "Sertifika"));
            return this;
        }

        public Builder ilgiAlani(String ilgiAlani) {
            ilgiAlanlari.add(Dogrula.bosOlamaz(ilgiAlani, "İlgi alanı"));
            return this;
        }

        /**
         * Girilen bilgileri doğrular ve değiştirilemez bir {@link Ozgecmis} döndürür.
         *
         * @throws IllegalStateException iş deneyimi veya eğitim eklenmemişse
         */
        public Ozgecmis olustur() {
            if (isDeneyimleri.isEmpty()) {
                throw new IllegalStateException("Özgeçmişte en az bir iş deneyimi olmalıdır.");
            }
            if (egitimler.isEmpty()) {
                throw new IllegalStateException("Özgeçmişte en az bir eğitim bilgisi olmalıdır.");
            }
            return new Ozgecmis(this);
        }
    }
}
