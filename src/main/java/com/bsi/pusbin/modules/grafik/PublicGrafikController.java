package com.bsi.pusbin.modules.grafik;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.modules.grafik.schema.ChartResponse;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/grafik")
@RequiredArgsConstructor
public class PublicGrafikController {

    private final GrafikService service;

    @GetMapping("/sebaran-asn-jenjang")
    public ResponseEntity<APIResponse<ChartResponse>> getSebaranAsnJenjang(FilterRequest request) {
        ChartResponse response = service.getSebaranAsnJenjang(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/persentase-gender")
    public ResponseEntity<APIResponse<ChartResponse>> getPersentaseGender(FilterRequest request) {
        ChartResponse response = service.getPersentaseGender(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/persentase-asn-jf-masn")
    public ResponseEntity<APIResponse<ChartResponse>> getPersentaseAsnJfMasn(FilterRequest request) {
        ChartResponse response = service.getPersentaseAsnJfMasn(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/sebaran-asn-jfmasn")
    public ResponseEntity<APIResponse<ChartResponse>> getSebaranAsnJfmasnInstansi(FilterRequest request) {
        ChartResponse response = service.getSebaranAsnJfmasnInstansi(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/sebaran-asn-klpd")
    public ResponseEntity<APIResponse<ChartResponse>> getSebaranAsnKlpd(FilterRequest request) {
        ChartResponse response = service.getSebaranAsnKlpd(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/sebaran-asn-jabatan")
    public ResponseEntity<APIResponse<ChartResponse>> getSebaranAsnJabatan(FilterRequest request) {
        ChartResponse response = service.getSebaranAsnJabatan(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/tren-kenaikan-jf")
    public ResponseEntity<APIResponse<ChartResponse>> getTrenKenaikanJf(FilterRequest request) {
        ChartResponse response = service.getTrenKenaikanJf(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/golongan-ruang")
    public ResponseEntity<APIResponse<ChartResponse>> getGolonganRuang(FilterRequest request) {
        ChartResponse response = service.getGolonganRuang(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/persentase-jf-masn")
    public ResponseEntity<APIResponse<ChartResponse>> getPersentaseJfMasn(FilterRequest request) {
        ChartResponse response = service.getPersentaseJfMasn(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/sebaran-kategori")
    public ResponseEntity<APIResponse<ChartResponse>> getSebaranKategori(FilterRequest request) {
        ChartResponse response = service.getSebaranKategori(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/masa-kerja-jabatan")
    public ResponseEntity<APIResponse<ChartResponse>> getMasaKerjaJabatan(FilterRequest request) {
        ChartResponse response = service.getMasaKerjaJabatan(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/masa-kerja-golongan")
    public ResponseEntity<APIResponse<ChartResponse>> getMasaKerjaGolongan(FilterRequest request) {
        ChartResponse response = service.getMasaKerjaGolongan(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }
}
