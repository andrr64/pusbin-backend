 Modul digunakan untuk mereturn value filter, contoh:
 - Instansi
    - Kementrian Agama
    - Badan Kepegawaian Negara
- Jenis ASN
    - PNS
    - PPPK
- Nomenklatur
    - Nomenklatur Lama
    - Nomenklatur Baru
- Jenjang
    - Pertama
    - Muda
    - Madya
    - Utama
    - Terampil
    - Mahir
    - Penyelia
- Kategori Instansi
    - Kementerian
    - Kabupaten
    - LPNK
    - Provinsi
    - Kota
    - KLNS
    - KLN
    - Kementrian Koordinator
- Wilayah Pokja
    - Pokja 1
    - Pokja 2
    - Pokja 3
    - Pokja 4
- Nama Jabatan
    - Analis SDM Aparatur
    - Pranata SDM Aaparatur
    - Asesor SDM Aaparatur
    - Auditor SDM Manajemen ASN
Nb: 
- Setiap filter saling berelasi. Nilai yang muncul pada suatu filter harus menyesuaikan filter lain yang dipilih user.

Endpoint
GET/api/filter

Request
Query parameter bersifat opsional.

 #response
 APIResponse
 data
 - key: array[
    - key: [label, id],
    - key: [label, id]
    - key: [label, id]
 ]
 - Instansi = [['Badan Kepegawaian Negara', 1], ['Kementrian Agama', 4], dst...]
 - JenisASN = [['PNS', 1], ['PPPK', 2], dst...]
 - Nomenklatur = [['Nomenklatur Lama', 1], ['Nomenklatur Baru', 2], dst...]
 - Jenjang = [['Pertama', 1], ['Muda', 2], ['Madya', 3], ['Utama', 4], ['Trampil', 5], ['Mahir', 6], ['Penyelia', 7], dst...]
 - Kategori Instansi = [['Kementrian', 1], ['Kabupaten', 2], dst...]
 - Wilayah Pokja = [['pokja 1', 1], ['pokja 2', 2], dst...]
 - Nama Jabatan = [['Analis SDM Aparatur', 1], ['Pranata SDM Aaparatur', 2], dst...]
 
Nb: 
- Repository harus mengambil seluruh nilai filter berdasarkan data yang tersedia setelah seluruh parameter request diterapkan.
- Contoh:
    - Jika user memilih:
        - Instansi = Kementerian Agama
        - Jenis ASN = PPPK
    - Maka seluruh filter lain hanya menampilkan data yang masih memiliki relasi dengan kombinasi tersebut.
    - Filter yang sedang dipilih tetap muncul pada response.
    Controller
        - Menerima seluruh query parameter filter.
        - Memanggil service.
        - Mengembalikan APIResponse.
    Service
        - Memvalidasi request.
        - Mengatur business logic filter.
        - Memanggil repository.
    Repository
        - Mengambil data filter dari database.
        - Menerapkan seluruh filter yang dikirim user.
        - Mengembalikan daftar nilai unik untuk setiap kategori filter.

