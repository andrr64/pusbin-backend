package com.bsi.pusbin.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bsi.pusbin.shared.response.APIResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * AuthGuard — filter keamanan yang melindungi SEMUA endpoint secara default.
 *
 * Filter ini berjalan SETELAH JwtFilter, sehingga SecurityContext sudah terisi
 * jika token valid. AuthGuard kemudian memblokir request yang tidak terotentikasi
 * ke endpoint yang tidak ada dalam daftar PUBLIC_PATHS dengan respons 401.
 *
 * Cara kerja:
 * 1. Jika path termasuk PUBLIC_PATHS → lewatkan tanpa memeriksa token.
 * 2. Jika path bukan PUBLIC_PATHS dan SecurityContext sudah berisi Authentication
 *    yang valid (diisi oleh JwtFilter) → lewatkan.
 * 3. Selain itu (token tidak ada / tidak valid) → return 401 Unauthorized.
 *
 * CATATAN UNTUK MAHASISWA:
 * - Endpoint IAM publik (login, register, refresh) dikecualikan agar bisa
 *   diakses tanpa token.
 * - Endpoint lain WAJIB membawa cookie `access_token` yang valid.
 * - Untuk menambahkan endpoint publik baru, cukup tambahkan path-nya ke PUBLIC_PATHS.
 */
@Component
@RequiredArgsConstructor
public class AuthGuard extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    /**
     * Daftar path yang dapat diakses secara publik tanpa token.
     * Pencocokan dilakukan menggunakan prefix (startsWith).
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/iam/login",
            "/api/v1/iam/register",
            "/api/v1/iam/refresh"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 1. Lewatkan request OPTIONS (preflight CORS) tanpa pemeriksaan auth
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Lewatkan endpoint publik yang tidak memerlukan token
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Periksa apakah SecurityContext memiliki autentikasi yang valid
        //    (diisi oleh JwtFilter jika token di cookie valid)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Tidak ada token atau token tidak valid → 401 Unauthorized
        writeUnauthorizedResponse(response);
    }

    /**
     * Memeriksa apakah path request termasuk dalam daftar endpoint publik.
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * Menulis respons 401 Unauthorized dalam format JSON standar API.
     */
    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), APIResponse.error("Unauthorized: token tidak ditemukan atau tidak valid"));
    }
}
