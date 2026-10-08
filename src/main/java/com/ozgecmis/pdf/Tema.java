package com.ozgecmis.pdf;

import java.awt.Color;

/**
 * Özgeçmiş tasarımında kullanılan renkler, ölçüler ve yazı boyutları.
 *
 * <p>Tüm değerler {@code static final} sabitlerdir; tasarımı değiştirmek için
 * yalnızca bu dosyayı düzenlemek yeterlidir.</p>
 *
 * <p>Sınıf <b>package-private</b>'tır (erişim belirleyicisi yazılmamıştır):
 * yalnızca {@code com.ozgecmis.pdf} paketi kullanır, uygulamanın geri kalanının
 * bu detayları bilmesine gerek yoktur.</p>
 */
final class Tema {

    private Tema() {
    }

    // ---- Renkler ----
    static final Color KENAR_ARKAPLAN = new Color(0x1E, 0x29, 0x3B);   // koyu lacivert
    static final Color VURGU = new Color(0x14, 0xB8, 0xA6);            // turkuaz
    static final Color VURGU_KOYU = new Color(0x0F, 0x76, 0x6E);
    static final Color METIN = new Color(0x1F, 0x29, 0x37);
    static final Color METIN_SOLUK = new Color(0x6B, 0x72, 0x80);
    static final Color KENAR_METIN = Color.WHITE;
    static final Color KENAR_SOLUK = new Color(0xA5, 0xB4, 0xC8);
    static final Color KENAR_PASIF = new Color(0x47, 0x55, 0x69);
    static final Color AYIRAC = new Color(0xE5, 0xE7, 0xEB);

    // ---- Sayfa yerleşimi (birim: punto, A4 = 595 x 842) ----
    static final float KENAR_GENISLIK = 190f;
    static final float KENAR_IC_BOSLUK = 22f;
    static final float ANA_SOL_BOSLUK = 26f;
    static final float ANA_SAG_BOSLUK = 32f;
    static final float UST_BOSLUK = 44f;
    static final float ALT_BOSLUK = 40f;
    static final float FOTOGRAF_YARICAP = 56f;

    // ---- Yazı boyutları ----
    static final float BOYUT_AD = 26f;
    static final float BOYUT_UNVAN = 12.5f;
    static final float BOYUT_BOLUM = 11f;
    static final float BOYUT_KAYIT_BASLIK = 10.5f;
    static final float BOYUT_NORMAL = 9.2f;
    static final float BOYUT_KUCUK = 8.2f;
    static final float BOYUT_ETIKET = 7f;

    /** Satır yüksekliğinin yazı boyutuna oranı. */
    static final float SATIR_ARALIGI = 1.38f;
}
