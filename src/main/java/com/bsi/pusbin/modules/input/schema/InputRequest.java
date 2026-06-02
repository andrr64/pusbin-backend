package com.bsi.pusbin.modules.input.schema;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputRequest {

    @NotNull(message = "ID ASN/NIP tidak boleh kosong")
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
    
    private LocalDate tmtJabatan;
    private String masaKerjaJabatanString;
    private String golonganRuang;
    private LocalDate tmtGolru;
    private String masaKerjaGolonganString;
    
    private String wilkerBkn;
    private Integer noUrutWilker;
    private String wilayahPokja;
    
    private Object mkGolongan; // can be Date, String, or Integer
    private Object mkJabatan; // can be Date, String, or Integer
}
