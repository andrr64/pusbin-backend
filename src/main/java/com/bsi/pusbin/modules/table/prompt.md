#Endpoint

1. tabel-wilayah-kerja
    a. kolom: wilayah kerja, jumlah ASN
    "jumlah ASN" diambil dari table "asn"
    "wilayah kerja" ambil dari table "wilayah_bkn"
    b. return 
    APIResponse.data
    [
        [column1, column2, dst...],
        [row1, row2, dst...]
    ]

    [
         ["Wilayah Kerja", "Jumlah"],
         [Jakarta, 200000],
         [Bekasi, 200000],
         dst
    ]

2. table-jabatan
    a. kolom: jabatan, jumlah asn
    "Jumlah asn" diambil dari table "asn" 
    "jabatan" adalah gabungan dari "nama jabatan" dan "jenjang"
    {jabatan.nama_jabatan} {jabatan.jenjang}
    b. return 
    APIResponse.data
    [
        [column1, column2, dst...],
        [row1, row2, dst...]
    ]

    [
         ["Jabatan", "Jumlah"],
         ["Analis SDM Aparatur Ahli Madya", 200000],
         ["Pranata SDM Aparatur Ahli Utama", 200000],
         dst
    ]

3. table-pendidikan
    a. kolom: tingkat pendidikan, jumlah asn, persentase
    "tingkat pendidikan" diambil dari "nama pendidikan"
    "jumlah asn" diambil dari table "asn"
    "Presentase" diambil dari banyaknya jumlah pada tabel "asn"
    b. return
    APIResponse.data
    [
        [column1, column2, column2, dst...],
        [row1, row2, dst...]
    ]

    [
         ["tingkat pendidikan", "Jumlah", "persentase"],
         ["S1", 200000, "70%"],
         [" Diploma III", 200000, "30%"],
         dst
    ]

4. table-instansi
Tabel Instansi
a. Kolom: Instansi, Jumlah
	- "Instansi" diambil dari table "Instansi"
	- "Jumlah" diambil dari table "ASN"
b. Return
APIResponse.data
[
	[column1, column2, dst...],
	[row1. row2, dst......]
]
[	["Instansi Kerja", "Jumlah"],
	["Kementerian Agama", 2700],
	["Kementerian Keuangan", 2700],
]

