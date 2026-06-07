import os

cls_name = "TotalAsnPeriode"
table = "total_asn_periode_by_nama_jabatan"
pk = "id"
fields = {"id": "Long", "jumlah_asn": "Integer", "periode": "java.time.LocalDate", "id_jabatan": "Integer"}

base_pkg = "com.bsi.pusbin.modules.input.master"
base_dir = r"D:\repository\skripsi-hana-ambar\pusbin-backend\src\main\java\com\bsi\pusbin\modules\input\master"
os.makedirs(base_dir, exist_ok=True)
os.makedirs(os.path.join(base_dir, "schema"), exist_ok=True)

def to_camel(s):
    parts = s.split('_')
    return parts[0] + ''.join(p.capitalize() for p in parts[1:])

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
    elif f_type == "Long":
        repo_code += f"""        long {to_camel(f_col)} = rs.getLong("{f_col}");
        if (!rs.wasNull()) dto.set{to_camel(f_col)[0].upper() + to_camel(f_col)[1:]}({to_camel(f_col)});
"""
    elif f_type == "java.time.LocalDate":
        repo_code += f"""        java.sql.Date {to_camel(f_col)}Date = rs.getDate("{f_col}");
        if ({to_camel(f_col)}Date != null) dto.set{to_camel(f_col)[0].upper() + to_camel(f_col)[1:]}({to_camel(f_col)}Date.toLocalDate());
"""
    else:
        repo_code += f'        dto.set{to_camel(f_col)[0].upper() + to_camel(f_col)[1:]}(rs.getString("{f_col}"));\n'

repo_code += f"""        return dto;
    }}

    public List<{cls_name}Dto> findAll() {{
        return jdbc.query("SELECT * FROM {table}", this::mapRow);
    }}

    public Optional<{cls_name}Dto> findById(Long id) {{
        List<{cls_name}Dto> res = jdbc.query("SELECT * FROM {table} WHERE {pk} = :id", new MapSqlParameterSource("id", id), this::mapRow);
        return res.stream().findFirst();
    }}

    public Long insert({cls_name}Dto dto) {{
        String sql = "INSERT INTO {table} ("""

insert_cols = [c for c in fields.keys() if c != pk]
repo_code += ", ".join(insert_cols) + ") VALUES (" + ", ".join([f":{to_camel(c)}" for c in insert_cols]) + ")\";\n"
repo_code += "        MapSqlParameterSource params = new MapSqlParameterSource();\n"
for c in insert_cols:
    repo_code += f'        params.addValue("{to_camel(c)}", dto.get{to_camel(c)[0].upper() + to_camel(c)[1:]}());\n'
    
repo_code += f"""        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{{"{pk}"}});
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }}

    public void update(Long id, {cls_name}Dto dto) {{
        String sql = "UPDATE {table} SET """

repo_code += ", ".join([f"{c} = :{to_camel(c)}" for c in insert_cols]) + f" WHERE {pk} = :id\";\n"
repo_code += "        MapSqlParameterSource params = new MapSqlParameterSource(\"id\", id);\n"
for c in insert_cols:
    repo_code += f'        params.addValue("{to_camel(c)}", dto.get{to_camel(c)[0].upper() + to_camel(c)[1:]}());\n'
repo_code += f"""        jdbc.update(sql, params);
    }}

    public void delete(Long id) {{
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
    public {cls_name}Dto findById(Long id) {{
        return repository.findById(id).orElseThrow(() -> new BusinessException("Data not found"));
    }}

    @Transactional
    public {cls_name}Dto create({cls_name}Dto dto) {{
        Long id = repository.insert(dto);
        dto.set{to_camel(pk)[0].upper() + to_camel(pk)[1:]}(id);
        return dto;
    }}

    @Transactional
    public {cls_name}Dto update(Long id, {cls_name}Dto dto) {{
        findById(id); // ensure exists
        repository.update(id, dto);
        dto.set{to_camel(pk)[0].upper() + to_camel(pk)[1:]}(id);
        return dto;
    }}

    @Transactional
    public void delete(Long id) {{
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
    public ResponseEntity<APIResponse<{cls_name}Dto>> findById(@PathVariable Long id) {{
        return ResponseEntity.ok(APIResponse.ok(service.findById(id), "Success"));
    }}

    @PostMapping
    public ResponseEntity<APIResponse<{cls_name}Dto>> create(@RequestBody {cls_name}Dto dto) {{
        return ResponseEntity.ok(APIResponse.ok(service.create(dto), "Created"));
    }}

    @PutMapping("/{{id}}")
    public ResponseEntity<APIResponse<{cls_name}Dto>> update(@PathVariable Long id, @RequestBody {cls_name}Dto dto) {{
        return ResponseEntity.ok(APIResponse.ok(service.update(id, dto), "Updated"));
    }}

    @DeleteMapping("/{{id}}")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Long id) {{
        service.delete(id);
        return ResponseEntity.ok(APIResponse.ok(null, "Deleted"));
    }}
}}
"""
with open(os.path.join(base_dir, f"{cls_name}Controller.java"), "w") as f:
    f.write(ctrl_code)

print("Generation total_asn_periode complete!")
