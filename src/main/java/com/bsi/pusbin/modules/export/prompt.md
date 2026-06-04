# Modul Export Dashboard ASN

1. Tujuan
- Membuat fitur Export PDF pada Dashboard ASN.
- PDF yang dihasilkan harus merepresentasikan kondisi dashboard saat tombol Download PDF ditekan.
- Jika user sedang menggunakan filter tertentu, maka data yang diexport harus mengikuti seluruh filter yang aktif.

2. Data Yang Diexport
- PDF harus memuat seluruh komponen yang tampil pada Dashboard.

3. Ringkasan Filter Aktif
- Tampilkan seluruh filter yang sedang dipilih user.
Contoh:
- Jenis ASN: PNS
- Jenjang: Madya
- Wilayah Pokja: Jawa Barat
- Instansi: Kementerian X

4. Grafik
- Export seluruh grafik dashboard:
  1. Persentase Gender
  2. Sebaran ASN JFMASN
  3. Sebaran Kategori JFMASN
  4. Sebaran ASN di K/L/PD
  5. Sebaran ASN Berdasar Jenjang Jabatan
  6. Masa Kerja Golongan
  7. Masa Kerja Jabatan
  8. Sebaran Golongan Ruang
  9. Grafik lain yang saat ini tampil pada dashboard
- Grafik yang diexport harus mengikuti filter aktif.
        
5. Tabel
- Export seluruh tabel dashboard:
  1. Tabel Wilayah Kerja
  2. Tabel Pendidikan
  3. Tabel Nama Jabatan
  4. Tabel lain yang tampil pada dashboard
- Seluruh tabel harus mengikuti filter aktif.

6. Perilaku Filter
- Export harus menggunakan state filter yang sama dengan dashboard.
Contoh:
- Jika user memilih:
  - Jenis ASN = PNS
  - Jenjang = Madya
- Maka seluruh grafik dan tabel pada PDF hanya menampilkan data hasil filter tersebut.

7. Layout PDF
- PDF harus menyerupai tampilan dashboard:
  - Judul laporan
  - Informasi waktu export
  - Filter aktif
  - Grafik
  - Tabel
- Jika konten melebihi satu halaman:
  - otomatis membuat halaman baru
  - tidak memotong grafik atau tabel

Endpoint:
POST /api/v1/export/dashboard

Request:

Seluruh filter dashboard yang sedang aktif.

Response:

File PDF.
