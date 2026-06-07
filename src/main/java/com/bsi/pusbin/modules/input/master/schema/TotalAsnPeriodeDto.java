package com.bsi.pusbin.modules.input.master.schema;

import lombok.Data;

@Data
public class TotalAsnPeriodeDto {
    private Long id;
    private Integer jumlahAsn;
    private java.time.LocalDate periode;
    private Integer idJabatan;
}
