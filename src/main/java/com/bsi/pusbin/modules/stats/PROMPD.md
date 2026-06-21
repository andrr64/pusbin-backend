# Implement Stats Summary Endpoint

Buat module `stats` dengan endpoint berikut:

```http
GET /api/v1/stats/summary
```

## Tujuan

Endpoint ini digunakan untuk menampilkan kartu ringkasan (summary cards) pada dashboard.

Endpoint harus mendukung seluruh filter yang tersedia pada module `grafik` melalui `FilterRequest`.

Jika filter tidak dikirim, maka perhitungan dilakukan terhadap seluruh data pada database.

Jika filter aktif, maka seluruh nilai statistik harus menyesuaikan hasil filter tersebut.

## Statistik yang dikembalikan

Endpoint harus mengembalikan object yang berisi:

```json
{
  "totalPegawai": 0,
  "totalInstansi": 0
}
```

### Definisi

#### totalPegawai

Jumlah seluruh ASN yang memenuhi filter.

Perhitungan menggunakan:

```sql
COUNT(a.id_asn)
```

#### totalInstansi

Jumlah instansi unik yang memiliki ASN yang memenuhi filter.

Perhitungan menggunakan:

```sql
COUNT(DISTINCT a.id_instansi)
```

## Catatan Penting

* `totalPegawai` dan `totalInstansi` dihitung secara independen.
* Nilai keduanya **tidak dijumlahkan menjadi satu angka**.
* Endpoint hanya menggabungkan beberapa metrik statistik ke dalam satu response untuk efisiensi request frontend.
* Seluruh filter harus menggunakan logic yang sama dengan module `grafik`.
* Disarankan untuk menggunakan helper/filter builder yang sama agar konsisten.

## Contoh

Data keseluruhan:

```text
Total ASN       : 100.000
Total Instansi  : 500
```

Response:

```json
{
  "totalPegawai": 100000,
  "totalInstansi": 500
}
```

Jika filter:

```text
kategori = "Kementerian"
jenisKelaminId = 1
```

dan hasil filter menghasilkan:

```text
ASN            : 23.456
Instansi unik  : 28
```

maka response menjadi:

```json
{
  "totalPegawai": 23456,
  "totalInstansi": 28
}
```

## Struktur yang diharapkan

* `StatsController`
* `StatsService`
* `StatsRepository`
* `StatsResponse`

Contoh endpoint:

```java
@GetMapping("/summary")
public ResponseEntity<APIResponse<StatsResponse>> getSummary(FilterRequest request) {
    StatsResponse response = service.getSummary(request);
    return ResponseEntity.ok(APIResponse.ok(response));
}
```
  