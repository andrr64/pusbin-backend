1. pada endpoint POST /api/v1/iam/register
fungsi: mendaftarkan user/akun baru ke dalam sistem
- input = RegisterRequest (nip, password)
- output = pesan sukses pendaftaran

dari itu, tabel apa yang perlu terkait?
- ..
- ...

2. pada endpoint POST /api/v1/iam/login
fungsi: memverifikasi kredensial user dan menerbitkan cookie sesi baru
- input = LoginRequest (nip, password)
- output = access_token dan refresh_token sebagai cookie HttpOnly + Secure

dari itu, tabel apa yang perlu terkait?
- ..
- ...

3. pada endpoint POST /api/v1/iam/refresh
fungsi: memperbarui access token menggunakan refresh token aktif
- input = cookie refresh_token lama
- output = cookie access_token dan refresh_token baru

dari itu, tabel apa yang perlu terkait?
- ..
- ...

4. pada endpoint POST /api/v1/iam/logout
fungsi: menghancurkan sesi otentikasi aktif user
- input = tanpa body, menghapus cookie di client
- output = cookie access_token dan refresh_token masa aktif 0 detik

dari itu, tabel apa yang perlu terkait?
- ..
- ...
============
PROMPT AGENT
Bertindaklah sebagai Ahli Keamanan Cyber & Senior Java Developer. Saya ingin membuat modul IAM (Identity and Access Management) di package "com.bsi.pusbin.modules.iam" dengan tingkat keamanan tinggi.

Spesifikasi & Aturan Keamanan Ketat:
1. GROUND-TRUTH DATABASE SCHEMA (WAJIB):
   - Sebelum menulis kode atau kueri SQL, Anda WAJIB membaca berkas-berkas migrasi database (DDL) yang berada di direktori "src/main/resources/db/migration" agar nama tabel, tipe data kolom, dan relasi Foreign Key yang dihasilkan 100% akurat dan sinkron dengan database asli (TIDAK BOLEH MENGARANG).

2. PENGAMANAN PASSWORD:
   - Gunakan Argon2PasswordEncoder dari Spring Security untuk mengenkripsi password saat registrasi dan memverifikasinya saat login.

3. MANAJEMEN SESI (JWT & COOKIES):
   - Jangan mengirim token JWT (Access Token dan Refresh Token) di body respons JSON.
   - Semua token harus ditulis langsung ke HTTP Cookies menggunakan HttpServletResponse servlet Java.
   - Aturan Cookie: Set `HttpOnly` sebagai true (mencegah XSS), `Secure` sebagai true (hanya lewat HTTPS), Path="/", dan MaxAge sesuai durasi token.
   - Access Token berdurasi pendek (e.g. 15 menit). Refresh Token berdurasi panjang (e.g. 3 hari).

4. REFRESH TOKEN ROTATION (DATABASE NATIVE):
   - Simpan hash satu-arah (SHA-256) dari refresh token ke tabel `refresh_tokens` di database (jangan simpan plain text token!).
   - Ketika endpoint "/api/v1/iam/refresh" dipanggil, hapus token lama yang dipakai tersebut dari database, buat token baru, lalu simpan ke database. Jika token tidak valid atau kadaluarsa, kembalikan 401 Unauthorized.
   - Hubungkan data token dengan user_id dari tabel `users` lewat native SQL JOIN.

5. RATE LIMITER:
   - Buat kelas RateLimiter yang membatasi request per IP Address. Batasi login maksimal 5 kali per 60 detik, dan registrasi 3 kali per 60 detik.
   - Simpan data secara thread-safe menggunakan ConcurrentHashMap. Lempar AppException dengan status HTTP 429 (Too Many Requests) jika melampaui batas.

6. SPESIFIKASI ENDPOINT (POST):
   - /api/v1/iam/register : Body RegisterRequest (nip, password)
   - /api/v1/iam/login : Body LoginRequest (nip, password)
   - /api/v1/iam/refresh : Tanpa body, membaca cookie "refresh_token"
   - /api/v1/iam/logout : Diotorisasi @Auth, menghapus cookie

Tolong buatkan berkas-berkas berikut:
- IamController.java
- IamService.java
- IamRepository.java
- RateLimiter.java
- RegisterRequest.java & LoginRequest.java (Records)
