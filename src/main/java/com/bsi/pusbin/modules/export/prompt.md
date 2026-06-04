# Modul Export Dashboard ASN

## Tujuan

Membuat fitur Export PDF pada Dashboard ASN.

PDF yang dihasilkan harus merepresentasikan kondisi dashboard saat tombol Download PDF ditekan.

Jika user sedang menggunakan filter tertentu, maka data yang diexport harus mengikuti seluruh filter yang aktif.

---

## Data Yang Diexport

PDF harus memuat seluruh komponen yang tampil pada Dashboard.

### Ringkasan Filter Aktif

Tampilkan seluruh filter yang sedang dipilih user.

Contoh:

* Jenis ASN: PNS
* Jenjang: Madya
* Wilayah Pokja: Jawa Barat
* Instansi: Kementerian X

---

### Grafik

Export seluruh grafik dashboard:

1. Persentase Gender
2. Sebaran ASN JFMASN
3. Sebaran Kategori JFMASN
4. Sebaran ASN di K/L/PD
5. Sebaran ASN Berdasar Jenjang Jabatan
6. Masa Kerja Golongan
7. Masa Kerja Jabatan
8. Sebaran Golongan Ruang
9. Grafik lain yang saat ini tampil pada dashboard

Grafik yang diexport harus mengikuti filter aktif.

---

### Tabel

Export seluruh tabel dashboard:

1. Tabel Wilayah Kerja
2. Tabel Pendidikan
3. Tabel Nama Jabatan
4. Tabel lain yang tampil pada dashboard

Seluruh tabel harus mengikuti filter aktif.

---

## Perilaku Filter

Export harus menggunakan state filter yang sama dengan dashboard.

Contoh:

Jika user memilih:

* Jenis ASN = PNS
* Jenjang = Madya

Maka seluruh grafik dan tabel pada PDF hanya menampilkan data hasil filter tersebut.

---

## Layout PDF

PDF harus menyerupai tampilan dashboard:

1. Judul laporan
2. Informasi waktu export
3. Filter aktif
4. Grafik
5. Tabel

Jika konten melebihi satu halaman:

* otomatis membuat halaman baru
* tidak memotong grafik atau tabel

---

## Backend

Buat modul export:

* ExportController
* ExportService

Endpoint:

POST /api/v1/export/dashboard

Request:

Seluruh filter dashboard yang sedang aktif.

Response:

File PDF.

---

## Frontend

Tombol Download PDF pada dashboard harus:

1. Mengirim seluruh filter aktif ke endpoint export.
2. Menerima file PDF dari backend.
3. Mengunduh file otomatis ke browser.

---

## Target

File PDF yang dihasilkan harus konsisten dengan kondisi dashboard saat tombol Download PDF ditekan dan berisi seluruh grafik serta tabel yang sedang tampil.
