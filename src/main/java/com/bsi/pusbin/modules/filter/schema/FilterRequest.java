package com.bsi.pusbin.modules.filter.schema;

public record FilterRequest(
    Integer instansiId,
    Integer jenisAsnId,
    Integer nomenklaturId,
    Integer jenjangId,
    String jenjang,
    Integer kategoriInstansiId,
    String kategoriInstansi,
    Integer wilayahPokjaId,
    Integer namaJabatanId,
    String jenisInstansi
) {}
