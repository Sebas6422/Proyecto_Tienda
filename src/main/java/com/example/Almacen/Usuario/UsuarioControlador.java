package com.example.Almacen.Usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Almacen.Encriptador.Hash;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/usuario/")
@Controller
public class UsuarioControlador {
    @Autowired
    private IUsuarioService service;

    public static Map<String, String> sesionesActivasA = new HashMap<>();
    public static Map<String, String> sesionesActivasU = new HashMap<>();
    private static Map<String, Integer> intentosFallidos = new HashMap<>();
    private static Map<String, Long> tiempoBloqueo = new HashMap<>();
    private static final int MAX_INTENTOS = 3;
    private static final long BLOQUEO_TIEMPO_MS = 1 * 60 * 1000; // 1 minuto

    @GetMapping("/usuarios")
    public String Mostrar(Model model, HttpSession session) {
        String correoUsuario = (String) session.getAttribute("correoUsuario");
        if (correoUsuario == null || !sesionActivaA(correoUsuario)) {
            return "redirect:/";
        }
        return "aDashboard";
    }

    public Map<String, String> getSesionesA() {
        return sesionesActivasA;
    }

    public static boolean sesionActivaA(String correoUsuario) {
        return sesionesActivasA.containsKey(correoUsuario);
    }

    public Map<String, String> getSesionesU() {
        return sesionesActivasU;
    }

    public static boolean sesionActivaU(String correoUsuario) {
        return sesionesActivasU.containsKey(correoUsuario);
    }

    @GetMapping("/ingresarLogin")
    public String mostrarLogin() {
        // Redirige a la página de inicio de sesión si se accede a la ruta con un método GET
        return "redirect:/Login";
    }

    @PostMapping("/ingresarLogin")
    public String verificarLogin(@RequestParam(value = "us_correo", required = false) String correo,
                                 @RequestParam(value = "us_contrasenha", required = false) String contra,
                                 Model model, HttpSession session) {

        if (correo == null || correo.isEmpty() || contra == null || contra.isEmpty()) {
            model.addAttribute("error", "Los campos 'Correo' y 'Contraseña' son obligatorios.");
            return "Login";
        }

        if (!correoCorrecto(correo)) {
            model.addAttribute("error", "El campo 'Correo' debe ser un correo válido.");
            return "Login";
        }

        // Verificar si el usuario está bloqueado
        if (tiempoBloqueo.containsKey(correo)) {
            long tiempoRestante = (System.currentTimeMillis() - tiempoBloqueo.get(correo)) / 1000;
            if (tiempoRestante < BLOQUEO_TIEMPO_MS / 1000) {
                model.addAttribute("error", "Cuenta bloqueada. Inténtelo de nuevo en " + (BLOQUEO_TIEMPO_MS / 1000 - tiempoRestante) + " segundos.");
                return "Login";
            } else {
                // El periodo de bloqueo ha expirado
                tiempoBloqueo.remove(correo);
                intentosFallidos.remove(correo);
            }
        }

        List<Usuario> usuarios = service.Listar();
        Hash hash = new Hash();
        contra = hash.StringToHash(contra, "SHA256");

        boolean userFound = false;

        for (Usuario usuario : usuarios) {
            if (usuario.getUs_correo().equals(correo)) {
                userFound = true;
                if (usuario.getUs_contrasenha().equals(contra)) {
                    if (sesionActivaA(correo) || sesionActivaU(correo)) {
                        model.addAttribute("error", "Esta cuenta ya está logueada.");
                        return "Login";
                    }

                    if (usuario.getRol().getRol_id() == 1) {
                        String tokenSesion = UUID.randomUUID().toString();
                        sesionesActivasA.put(correo, tokenSesion);
                        session.setAttribute("correoUsuario", correo);
                        session.setAttribute("tokenSesion", tokenSesion);
                        session.setAttribute("sessionStartTime", System.currentTimeMillis());
                        // Reiniciar los intentos fallidos al ingresar exitosamente al login
                        intentosFallidos.remove(correo);
                        return Mostrar(model, session);
                    } else if (usuario.getRol().getRol_id() == 2) {
                        // Verifica al usuario
                        String tokenSesion = UUID.randomUUID().toString();
                        sesionesActivasU.put(correo, tokenSesion);
                        session.setAttribute("correoUsuarioC", correo);
                        session.setAttribute("tokenSesionC", tokenSesion);
                        // Reiniciar los intentos fallidos al ingresar exitosamente al login
                        intentosFallidos.remove(correo);
                        return "uLoginUsuario";
                    } else if (usuario.getRol().getRol_id() == 3) {
                        return "vIndexVendedor";
                    }
                }
            }
        }

        if (!userFound) {
            // No se encontró el usuario
            model.addAttribute("error", "Correo o contraseña incorrecta");
            return "redirect:Login";
        }

        // Manejar intentos fallidos
        intentosFallidos.put(correo, intentosFallidos.getOrDefault(correo, 0) + 1);
        if (intentosFallidos.get(correo) >= MAX_INTENTOS) {
            tiempoBloqueo.put(correo, System.currentTimeMillis());
            model.addAttribute("error", "Cuenta bloqueada debido a demasiados intentos fallidos. Inténtelo en 1 minuto.");
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos. Intento " + intentosFallidos.get(correo) + " de " + MAX_INTENTOS + ".");
        }
        return "Login";
    }


    @GetMapping("/cerrarSesionA")
    public String cerrarSesionA(HttpSession session) {
        if (verificarTokenSesionA(session.getAttribute("correoUsuario").toString(),  session.getAttribute("tokenSesion").toString())) {
            // Eliminar el token de sesión asociado al usuario
            sesionesActivasA.remove(session.getAttribute("correoUsuario").toString());
            session.invalidate(); // Invalida la sesión
            return "redirect:/";
        } else {
            return "redirect:/aProveedores";
        }
    }

    @GetMapping("/cerrarSesionU")
    public String cerrarSesionU(HttpSession session) {
        if (verificarTokenSesionU(session.getAttribute("correoUsuarioC").toString(),  session.getAttribute("tokenSesionC").toString())) {
            // Eliminar el token de sesión asociado al usuario
            sesionesActivasU.remove(session.getAttribute("correoUsuarioC").toString());
            session.invalidate(); // Invalida la sesión
            return "redirect:/";
        } else {
            return "redirect:/aProveedores";
        }
    }

    // Método para verificar si un token de sesión es válido
    public static boolean verificarTokenSesionA(String correoA, String tokenA) {
        String tokenGuardadoA = sesionesActivasA.get(correoA);
        return tokenGuardadoA != null && tokenGuardadoA.equals(tokenA);
    }

    public static boolean verificarTokenSesionU(String correoU, String tokenU) {
        String tokenGuardadoU = sesionesActivasU.get(correoU);
        return tokenGuardadoU != null && tokenGuardadoU.equals(tokenU);
    }

    private boolean correoCorrecto(String correo) {
        if (correo.length() > 40) {
            return false;
        }
        
        // Expresión regular para verificar que el correo termina en @gmail.com
        Pattern p = Pattern.compile("^[\\w.%+-]+@gmail\\.com$");
        Matcher m = p.matcher(correo);
        return m.find();
    }
}
