import os

tables = {
    "JenisAsn": {"table": "jenis_asn", "pk": "id_jenis_asn", "fields": {"id_jenis_asn": "Integer", "nama_jenis": "String"}},
    "KedudukanAsn": {"table": "kedudukan_asn", "pk": "id_kedudukan", "fields": {"id_kedudukan": "Integer", "nama_kedudukan": "String"}},
    "JenisKelamin": {"table": "jenis_kelamin", "pk": "id_jenis_kelamin", "fields": {"id_jenis_kelamin": "Integer", "nama_kelamin": "String"}},
    "Nomenklatur": {"table": "nomenklatur", "pk": "id_nomenklatur", "fields": {"id_nomenklatur": "Integer", "nama_nomenklatur": "String"}},
    "JenisJf": {"table": "jenis_jf", "pk": "id_jenis_jf", "fields": {"id_jenis_jf": "Integer", "nama_jenis_jf": "String"}},
    "JenisDiklat": {"table": "jenis_diklat", "pk": "id_jenis_diklat", "fields": {"id_jenis_diklat": "Integer", "nama_jenis_diklat": "String"}},
    "Golongan": {"table": "golongan", "pk": "id_golongan", "fields": {"id_golongan": "Integer", "golongan_ruang": "String"}},
    "WilayahPokja": {"table": "wilayah_pokja", "pk": "id_wilayah_pokja", "fields": {"id_wilayah_pokja": "Integer", "nama_pokja": "String"}},
    "Pendidikan": {"table": "pendidikan", "pk": "id_pendidikan", "fields": {"id_pendidikan": "Integer", "tingkat": "String", "nama_pendidikan": "String"}},
    "WilayahBkn": {"table": "wilayah_bkn", "pk": "id_wilker", "fields": {"id_wilker": "Integer", "nama_wilker": "String", "no_urut": "Integer", "id_wilayah_pokja": "Integer"}},
    "Jabatan": {"table": "jabatan", "pk": "id_jabatan", "fields": {"id_jabatan": "Integer", "id_nomenklatur": "Integer", "id_jenis_jf": "Integer", "nama_jabatan": "String", "jenjang": "String"}},
    "Instansi": {"table": "instansi", "pk": "id_instansi", "fields": {"id_instansi": "Integer", "id_wilker": "Integer", "nama_instansi": "String", "kategori": "String", "jenis_instansi": "String"}},
    "Users": {"table": "users", "pk": "id", "fields": {"id": "Integer", "nip": "String", "password_hash": "String"}},
}

base_pkg = "com.bsi.pusbin.modules.input.master"
base_dir = r"D:\repository\skripsi-hana-ambar\pusbin-backend\src\main\java\com\bsi\pusbin\modules\input\master"
os.makedirs(base_dir, exist_ok=True)
os.makedirs(os.path.join(base_dir, "schema"), exist_ok=True)

def to_camel(s):
    parts = s.split('_')
    return parts[0] + ''.join(p.capitalize() for p in parts[1:])

for cls_name, info in tables.items():
    table = info["table"]
    pk = info["pk"]
    fields = info["fields"]
    
    # 1. Schema
    schema_code = f"package {base_pkg}.schema;\n\nimport lombok.Data;\n\n@Data\npublic class {cls_name}Dto {{\n"
    for f_col, f_type in fields.items():
        schema_code += f"    private {f_type} {to_camel(f_col)};\n"
    schema_code += "}\n"
    
    with open(os.path.join(base_dir, "schema", f"{cls_name}Dto.java"), "w") as f:
        f.write(schema_code)
        
    # 2. Repository
    repo_code = f"""package {base_pkg};

import {base_pkg}.schema.{cls_name}Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class {cls_name}Repository {{
    private final NamedParameterJdbcTemplate jdbc;

    private {cls_name}Dto mapRow(ResultSet rs, int rowNum) throws SQLException {{
        {cls_name}Dto dto = new {cls_name}Dto();
"""
    for f_col, f_type in fields.items():
        if f_type == "Integer":
            repo_code += f"""        int {to_camel(f_col)} = rs.getInt("{f_col}");
        if (!rs.wasNull()) dto.set{to_camel(f_col)[0].upper() + to_camel(f_col)[1:]}({to_camel(f_col)});
"""
        else:
            repo_code += f'        dto.set{to_camel(f_col)[0].upper() + to_camel(f_col)[1:]}(rs.getString("{f_col}"));\n'
    
    repo_code += f"""        return dto;
    }}

    public List<{cls_name}Dto> findAll() {{
        return jdbc.query("SELECT * FROM {table}", this::mapRow);
    }}

    public Optional<{cls_name}Dto> findById(Integer id) {{
        List<{cls_name}Dto> res = jdbc.query("SELECT * FROM {table} WHERE {pk} = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }}

    public Integer insert({cls_name}Dto dto) {{
        String sql = "INSERT INTO {table} (""";
    
    insert_cols = [c for c in fields.keys() if c != pk]
    repo_code += ", ".join(insert_cols) + ") VALUES (" + ", ".join([f":{to_camel(c)}" for c in insert_cols]) + ")\";\n"
    repo_code += "        MapSqlParameterSource params = new MapSqlParameterSource();\n"
    for c in insert_cols:
        repo_code += f'        params.addValue("{to_camel(c)}", dto.get{to_camel(c)[0].upper() + to_camel(c)[1:]}());\n'
        
    repo_code += f"""        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{{"{pk}"}});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }}

    public void update(Integer id, {cls_name}Dto dto) {{
        String sql = "UPDATE {table} SET """
    
    repo_code += ", ".join([f"{c} = :{to_camel(c)}" for c in insert_cols]) + f" WHERE {pk} = :id\";\n"
    repo_code += "        MapSqlParameterSource params = new MapSqlParameterSource(\"id\", id);\n"
    for c in insert_cols:
        repo_code += f'        params.addValue("{to_camel(c)}", dto.get{to_camel(c)[0].upper() + to_camel(c)[1:]}());\n'
    repo_code += f"""        jdbc.update(sql, params);
    }}

    public void delete(Integer id) {{
        jdbc.update("DELETE FROM {table} WHERE {pk} = :id", new MapSqlParameterSource("id", id));
    }}
}}
"""
    with open(os.path.join(base_dir, f"{cls_name}Repository.java"), "w") as f:
        f.write(repo_code)

    # 3. Service
    srv_code = f"""package {base_pkg};

import {base_pkg}.schema.{cls_name}Dto;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class {cls_name}Service {{
    private final {cls_name}Repository repository;

    @Transactional(readOnly = true)
    public List<{cls_name}Dto> findAll() {{
        return repository.findAll();
    }}

    @Transactional(readOnly = true)
    public {cls_name}Dto findById(Integer id) {{
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }}

    @Transactional
    public {cls_name}Dto create({cls_name}Dto dto) {{
        Integer id = repository.insert(dto);
        dto.set{to_camel(pk)[0].upper() + to_camel(pk)[1:]}(id);
        return dto;
    }}

    @Transactional
    public {cls_name}Dto update(Integer id, {cls_name}Dto dto) {{
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.set{to_camel(pk)[0].upper() + to_camel(pk)[1:]}(id);
        return dto;
    }}

    @Transactional
    public void delete(Integer id) {{
        findById(id); // ensure exists
        repository.delete(id);
    }}
}}
"""
    with open(os.path.join(base_dir, f"{cls_name}Service.java"), "w") as f:
        f.write(srv_code)

    # 4. Controller
    ctrl_code = f"""package {base_pkg};

import {base_pkg}.schema.{cls_name}Dto;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master/{table.replace('_', '-')}")
@RequiredArgsConstructor
public class {cls_name}Controller {{
    private final {cls_name}Service service;

    @GetMapping
    public ResponseEntity<APIResponse<List<{cls_name}Dto>>> findAll() {{
        return ResponseEntity.ok(APIResponse.ok(service.findAll(), "Success"));
    }}

    @GetMapping("/{{id}}")
    public ResponseEntity<APIResponse<{cls_name}Dto>> findById(@PathVariable Integer id) {{
        return ResponseEntity.ok(APIResponse.ok(service.findById(id), "Success"));
    }}

    @PostMapping
    public ResponseEntity<APIResponse<{cls_name}Dto>> create(@RequestBody {cls_name}Dto dto) {{
        return ResponseEntity.ok(APIResponse.ok(service.create(dto), "Created"));
    }}

    @PutMapping("/{{id}}")
    public ResponseEntity<APIResponse<{cls_name}Dto>> update(@PathVariable Integer id, @RequestBody {cls_name}Dto dto) {{
        return ResponseEntity.ok(APIResponse.ok(service.update(id, dto), "Updated"));
    }}

    @DeleteMapping("/{{id}}")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Integer id) {{
        service.delete(id);
        return ResponseEntity.ok(APIResponse.ok(null, "Deleted"));
    }}
}}
"""
    with open(os.path.join(base_dir, f"{cls_name}Controller.java"), "w") as f:
        f.write(ctrl_code)

print("Generation complete!")
