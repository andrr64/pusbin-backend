# Panduan Integrasi API (Frontend AI Agent)

File ini berisi panduan singkat dan *to-the-point* mengenai endpoint API untuk modul `input/master` di aplikasi backend `Pusbin`.
AI Frontend Developer: Gunakan dokumen ini sebagai referensi utama saat membuat *service* API (seperti Axios/Fetch), *state management* (Zustand/Redux/Pinia), dan UI form/table di *frontend*.

## Base URL
Semua path API di bawah ini harus diawali dengan base URL:
`/api/v1/master`

## Format Response Standar
Semua API menggunakan *wrapper* standar `APIResponse`. Response HTTP 200 OK akan memiliki struktur JSON seperti berikut:

```json
{
  "data": <object atau array data>,
  "message": "Success" // Bisa juga "Created", "Updated", "Deleted"
}
```
*Catatan: Ekstrak properti `data` dari respons untuk diolah lebih lanjut.*

## Pola Endpoint CRUD Default
Semua entitas master di bawah ini menerapkan pola RESTful API yang konsisten:
- **GET** `/{endpoint}` : Menarik semua data (list).
- **GET** `/{endpoint}/{id}` : Menarik satu data spesifik berdasarkan ID.
- **POST** `/{endpoint}` : Membuat data baru. Membutuhkan body payload berupa objek DTO.
- **PUT** `/{endpoint}/{id}` : Mengupdate data berdasarkan ID. Membutuhkan body payload berupa objek DTO.
- **DELETE** `/{endpoint}/{id}` : Menghapus data berdasarkan ID.

## Daftar Endpoint Entitas Master

| Nama Entitas | Endpoint / Path | Keterangan |
| :--- | :--- | :--- |
| **Golongan** | `/golongan` | Data golongan / ruang |
| **Instansi** | `/instansi` | Data daftar instansi |
| **Jabatan** | `/jabatan` | Data daftar jabatan |
| **Jenis ASN** | `/jenis-asn` | Data tipe ASN (PNS, PPPK, dll) |
| **Jenis Diklat** | `/jenis-diklat` | Data macam-macam diklat |
| **Jenis JF** | `/jenis-jf` | Data jenis jabatan fungsional |
| **Jenis Kelamin** | `/jenis-kelamin` | Data laki-laki / perempuan |
| **Kedudukan ASN** | `/kedudukan-asn` | Data status/kedudukan ASN saat ini |
| **Nomenklatur** | `/nomenklatur` | Data nomenklatur |
| **Pendidikan** | `/pendidikan` | Data jenjang/tingkat pendidikan |
| **Total ASN Periode** | `/total-asn-periode-by-nama-jabatan` | Data rekap ASN per periode per jabatan |
| **Users** | `/users` | Data akun pengguna / admin |
| **Wilayah BKN** | `/wilayah-bkn` | Data kantor wilayah BKN |
| **Wilayah Pokja** | `/wilayah-pokja` | Data pembagian wilayah kelompok kerja |

## Instruksi untuk AI Frontend
1. **Generator API Client**: Buat fungsi modular untuk setiap endpoint (misal `getGolongan()`, `createGolongan(payload)`, dll) sesuai dengan pola CRUD di atas.
2. **Handle Response**: Otomatis unwrap properti `data` dari JSON response.
3. **Penamaan Model**: Gunakan penamaan field CamelCase di Typescript/Javascript agar sesuai dengan struktur backend Java Spring Boot.
