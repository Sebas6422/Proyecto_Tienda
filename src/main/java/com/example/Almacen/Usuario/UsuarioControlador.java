package com.example.Almacen.Usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Almacen.Encriptador.Hash;
import com.example.Almacen.Proveedor.ProveedorControlador;
import com.example.Almacen.Rol.IRol;
import com.example.Almacen.Rol.Rol;

import ch.qos.logback.classic.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;


@RequestMapping("/usuario/")
@Controller
public class UsuarioControlador {
    @Autowired
    private IUsuarioService service;

    @Autowired
    private IRol iRol;

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
        return "redirect:/aDashboard";
    }

    public Map<String, String> getSesionesA() {
        return sesionesActivasA;
    }

    public static boolean sesionActivaA(String correoUsuario) {
        return sesionesActivasA.containsKey(correoUsuario);
    }

    @GetMapping("/ingresarLogin")
    public String mostrarLogin(HttpSession session, Model model) {
        // Transferir los mensajes de sesión al modelo
        if (session.getAttribute("error") != null) {
            model.addAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        if (session.getAttribute("success") != null) {
            model.addAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("errorR") != null) {
            model.addAttribute("errorR", session.getAttribute("errorR"));
            session.removeAttribute("errorR");
        }
        return "login"; // Asegúrate de que este nombre coincida con tu vista de login
    }


    @PostMapping("/ingresarLogin")
    public String verificarLogin(@RequestParam(value = "us_correo", required = false) String correo,
                                 @RequestParam(value = "us_contrasenha", required = false) String contra,
                                 Model model, HttpSession session) {

        if (correo == null || correo.isEmpty() || contra == null || contra.isEmpty()) {
            session.setAttribute("error", "Los campos 'Correo' y 'Contraseña' son obligatorios.");
            return "redirect:/usuario/ingresarLogin";
        }

        if (!correoCorrecto(correo)) {
            session.setAttribute("error", "El campo 'Correo' debe ser un correo válido.");
            return "redirect:/usuario/ingresarLogin";
        }

        // Verificar si el usuario está bloqueado
        if (tiempoBloqueo.containsKey(correo)) {
            long tiempoRestante = (System.currentTimeMillis() - tiempoBloqueo.get(correo)) / 1000;
            if (tiempoRestante < BLOQUEO_TIEMPO_MS / 1000) {
                session.setAttribute("error", "Cuenta bloqueada. Inténtelo de nuevo en " + (BLOQUEO_TIEMPO_MS / 1000 - tiempoRestante) + " segundos.");
                return "redirect:/usuario/ingresarLogin";
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
                    Usuario usuarioLo = usuario;
                    if (sesionActivaA(correo)) {
                        session.setAttribute("error", "Esta cuenta ya está logueada.");
                        return "redirect:/usuario/ingresarLogin";
                    }

                    if (usuario.getRol().getRol_id() == 1) {
                        String tokenSesion = UUID.randomUUID().toString();
                        //guardamos la sesion del usuario con su objeto y tambien su token de sesion
                        session.setAttribute("Usuario", usuarioLo);
                        session.setAttribute("tokenSesion", tokenSesion);
                        sesionesActivasA.put(usuarioLo.getUs_correo(), tokenSesion);
                        
                        session.setAttribute("sessionStartTime", System.currentTimeMillis());
                        // Reiniciar los intentos fallidos al ingresar exitosamente al login
                        intentosFallidos.remove(usuarioLo.getUs_correo());
                        return "redirect:/AdminDashIn";
                    } else if (usuario.getRol().getRol_id() == 2) {
                        // Verifica al usuario
                        String tokenSesion = UUID.randomUUID().toString();
                        //guardamos la sesion del usuario con su objeto y tambien su token de sesion
                        session.setAttribute("Usuario", usuarioLo);
                        session.setAttribute("tokenSesion", tokenSesion);
                        sesionesActivasA.put(usuarioLo.getUs_correo(), tokenSesion);
                        
                        session.setAttribute("sessionStartTime", System.currentTimeMillis());
                        // Reiniciar los intentos fallidos al ingresar exitosamente al login
                        intentosFallidos.remove(usuarioLo.getUs_correo());
                        return "redirect:/Cliente";
                    } else if (usuario.getRol().getRol_id() == 3) {
                        String tokenSesion = UUID.randomUUID().toString();
                        //guardamos la sesion del usuario con su objeto y tambien su token de sesion
                        session.setAttribute("Usuario", usuarioLo);
                        session.setAttribute("tokenSesion", tokenSesion);
                        sesionesActivasA.put(usuarioLo.getUs_correo(), tokenSesion);
                        
                        session.setAttribute("sessionStartTime", System.currentTimeMillis());
                        // Reiniciar los intentos fallidos al ingresar exitosamente al login
                        intentosFallidos.remove(usuarioLo.getUs_correo());
                        return "redirect:/VendedorBienvenida";
                    }
                }
            }
        }

        if (!userFound) {
            // No se encontró el usuario
            session.setAttribute("error", "Correo o contraseña incorrecta");
            return "redirect:/usuario/ingresarLogin";
        }

        // Manejar intentos fallidos
        intentosFallidos.put(correo, intentosFallidos.getOrDefault(correo, 0) + 1);
        if (intentosFallidos.get(correo) >= MAX_INTENTOS) {
            tiempoBloqueo.put(correo, System.currentTimeMillis());
            session.setAttribute("error", "Cuenta bloqueada debido a demasiados intentos fallidos. Inténtelo en 1 minuto.");
        } else {
            session.setAttribute("error", "Correo o contraseña incorrectos. Intento " + intentosFallidos.get(correo) + " de " + MAX_INTENTOS + ".");
        }
        return "redirect:/usuario/ingresarLogin";
    }


    @GetMapping("/cerrarSesion")
    public String cerrarSesion(HttpSession session) {
        Usuario usua  = (Usuario) session.getAttribute("Usuario");
        String correoU = usua.getUs_correo();
        String tokenU = session.getAttribute("tokenSesion").toString();
        if (verificarTokenSesionA(correoU,  tokenU)) {
            // Eliminar el token de sesión asociado al usuario
            sesionesActivasA.remove(correoU);
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

    //Validaciones
    private boolean correoCorrecto(String correo) {
        if (correo.length() > 40) {
            return false;
        }
        
        // Expresión regular para verificar que el correo termina en @gmail.com
        Pattern p = Pattern.compile("^[\\w.%+-]+@gmail\\.com$");
        Matcher m = p.matcher(correo);
        return m.find();
    }


    @PostMapping("/registrarCliente")
    public String registrarCliente(@RequestParam("dni") String us_dni,
                                @RequestParam("nombre") String us_nombre,
                                @RequestParam("apellido") String us_apellido,
                                @RequestParam("correo") String us_correo,
                                @RequestParam("hashedPassword") String us_clave,
                                @RequestParam("direccion") String us_direccion,
                                @RequestParam("telefono") String us_telefono,
                                Model model, HttpSession session) {
        Usuario usuario = new Usuario();

        boolean dnidV = dnid(us_dni);
        boolean correoV = correo(us_correo);
        boolean nombreV = nombre(us_nombre);
        boolean apellidoV = apellido(us_apellido);
        boolean direccionV = direccion(us_direccion);
        boolean celularV = celular(us_telefono);

        if(correoV && dnidV && nombreV && apellidoV && direccionV && celularV){
            usuario.setUs_contrasenha(us_clave);            
            usuario.setUs_dni(us_dni);                            
            usuario.setUs_nombre(us_nombre);
            usuario.setUs_apellido(us_apellido);
            usuario.setUs_correo(us_correo);
            usuario.setUs_direccion(us_direccion);
            usuario.setUs_telefono(us_telefono);
            
            // Buscamos y asignamos el rol al Cliente
            Rol rolCliente = iRol.findById(2)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            usuario.setRol(rolCliente);

            service.Guardar(usuario);
            session.setAttribute("success", "El cliente se registró con éxito.");
        } else {
            session.setAttribute("errorR", "Error al registrar cliente");
        }
        return "redirect:/usuario/ingresarLogin";
    }


    private static Logger logger = (Logger) LoggerFactory.getLogger(ProveedorControlador.class);
    //USUARIO
     
    public static boolean correo(String correo){
        if(correo==null || correo.isEmpty()){
            return false;
        }       

        if(!correo.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")){
            return false;
        }
            
        if (correo.matches("^[0-9]+@gmail\\.com")) {
            return false; 
        }
        logger.info("Correo Verdadero");
        return true;
    }

    //DNI
    public static boolean dnid(String dni) {
      
        if (dni == null || dni.isEmpty()) {
            return false;
        }    
        logger.info("dni Verdadero");
        return dni.matches("^\\d{8}$");
    }
   //nombre
    public static boolean nombre(String nombre) {
    
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        logger.info("nombre");
        return nombre.matches("^[a-zA-Z\\s]+$");
    }
    //apellido
     public static boolean apellido(String apellido) {
        if (apellido == null || apellido.isEmpty()) {
            return false;
        }
        logger.info("apellido");
        return apellido.matches("^[a-zA-Z\\s]+$");
    }
     
     //direccion
     public static boolean direccion(String dir) {
        if (dir == null || dir.isEmpty()) {
            return false;
        }
        logger.info("direccion");
        return dir.matches("^[A-Za-z0-9\\s.,-]+$");
    }
    //celular
      public static boolean celular(String celular) {
        if (celular == null || celular.isEmpty()) {
            return false;
        }    
        logger.info("celular");
        return celular.matches("^9\\d{8}$");
    }
    
    
}
