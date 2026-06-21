package com.bsi.pusbin.modules.input.schema;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class InputRequest {
    @NotBlank(message = "NIP wajib diisi")
    @Pattern(regexp = "^\\d{18}$", message = "NIP harus 18 digit angka")
    private String nip;

    @NotBlank(message = "Nama wajib diisi")
    private String nama;

    @NotBlank(message = "Jenis ASN wajib diisi")
    private String jenisAsn;

    @NotBlank(message = "Kedudukan ASN wajib diisi")
    private String kedudukanAsn;

    @NotBlank(message = "Jenis kelamin wajib diisi")
    private String jenisKelamin;

    @NotBlank(message = "Instansi kerja wajib diisi")
    private String instansiKerja;

    @NotBlank(message = "Kategori instansi wajib diisi")
    private String kategoriInstansi;

    @NotBlank(message = "Jenis instansi wajib diisi")
    private String jenisInstansi;

    @NotBlank(message = "Tingkat pendidikan wajib diisi")
    private String tingkatPendidikan;

    @NotBlank(message = "Pendidikan wajib diisi")
    private String pendidikan;

    @NotBlank(message = "Jabatan wajib diisi")
    private String jabatan;

    private Integer noUrutJenjang;

    @NotBlank(message = "Jenjang wajib dipilih")
    private String jenjang;

    @NotBlank(message = "Jenis JF wajib dipilih")
    private String jenisJf;

    @NotBlank(message = "Nama jabatan wajib diisi")
    private String namaJabatan;

    @NotBlank(message = "Nomenklatur wajib diisi")
    private String nomenklatur;

    private String golongan;

    @NotBlank(message = "Jenis diklat wajib dipilih")
    private String jenisDiklat;

    @NotBlank(message = "TMT Jabatan wajib diisi")
    private String tmtJabatan;

    private String masaKerjaJabatanString;

    @NotBlank(message = "Golongan ruang wajib diisi")
    private String golonganRuang;

    @NotBlank(message = "TMT Golru wajib diisi")
    private String tmtGolru;

    private Integer masaKerjaGolongan;

    @NotBlank(message = "Wilker BKN wajib diisi")
    private String wilkerBkn;

    @NotBlank(message = "Wilayah Pokja wajib diisi")
    private String wilayahPokja;

    private String mkGolongan;

    private Integer mkJabatan;
}
