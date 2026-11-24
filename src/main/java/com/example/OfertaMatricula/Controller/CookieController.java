package com.example.OfertaMatricula.Controller;

import java.time.Duration;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CookieController {

    @PostMapping("/set")
    public String definirCookie(
            @RequestParam String tema,
            @RequestParam(defaultValue = "/") String redirectTo,
            HttpServletResponse response) {

        
        if (!tema.equals("claro") && !tema.equals("escuro")) {
            tema = "claro";
        }

        
        ResponseCookie cookieTema = ResponseCookie.from("tema", tema)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .build();

      
        String metadata = URLEncoder.encode(
                "tema=" + tema,
                StandardCharsets.UTF_8
        );

        ResponseCookie cookieMeta = ResponseCookie.from("tema_meta", metadata)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .httpOnly(false)
                .secure(false)
                .sameSite("Lax")
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookieTema.toString()
        );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookieMeta.toString()
        );

        if (!redirectTo.startsWith("/") || redirectTo.startsWith("//")) {
            redirectTo = "/";
        }

        return "redirect:" + redirectTo;
    }
}
