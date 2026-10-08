package com.ozgecmis.model;

/**
 * Bir kişinin iletişim bilgilerini tutar.
 *
 * <p>Tüm alanlar {@code private final} olduğu için nesne oluşturulduktan sonra
 * değiştirilemez (<i>immutable</i>). Dışarıya yalnızca okuma (getter) metotları açılır;
 * bu, kapsülleme (encapsulation) ilkesinin doğrudan uygulamasıdır.</p>
 */
public class IletisimBilgisi {

    private final String eposta;
    private final String telefon;
    private final String konum;
    private final String linkedin;
    private final String github;

    /**
     * Zorunlu ve isteğe bağlı tüm alanlarla iletişim bilgisi oluşturur.
     *
     * @param eposta   zorunlu
     * @param telefon  zorunlu
     * @param konum    zorunlu (ör. "İstanbul, Türkiye")
     * @param linkedin isteğe bağlı, {@code null} olabilir
     * @param github   isteğe bağlı, {@code null} olabilir
     */
    public IletisimBilgisi(String eposta, String telefon, String konum,
                           String linkedin, String github) {
        this.eposta = Dogrula.bosOlamaz(eposta, "E-posta");
        this.telefon = Dogrula.bosOlamaz(telefon, "Telefon");
        this.konum = Dogrula.bosOlamaz(konum, "Konum");
        this.linkedin = Dogrula.istegeBagli(linkedin);
        this.github = Dogrula.istegeBagli(github);
    }

    /**
     * Yalnızca zorunlu alanlarla oluşturan aşırı yüklenmiş (overloaded) yapıcı.
     * İşi {@code this(...)} ile ana yapıcıya devreder (constructor chaining).
     */
    public IletisimBilgisi(String eposta, String telefon, String konum) {
        this(eposta, telefon, konum, null, null);
    }

    public String getEposta() {
        return eposta;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getKonum() {
        return konum;
    }

    /** @return LinkedIn adresi; tanımlı değilse {@code null} */
    public String getLinkedin() {
        return linkedin;
    }

    /** @return GitHub adresi; tanımlı değilse {@code null} */
    public String getGithub() {
        return github;
    }
}
