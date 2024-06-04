package com.example.Almacen;

import java.io.IOException;

import com.example.Almacen.Usuario.UsuarioControlador;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SessionTimeoutFilter implements Filter{
    private static final long SESSION_TIMEOUT_MS = 1 * 60 * 1000; // 30 minutos

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización si es necesario
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            Long sessionStartTime = (Long) session.getAttribute("sessionStartTime");
            if (sessionStartTime != null) {
                long sessionDuration = System.currentTimeMillis() - sessionStartTime;
                if (sessionDuration > SESSION_TIMEOUT_MS) {
                    // Eliminar usuario de sesiones activas
                    String correoUsuario = (String) session.getAttribute("correoUsuario");
                    String correoUsuarioC = (String) session.getAttribute("correoUsuarioC");

                    if (correoUsuario != null) {
                        UsuarioControlador.sesionesActivasA.remove(correoUsuario);
                    }
                    if (correoUsuarioC != null) {
                        UsuarioControlador.sesionesActivasU.remove(correoUsuarioC);
                    }

                    session.setAttribute("sessionExpired", true);
                    session.invalidate();
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/VolverLogin");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup si es necesario
    }
}
