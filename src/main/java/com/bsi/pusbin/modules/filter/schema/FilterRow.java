package com.bsi.pusbin.modules.filter.schema;

import lombok.Data;

@Data
public class FilterRow {
    private Integer idInstansi;
    private String namaInstansi;
    private String kategori;
    private Integer idJenisAsn;
    private String namaJenis;
    private Integer idNomenklatur;
    private String namaNomenklatur;
    private String jenjang;
    private Integer idWilayahPokja;
    private String namaPokja;
    private Integer idJabatan;
    private String namaJabatan;
}
