package com.bsi.pusbin.modules.filter.schema;

import java.util.List;

public record FilterRequest(
    List<Integer> instansiId,
    List<Integer> jenisAsnId,
    List<Integer> nomenklaturId,
    List<Integer> jenjangId,
    List<String> jenjang,
    List<Integer> kategoriInstansiId,
    List<String> kategoriInstansi,
    List<Integer> wilayahPokjaId,
    List<Integer> namaJabatanId,
    List<Integer> jenisInstansiId,
    List<String> jenisInstansi
) {}

