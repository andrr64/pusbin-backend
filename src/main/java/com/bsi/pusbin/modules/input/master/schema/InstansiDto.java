package com.bsi.pusbin.modules.input.master.schema;

import lombok.Data;

@Data
public class InstansiDto {
    private Integer idInstansi;
    private Integer idWilker;
    private String namaInstansi;
    private String kategori;
    private String jenisInstansi;
}
