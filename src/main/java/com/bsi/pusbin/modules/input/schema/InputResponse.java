package com.bsi.pusbin.modules.input.schema;

import lombok.Data;

@Data
public class InputResponse {
    private Long idAsn;
    private String jenisAsn;
    private String kedudukanAsn;
    private String jenisKelamin;
    private String instansiKerja;
    private String kategoriInstansi;
    private String unitKerja;
    private String tingkatPendidikan;
    private String pendidikan;
    private String jabatan;
    private Integer noUrutJenjang;
    private String jenjang;
    private String jenisJf;
    private String namaJabatan;
    private String nomenklatur;
    private String golongan;
    private String jenisDiklat;
    private String tmtJabatan;
    private String masaKerjaJabatanString;
    private String golonganRuang;
    private String tmtGolru;
    private Integer masaKerjaGolongan;
    private String wilkerBkn;
    private String wilayahPokja;
    private String mkGolongan;
    private Integer mkJabatan;
}
