package com.bsi.pusbin.modules.input;

import com.bsi.pusbin.modules.input.schema.InputRequest;
import com.bsi.pusbin.modules.input.schema.InputResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InputRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private InputResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        InputResponse res = new InputResponse();
        res.setIdAsn(rs.getLong("id_asn"));
        res.setNip(rs.getString("nip"));  // FIX: nip sebelumnya tidak di-set
        res.setNama(rs.getString("nama"));
        res.setJenisAsn(rs.getString("jenis_asn"));
        res.setKedudukanAsn(rs.getString("kedudukan_asn"));
        res.setJenisKelamin(rs.getString("jenis_kelamin"));
        res.setInstansiKerja(rs.getString("instansi_kerja"));
        res.setKategoriInstansi(rs.getString("kategori_instansi"));
        res.setTingkatPendidikan(rs.getString("tingkat_pendidikan"));
        res.setPendidikan(rs.getString("pendidikan"));
        res.setJabatan(rs.getString("jabatan"));
        res.setJenjang(rs.getString("jenjang"));
        res.setJenisJf(rs.getString("jenis_jf"));
        res.setNamaJabatan(rs.getString("nama_jabatan"));
        res.setNomenklatur(rs.getString("nomenklatur"));
        res.setGolonganRuang(rs.getString("golongan_ruang"));
        res.setJenisDiklat(rs.getString("jenis_diklat"));
        res.setWilkerBkn(rs.getString("wilker_bkn"));
        res.setWilayahPokja(rs.getString("wilayah_pokja"));
        
        // Ensure nullable fields don't cause issues
        Object mkGol = rs.getObject("mk_golongan");
        if (mkGol != null) {
            int mk = ((Number) mkGol).intValue();
            res.setMasaKerjaGolongan(mk);
            res.setMkGolongan(mk >= 5 ? ">= 5 Tahun" : "< 5 Tahun");
        }
        
        Object mkJab = rs.getObject("mk_jabatan");
        if (mkJab != null) res.setMkJabatan(((Number) mkJab).intValue());
        
        res.setTmtJabatan(rs.getString("tmt_jabatan"));
        res.setTmtGolru(rs.getString("tmt_golongan"));

        return res;
    }

    private String getBaseQuery() {
        return """
            SELECT 
                a.id_asn,
                a.nip,
                a.nama,
                ja.nama_jenis AS jenis_asn,
                k.nama_kedudukan AS kedudukan_asn,
                jk.nama_kelamin AS jenis_kelamin,
                i.nama_instansi AS instansi_kerja,
                i.kategori AS kategori_instansi,
                p.tingkat AS tingkat_pendidikan,
                p.nama_pendidikan AS pendidikan,
                j.nama_jabatan AS jabatan,
                j.jenjang AS jenjang,
                jf.nama_jenis_jf AS jenis_jf,
                j.nama_jabatan AS nama_jabatan,
                n.nama_nomenklatur AS nomenklatur,
                g.golongan_ruang AS golongan_ruang,
                jd.nama_jenis_diklat AS jenis_diklat,
                wb.nama_wilker AS wilker_bkn,
                wp.nama_pokja AS wilayah_pokja,
                a.masa_kerja_golongan AS mk_golongan,
                a.masa_kerja_jabatan AS mk_jabatan,
                a.tmt_jabatan,
                a.tmt_golongan
            FROM asn a
            LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn
            LEFT JOIN kedudukan_asn k ON a.id_kedudukan = k.id_kedudukan
            LEFT JOIN jenis_kelamin jk ON a.id_jenis_kelamin = jk.id_jenis_kelamin
            LEFT JOIN instansi i ON a.id_instansi = i.id_instansi
            LEFT JOIN wilayah_bkn wb ON i.id_wilker = wb.id_wilker
            LEFT JOIN wilayah_pokja wp ON wb.id_wilayah_pokja = wp.id_wilayah_pokja
            LEFT JOIN pendidikan p ON a.id_pendidikan = p.id_pendidikan
            LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan
            LEFT JOIN nomenklatur n ON j.id_nomenklatur = n.id_nomenklatur
            LEFT JOIN jenis_jf jf ON j.id_jenis_jf = jf.id_jenis_jf
            LEFT JOIN golongan g ON a.id_golongan = g.id_golongan
            LEFT JOIN jenis_diklat jd ON a.id_jenis_diklat = jd.id_jenis_diklat
        """;
    }

    public List<InputResponse> findAll(String search, int limit, int offset) {
        StringBuilder sql = new StringBuilder(getBaseQuery());
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" WHERE ja.nama_jenis ILIKE :search OR i.nama_instansi ILIKE :search OR j.nama_jabatan ILIKE :search");
            params.addValue("search", "%" + search + "%");
        }

        sql.append(" ORDER BY a.id_asn DESC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbc.query(sql.toString(), params, this::mapRow);
    }

    public long countAll(String search) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM asn a ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" LEFT JOIN jenis_asn ja ON a.id_jenis_asn = ja.id_jenis_asn ");
            sql.append(" LEFT JOIN instansi i ON a.id_instansi = i.id_instansi ");
            sql.append(" LEFT JOIN jabatan j ON a.id_jabatan = j.id_jabatan ");
            sql.append(" WHERE ja.nama_jenis ILIKE :search OR i.nama_instansi ILIKE :search OR j.nama_jabatan ILIKE :search");
            params.addValue("search", "%" + search + "%");
        }

        Long count = jdbc.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    public InputResponse findById(Long id) {
        String sql = getBaseQuery() + " WHERE a.id_asn = :id";
        List<InputResponse> list = jdbc.query(sql, new MapSqlParameterSource("id", id), this::mapRow);
        return list.isEmpty() ? null : list.get(0);
    }

    private final com.bsi.pusbin.modules.importdata.ImportRepository importRepository;

    public void save(InputRequest req) {
        Integer idJenisAsn = resolveJenisAsn(req.getJenisAsn());
        Integer idKedudukan = resolveKedudukanAsn(req.getKedudukanAsn());
        Integer idJenisKelamin = resolveJenisKelamin(req.getJenisKelamin());
        Integer idPokja = resolveWilayahPokja(req.getWilayahPokja());
        Integer idWilker = resolveWilayahBkn(req.getWilkerBkn(), idPokja, null);
        Integer idInstansi = resolveInstansi(req.getInstansiKerja(), req.getKategoriInstansi(), idWilker);
        Integer idPendidikan = resolvePendidikan(req.getPendidikan(), req.getTingkatPendidikan());
        Integer idNomenklatur = resolveNomenklatur(req.getNomenklatur());
        Integer idJenisJf = resolveJenisJf(req.getJenisJf());
        String resolvedNamaJabatan = req.getNamaJabatan() != null && !req.getNamaJabatan().isEmpty() ? req.getNamaJabatan() : req.getJabatan();
        Integer idJabatan = resolveJabatan(resolvedNamaJabatan, req.getJenjang(), idNomenklatur, idJenisJf);
        String resolvedGol = req.getGolonganRuang() != null && !req.getGolonganRuang().isEmpty() ? req.getGolonganRuang() : req.getGolongan();
        Integer idGolongan = resolveGolongan(resolvedGol);
        Integer idJenisDiklat = resolveJenisDiklat(req.getJenisDiklat());
        
        java.time.LocalDate tmtJab = req.getTmtJabatan() != null && !req.getTmtJabatan().isEmpty() ? java.time.LocalDate.parse(req.getTmtJabatan()) : null;
        java.time.LocalDate tmtGol = req.getTmtGolru() != null && !req.getTmtGolru().isEmpty() ? java.time.LocalDate.parse(req.getTmtGolru()) : null;

        String sql = """
            INSERT INTO asn (
                nip, nama, id_jenis_asn, id_kedudukan, id_jenis_kelamin, id_pendidikan,
                id_instansi, id_jabatan, id_golongan, id_jenis_diklat,
                tmt_jabatan, masa_kerja_jabatan, tmt_golongan, masa_kerja_golongan
            ) VALUES (
                :nip, :nama, :idJenisAsn, :idKedudukan, :idJenisKelamin, :idPendidikan,
                :idInstansi, :idJabatan, :idGolongan, :idJenisDiklat,
                :tmtJabatan, :masaKerjaJabatan, :tmtGolongan, :masaKerjaGolongan
            )
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("nip", req.getNip())
            .addValue("nama", req.getNama())
            .addValue("idJenisAsn", idJenisAsn)
            .addValue("idKedudukan", idKedudukan)
            .addValue("idJenisKelamin", idJenisKelamin)
            .addValue("idPendidikan", idPendidikan)
            .addValue("idInstansi", idInstansi)
            .addValue("idJabatan", idJabatan)
            .addValue("idGolongan", idGolongan)
            .addValue("idJenisDiklat", idJenisDiklat)
            .addValue("tmtJabatan", tmtJab)
            .addValue("masaKerjaJabatan", req.getMkJabatan())
            .addValue("tmtGolongan", tmtGol)
            .addValue("masaKerjaGolongan", req.getMasaKerjaGolongan());

        jdbc.update(sql, params);
    }

    public void update(Long id, InputRequest req) {
        Integer idJenisAsn = resolveJenisAsn(req.getJenisAsn());
        Integer idKedudukan = resolveKedudukanAsn(req.getKedudukanAsn());
        Integer idJenisKelamin = resolveJenisKelamin(req.getJenisKelamin());
        Integer idPokja = resolveWilayahPokja(req.getWilayahPokja());
        Integer idWilker = resolveWilayahBkn(req.getWilkerBkn(), idPokja, null);
        Integer idInstansi = resolveInstansi(req.getInstansiKerja(), req.getKategoriInstansi(), idWilker);
        Integer idPendidikan = resolvePendidikan(req.getPendidikan(), req.getTingkatPendidikan());
        Integer idNomenklatur = resolveNomenklatur(req.getNomenklatur());
        Integer idJenisJf = resolveJenisJf(req.getJenisJf());
        String resolvedNamaJabatan = req.getNamaJabatan() != null && !req.getNamaJabatan().isEmpty() ? req.getNamaJabatan() : req.getJabatan();
        Integer idJabatan = resolveJabatan(resolvedNamaJabatan, req.getJenjang(), idNomenklatur, idJenisJf);
        String resolvedGol = req.getGolonganRuang() != null && !req.getGolonganRuang().isEmpty() ? req.getGolonganRuang() : req.getGolongan();
        Integer idGolongan = resolveGolongan(resolvedGol);
        Integer idJenisDiklat = resolveJenisDiklat(req.getJenisDiklat());
        
        java.time.LocalDate tmtJab = req.getTmtJabatan() != null && !req.getTmtJabatan().isEmpty() ? java.time.LocalDate.parse(req.getTmtJabatan()) : null;
        java.time.LocalDate tmtGol = req.getTmtGolru() != null && !req.getTmtGolru().isEmpty() ? java.time.LocalDate.parse(req.getTmtGolru()) : null;

        String sql = """
            UPDATE asn SET
                nip = :nip,
                nama = :nama,
                id_jenis_asn = :idJenisAsn,
                id_kedudukan = :idKedudukan,
                id_jenis_kelamin = :idJenisKelamin,
                id_pendidikan = :idPendidikan,
                id_instansi = :idInstansi,
                id_jabatan = :idJabatan,
                id_golongan = :idGolongan,
                id_jenis_diklat = :idJenisDiklat,
                tmt_jabatan = :tmtJabatan,
                masa_kerja_jabatan = :masaKerjaJabatan,
                tmt_golongan = :tmtGolongan,
                masa_kerja_golongan = :masaKerjaGolongan
            WHERE id_asn = :idAsn
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("nip", req.getNip())
            .addValue("nama", req.getNama())
            .addValue("idJenisAsn", idJenisAsn)
            .addValue("idKedudukan", idKedudukan)
            .addValue("idJenisKelamin", idJenisKelamin)
            .addValue("idPendidikan", idPendidikan)
            .addValue("idInstansi", idInstansi)
            .addValue("idJabatan", idJabatan)
            .addValue("idGolongan", idGolongan)
            .addValue("idJenisDiklat", idJenisDiklat)
            .addValue("tmtJabatan", tmtJab)
            .addValue("masaKerjaJabatan", req.getMkJabatan())
            .addValue("tmtGolongan", tmtGol)
            .addValue("masaKerjaGolongan", req.getMasaKerjaGolongan())
            .addValue("idAsn", id);

        jdbc.update(sql, params);
    }

    private Integer resolveJenisAsn(String val) { return val == null || val.isEmpty() ? null : importRepository.insertJenisAsn(val); }
    private Integer resolveKedudukanAsn(String val) { return val == null || val.isEmpty() ? null : importRepository.insertKedudukanAsn(val); }
    private Integer resolveJenisKelamin(String val) { return val == null || val.isEmpty() ? null : importRepository.insertJenisKelamin(val); }
    private Integer resolveWilayahPokja(String val) { return val == null || val.isEmpty() ? null : importRepository.insertWilayahPokja(val); }
    private Integer resolveWilayahBkn(String val, Integer idPokja, Integer noUrut) { return val == null || val.isEmpty() ? null : importRepository.insertWilayahBkn(val, idPokja, noUrut); }
    private Integer resolveInstansi(String val, String kategori, Integer idWilker) { return val == null || val.isEmpty() ? null : importRepository.insertInstansi(val, kategori, null, idWilker); }
    private Integer resolvePendidikan(String val, String tingkat) { return val == null || val.isEmpty() ? null : importRepository.insertPendidikan(val, tingkat); }
    private Integer resolveNomenklatur(String val) { return val == null || val.isEmpty() ? null : importRepository.insertNomenklatur(val); }
    private Integer resolveJenisJf(String val) { return val == null || val.isEmpty() ? null : importRepository.insertJenisJf(val); }
    private Integer resolveJabatan(String val, String jenjang, Integer idNom, Integer idJf) { return val == null || val.isEmpty() ? null : importRepository.insertJabatan(val, jenjang, idNom, idJf); }
    private Integer resolveGolongan(String val) { return val == null || val.isEmpty() ? null : importRepository.insertGolongan(val); }
    private Integer resolveJenisDiklat(String val) { return val == null || val.isEmpty() ? null : importRepository.insertJenisDiklat(val); }

    public void delete(Long id) {
        jdbc.update("DELETE FROM asn WHERE id_asn = :id", new MapSqlParameterSource("id", id));
    }
}
