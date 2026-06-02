Modul ini digunakan untuk
- menampilkan data sudah ada di dalam database
- sinkronisasi data dari data lama, data baru, data yang diubah, data yang dihapus
- menginput dan menyimpan data baru melalui form input manual
- mengambil detail data asn berdasarkan ID untuk menampilkan data bawaan saat melaukan edit manual di front-end

Kolom di excel sebagai berikut
jenis asn: diambil dari jenis_asn.nama_jenis disimpan ke asn.id_jenis_asn
kedudukan asn: diambil dari kedudukan_asn.nama_kedudukan disimpan ke asn.id_kedudukan
jenis kelamin: diambil dari jenis_kelamin.nama_kelamin disimpan ke asn.id_jenis_kelamin
instansi kerja: diambil dari instansi.nama_instansi disimpan ke asn.id_instansi
kategori instansi: terhubung dengan kolom instansi.kategori 
unit kerja: cuma ada di field input dan kolom excel, diabaikan saat simpan ke database
tingkat pendidikan: terhubung dengan pendidikan.tingkat
pendidikan: diambil dari pendidikan.nama_pendidikan disimpan ke asn.id_pendidikan
jabatan: diambil dari pendidikan.nama_pendidikan disimpan ke asn.id_pendidikan
no urut jenjang: cuma ada di field input dan kolom excel, diabaikan saat simpan ke database
jenjang: terhubung dengan jabatan.jenjang 
jenis jf: terhubung melalui jabatan.id_jenis_jf ke jenis_jf.nama_jenis_jf
nama jabatan: dari jabatan.nama_jabatan
nomenklatur: Terhubung melalui jabatan.id_nomenklatur ke tabel master nomenklatur.nama_nomenklatur
golongan: diambil dari golongan.golongan_ruang disimpan ke asn.id_golongan
jenis diklat: diambil dari jenis_diklat.nama_jenis_diklat disimpan ke asn.id_jenis_diklat
TMT JABATAN: Input tanggal, disimpan ke asn.tmt_jabatan (DATE).
MASA KERJA JABATAN: Format teks durasi visual (string), diabaikan saat simpan ke database.
GOLONGAN RUANG: Diambil dari pilihan master golongan.golongan_ruang, disimpan ke asn.id_golongan.
TMT GOLRU: Input tanggal, disimpan ke asn.tmt_golongan (DATE).
MASA KERJA GOLONGAN: Format teks durasi visual (string), diabaikan saat simpan ke database.
WILKER BKN: Terhubung melalui instansi.id_wilker ke tabel master wilayah_bkn.nama_wilker.
NO URUT WILKER: cuma ada di field input dan kolom excel, diabaikan saat simpan ke database
WILAYAH POKJA: Terhubung melalui wilayah_bkn.id_wilayah_pokja ke tabel master wilayah_pokja.nama_pokja.
MK GOLONGAN: date disimpan ke asn.masa_kerja_golongan (INTEGER).
MK JABATAN: date disimpan ke asn.masa_kerja_jabatan (INTEGER).

- Di Tampilan Form Frontend: Menyediakan 26 field inputan/dropdown lengkap agar sesuai dengan format kolom Excel yang biasa digunakan pengguna.
- Di Sisi Database Backend: Data dari 26 field form tersebut akan divalidasi dan disimpan ke dalam tabel asn yang hanya memiliki 13 kolom relasi Foreign Key ke tabel master terkait.


Aku mau mengubah beberapa input manual (teks biasa) menjadi komponen Select Box (Dropdown) yang datanya dinamis diambil dari database. 

Saya ingin mengubah seluruh kolom inputnya menjadi dropdown kecuali kolom nip nya

Saya sudah menyiapkan semua file terkait yang dibutuhkan. Tolong buatkan perubahan kodenya secara lengkap untuk sisi backend dan frontend.

Tolong berikan arahan modifikasi kode untuk:
- Membuat endpoint GET baru di Java Controller untuk menyediakan opsi pilihan dinamis.
- Membuat DTO/Schema Response baru di Java untuk struktur data option (label & value).
- Membuat custom hook React Query baru di Frontend untuk mengambil data tersebut.
- Mengubah komponen InputField menjadi SelectBox dinamis di file page.tsx.