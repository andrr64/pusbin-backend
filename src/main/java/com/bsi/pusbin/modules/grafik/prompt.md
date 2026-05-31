1. Grafik Sebaran ASN Berdasar Jenjang Jabatan
- x diambil dari tabel jenjang.jabatan
- y diambil dari table asn
- data jumlah dipecah berdasarkan jenis asn
[
 x: jenjang
 y: 2000, 3000
]

// Response X,Y(LABEL, VALUE)
[
    x: [Utama, Penyelia, Pertama, Mahir, Madya, Terampil, Muda]
    y: [
         [
            {
                label: PNS
                value: ....
            },
            {
                label: PPPK
                value: ....
            }
         ]
        ] 
]  

2. Grafik Persentase Gender
- x diambil dari tabel jenis_kelamin
- y diambil dari tabel asn
[
 x: gender
 y: 2000, 3000 
]
Response X, Y (LABEL, VALUE)
[
 x: [Pria, Wanita]
 y: [100, 200]
]


3. Grafik Persentase ASN JF MASN
- x diambil dari tabel jenis asn
- y diambil dari tabel asn
[
 x: jenis asn
 y: 2000, 3000 
]
Response X, Y (LABEL, VALUE)
[
 x: [ASN, PPPK]
 y: [100, 200]
]

4. Grafik Sebaran ASN JFMASN
- x diambil dari tabel instansi
- y diambil dari tabel asn
- data jumlah dipecah berdasarkan jenis asn
[
 x: jenis instansi
 y: 2000, 3000 
]

// Response X,Y(LABEL, VALUE)
[
    x: [Instansi Pusat, Instansi Daerah]
    y: [
         [
            {
                label: PNS
                value: ....
            },
            {
                label: PPPK
                value: ....
            }
         ]
        ] 
]  

5. Grafik Sebaran ASN K/L/PD
- x diambil dari tabel kategori instansi
- y diambil dari tabel instansi
- data jumlah dipecah berdasarkan jenis asn
[
 x: kategori instansi
 y: 2000, 3000, 5000, 9000, dst...
]

// Response X,Y(LABEL, VALUE)
[
    x: [Kementrian, Kabupaten, LPNK, Provinsi, Kota, KLN, KLNS, Kementrian Koordinator]
    y: [
         [
            {
                label: PNS
                value: ....
            },
            {
                label: PPPK
                value: ....
            }
         ]
        ] 
]  

6. Grafik Sebaran ASN Berdasar Jabatan
- x diambil dari tabel jabatan.nama_jabatan
- y diambil dari tabel asn 
- data jumlah dipecah berdasarkan jenis asn
[
 x: nama jabatan
 y: 2000, 3000, 5000, 9000, dst...
]

// Response X,Y(LABEL, VALUE)
[
    x: [Analis SDM Aparatur, Pranata SDM Aparatur, Asesor SDM Aparatur, Auditor Manajemen ASN]
    y: [
         [
            {
                label: PNS
                value: ....
            },
            {
                label: PPPK
                value: ....
            }
         ]
        ] 
]  

7. Grafik Tren Kenaikan Jumlah JF Bidang MASN
- x diambil dari periode.total_asn_periode_by_nama_jabatan
- y diambil dari jumlah_asn.total_asn_periode_by_nama_jabatan
- jumlah dipecah berdasarkan jabatan.namajabatan
[
 x: timeline
 y: 2000, 3000, 5000, 9000, dst...
]

// Response X,Y(LABEL, VALUE)
[
    x: [21 Des 2020, dst]
    y: [
         [
            {
                label: Analis SDM Aparatur
                value: ....
            },
            {
                label: Asesor SDM Aparatur
                value: ....
            },
            {
                label: Pranata SDM Aparatur
                value: ....
            },
            {
                label: Auditor Manajemen ASN
                value: ....
            }
         ]
        ] 
]  

8. Grafik Golongan Ruang
	- "X" diambil dari table "golongan"
	- "Y" diambil dari table "asn"
Response X, Y
[
 x: Golongan ruang
 y: 2000
]

Response X, Y (LABEL, VALUE)
[
 x: [III/d, IX, III/a, III/b, III/c, IV/a, II/c, VII, IV/b, II/d, IV/c, IV/d, IV/e, X]
 y: [100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300]
]


9. Grafik Presentase JF Bidang MASN
	- "X" diambil dari table "jabatan"
	- "Y" diambil dari table "asn"
Response X, Y
[
 x: Nama Jabatan
 y: 2000
]

Response X, Y (LABEL, VALUE)
[
 x: [Analis SDM Aparatur, Pranata SDM Aparatur, Asesor SDM Aparatur, Auditor Manajemen ASN]
 y: [1000, 2000, 3000, 4000, 5000]
]


10. Grafik Sebaran Kategori
	- "X" diambil dari table "jenis_jf"
	- "Y" diambil dari table "asn"
Responnse X, Y
[
 x: PNS, PPPK
 y: 2000, 3000
]
Response X, Y (LABEL, VALUE)
[
 x: [Keahlian, Keterampilan]
 y: [
     [
	{
	  label: PNS
	  value: ....
	}
	{
	  label: PPPK
	  value: ...
	}
     ],
[]
]
]


11. Grafik Masa Kerja Jabatan
	X = proses (asn.masa_kerja_jabatan)
	    #Proses()
	      Masa kerja jabatan adalah integer, tapi aku pengen outputnya:
		1. <9 Tahun
		2. >=9 Tahun
	Y diambil dari table "asn"
Response X, Y
[
 x: Masa Kerja Jabatan
 y: 2000
]
Response X, Y (LABEL, VALUE)
[
 x: [<9 Tahun, >=9 Tahun]
 y: [1000, 1000]
]

12. Grafik Masa Kerja Golongan
X = proses (asn.masa_kerja_golongan)
	    #Proses()
	      Masa kerja golongan adalah integer, tapi aku pengen outputnya:
		1. <5 Tahun
		2. >=5 Tahun
	Y diambil dari table "asn"
Response X, Y
[
 x: Masa Kerja Golongan
 y: 2000
]
Response X, Y (LABEL, VALUE)
[
 x: [<5 Tahun, >=5 Tahun]
 y: [1000, 1000]
]