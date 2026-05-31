CREATE TABLE IF NOT EXISTS total_asn_periode_by_nama_jabatan (
    id bigserial primary key,
    jumlah_asn integer,
    periode date not null,
    id_jabatan integer REFERENCES jabatan(id_jabatan)
)
