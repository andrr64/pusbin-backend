-- ===========================================================================
-- DATABASE MIGRATION V1: INITIAL SCHEMA CREATION WITH HIGHLY OPTIMIZED INDEXING
-- Based on SQLAlchemy Models from jfk-analytics-python-server
-- ===========================================================================

-- ---------------------------------------------------------------------------
-- 1. INDEPENDENT MASTER TABLES
-- ---------------------------------------------------------------------------

-- Table: users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    nip VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- Table: jenis_asn
CREATE TABLE jenis_asn (
    id_jenis_asn SERIAL PRIMARY KEY,
    nama_jenis VARCHAR(255) NOT NULL
);

-- Table: kedudukan_asn
CREATE TABLE kedudukan_asn (
    id_kedudukan SERIAL PRIMARY KEY,
    nama_kedudukan VARCHAR(255) NOT NULL
);

-- Table: jenis_kelamin
CREATE TABLE jenis_kelamin (
    id_jenis_kelamin SERIAL PRIMARY KEY,
    nama_kelamin VARCHAR(255) NOT NULL
);

-- Table: nomenklatur
CREATE TABLE nomenklatur (
    id_nomenklatur SERIAL PRIMARY KEY,
    nama_nomenklatur VARCHAR(255) NOT NULL
);

-- Table: jenis_jf
CREATE TABLE jenis_jf (
    id_jenis_jf SERIAL PRIMARY KEY,
    nama_jenis_jf VARCHAR(255) NOT NULL
);

-- Table: jenis_diklat
CREATE TABLE jenis_diklat (
    id_jenis_diklat SERIAL PRIMARY KEY,
    nama_jenis_diklat VARCHAR(255) NOT NULL
);

-- Table: golongan
CREATE TABLE golongan (
    id_golongan SERIAL PRIMARY KEY,
    golongan_ruang VARCHAR(10) NOT NULL
);

-- Table: wilayah_pokja
CREATE TABLE wilayah_pokja (
    id_wilayah_pokja SERIAL PRIMARY KEY,
    nama_pokja VARCHAR(100) NOT NULL
);

-- Table: pendidikan
CREATE TABLE pendidikan (
    id_pendidikan SERIAL PRIMARY KEY,
    tingkat VARCHAR(10) NOT NULL,
    nama_pendidikan VARCHAR(100) NOT NULL
);

-- ---------------------------------------------------------------------------
-- 2. DEPENDENT MASTER TABLES
-- ---------------------------------------------------------------------------

-- Table: wilayah_bkn
CREATE TABLE wilayah_bkn (
    id_wilker SERIAL PRIMARY KEY,
    nama_wilker VARCHAR(100) NOT NULL,
    no_urut INTEGER,
    id_wilayah_pokja INTEGER REFERENCES wilayah_pokja(id_wilayah_pokja) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------------
-- 3. MEDIUM-LEVEL ENTITIES
-- ---------------------------------------------------------------------------

-- Table: jabatan
CREATE TABLE jabatan (
    id_jabatan SERIAL PRIMARY KEY,
    id_nomenklatur INTEGER REFERENCES nomenklatur(id_nomenklatur) ON DELETE SET NULL,
    id_jenis_jf INTEGER REFERENCES jenis_jf(id_jenis_jf) ON DELETE SET NULL,
    nama_jabatan VARCHAR(255) NOT NULL,
    jenjang VARCHAR(50)
);

-- Table: instansi
CREATE TABLE instansi (
    id_instansi SERIAL PRIMARY KEY,
    id_wilker INTEGER REFERENCES wilayah_bkn(id_wilker) ON DELETE SET NULL,
    nama_instansi VARCHAR(255) NOT NULL,
    kategori VARCHAR(50),
    jenis_instansi VARCHAR(50)
);

-- ---------------------------------------------------------------------------
-- 4. CORE ANALYTICAL FACT TABLE (asn)
-- ---------------------------------------------------------------------------

-- Table: asn
CREATE TABLE asn (
    id_asn BIGINT PRIMARY KEY,
    id_jenis_asn INTEGER REFERENCES jenis_asn(id_jenis_asn) ON DELETE SET NULL,
    id_kedudukan INTEGER REFERENCES kedudukan_asn(id_kedudukan) ON DELETE SET NULL,
    id_jenis_kelamin 1 INTEGER REFERENCES jenis_kelamin(id_jenis_kelamin) ON DELETE SET NULL,
    id_pendidikan INTEGER REFERENCES pendidikan(id_pendidikan) ON DELETE SET NULL,
    id_instansi INTEGER REFERENCES instansi(id_instansi) ON DELETE SET NULL,
    id_jabatan INTEGER REFERENCES jabatan(id_jabatan) ON DELETE SET NULL,
    id_golongan INTEGER REFERENCES golongan(id_golongan) ON DELETE SET NULL,
    id_jenis_diklat INTEGER REFERENCES jenis_diklat(id_jenis_diklat) ON DELETE SET NULL,
    tmt_jabatan DATE,
    masa_kerja_jabatan INTEGER,
    tmt_golongan DATE,
    masa_kerja_golongan INTEGER
);

-- ---------------------------------------------------------------------------
-- 5. HIGH-PERFORMANCE INDEXES
-- ---------------------------------------------------------------------------

-- Master & Entity Table Foreign Key / Search Indexes
CREATE INDEX idx_users_nip ON users(nip);
CREATE INDEX idx_wilayah_bkn_pokja ON wilayah_bkn(id_wilayah_pokja);
CREATE INDEX idx_jabatan_nomenklatur ON jabatan(id_nomenklatur);
CREATE INDEX idx_jabatan_jenis_jf ON jabatan(id_jenis_jf);
CREATE INDEX idx_instansi_wilker ON instansi(id_wilker);

-- B-Tree Indexes on the Fact Table (asn) Foreign Keys for Fast Joins and Group By Queries
CREATE INDEX idx_asn_jenis_asn ON asn(id_jenis_asn);
CREATE INDEX idx_asn_kedudukan ON asn(id_kedudukan);
CREATE INDEX idx_asn_jenis_kelamin ON asn(id_jenis_kelamin);
CREATE INDEX idx_asn_pendidikan ON asn(id_pendidikan);
CREATE INDEX idx_asn_instansi ON asn(id_instansi);
CREATE INDEX idx_asn_jabatan ON asn(id_jabatan);
CREATE INDEX idx_asn_golongan ON asn(id_golongan);
CREATE INDEX idx_asn_jenis_diklat ON asn(id_jenis_diklat);
