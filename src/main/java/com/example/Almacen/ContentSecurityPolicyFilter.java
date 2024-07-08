package com.example.Almacen;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentSecurityPolicyFilter implements Filter {
     @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("Content-Security-Policy", "default-src 'self'; " +
                "font-src 'self' https://cdnjs.cloudflare.com https://unpkg.com https://ka-f.fontawesome.com https://kit.fontawesome.com https://cdn.jsdelivr.net; " +
                "img-src 'self' https://trustedimages.example.com data:; " +
                "connect-src 'self' https://ka-f.fontawesome.com https://trustedapi.example.com https://cdnjs.cloudflare.com https://code.jquery.com https://cdn.jsdelivr.net https://www.google.com https://www.gstatic.com; " +
                "script-src 'self' 'unsafe-inline' https://code.jquery.com https://kit.fontawesome.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://www.gstatic.com https://www.google.com https://www.recaptcha.net; " +
                "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://unpkg.com https://ka-f.fontawesome.com https://cdn.jsdelivr.net https://www.gstatic.com; " +
                "frame-src 'self' https://www.google.com https://www.recaptcha.net");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code
    }
}
