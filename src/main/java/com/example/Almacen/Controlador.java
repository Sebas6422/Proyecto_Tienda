package com.example.Almacen;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Almacen.Carrito.Carrito;
import com.example.Almacen.Carrito.ICarritoService;
import com.example.Almacen.Categoria_Producto.Categoria_Producto;
import com.example.Almacen.Categoria_Producto.ICategoria_ProductoService;
import com.example.Almacen.Pedido.IPedidoService;
import com.example.Almacen.Pedido.Pedido;
import com.example.Almacen.Producto.IProductoService;
import com.example.Almacen.Producto.Producto;
import com.example.Almacen.Proveedor.IProveedorService;
import com.example.Almacen.Proveedor.Proveedor;
import com.example.Almacen.Usuario.IUsuarioService;
import com.example.Almacen.Usuario.Usuario;
import com.example.Almacen.Usuario.UsuarioControlador;

import jakarta.servlet.http.HttpSession;

@Controller
@CrossOrigin(origins = "https://bodega-janys.azurewebsites.net/")
public class Controlador {

    @Autowired
    private IProveedorService service;
    @Autowired
    private IProductoService serviceP;
    @Autowired
    private ICategoria_ProductoService serviceC;
    @Autowired
    private IUsuarioService serviceU;
    @Autowired
    private ICarritoService serviceCa;
    @Autowired
    private IPedidoService servicePedido;

    @GetMapping("/")
    public String inicio() {
        return "uIndex";
    }

    @GetMapping("/api/data")
    public String getData() {
        // Lógica para obtener datos
        return "Datos protegidos por CORS";
    }

    @GetMapping("Nosotros")
    public String nosotros() {
        return "uNosotros";  
    }

    @GetMapping("/Login")
    public String login(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        String retorno = "Login";
        if (usuario == null) {
            return retorno;
        }else{
            switch (usuario.getRol().getRol_id()) {
                case 1:
                    retorno = "redirect:/AdminDashIn";
                    break;
                case 2:
                    retorno = "redirect:/Cliente";
                    break;
                case 3:
                    retorno = "redirect:/VendedorBienvenida";
                    break;    
                default:
                    break;
            }
            return retorno;
        }
    }

     // Método para redirigir rutas incorrectas
     @GetMapping("/usuario/Login")
     public String redirectLogin() {
         return "redirect:/Login";
     }

    @GetMapping("/CarritoP")
    public String carrito_principal(HttpSession session, Model model){
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        String volver = "Login";
        if(usuario != null){
            if (usuario.getRol().getRol_id() != 1 && usuario.getRol().getRol_id() != 3) {
                // para mostrar el carrito del cliente actual
                List<Carrito> carritos = serviceCa.Listar();
                List<Carrito> carritoC = new ArrayList<>();
                for (Carrito carr : carritos) {
                    if (carr.getUsu().getUs_id() == usuario.getUs_id() && carr.getEstado().getEstado_id() == 1) {
                        carritoC.add(carr);
                    }
                }
                model.addAttribute("carritosC", carritoC);
                volver = "uCarritoCliente";
            } 
        }
        return volver;
    }

    @GetMapping("/Cliente")
    public String cliente_index(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        List<Producto> productos = serviceP.Listar();
        model.addAttribute("productos", productos); 

        return "uLoginUsuario";
    }

    @GetMapping("/PagoCliente")
    public String pago_cliente(HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        return "uPago";
    }

    @GetMapping("/VolverLogin")
    public String volverLogin(Model model) {
        model.addAttribute("error", "Sesión expirada");
        return "Login";
    }

    //CONTROLADORES PARA ADMINISTRACION
    @GetMapping("/AdminDashIn")
    public String admin_indexDash(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }
        List<Usuario> usuarios = serviceU.Listar();
        List<Producto> productos = serviceP.Listar();
        int totalProductos = productos.size(); 
        List<Producto> productoStock = productos.stream()
                                        .filter(p -> p.getProduc_stock() > 0)
                                        .collect(Collectors.toList());

        double totalVentas = servicePedido.Listar().stream()
                                        .filter(p -> p.getEstado().getEstado_id() == 3)
                                        .mapToDouble(Pedido::getPedido_total) // Utiliza mapToDouble para obtener un DoubleStream
                                        .sum();
    
        
        // Contar productos con stock y sin stock
        long productosConStock = productoStock.size();

        // Calcular los porcentajes
        double porcentajeConStock = ((double) productosConStock / totalProductos) * 100;
        // Formatear el porcentaje a dos decimales
        DecimalFormat df = new DecimalFormat("0.00");
        String porcentajeConStockFormateado = df.format(porcentajeConStock);  
        String totalGanado = df.format(totalVentas);

        model.addAttribute("porcentajeConStock", porcentajeConStockFormateado);
        model.addAttribute("totalVentas", totalGanado);
        model.addAttribute("productos", productoStock);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarios);
        return "aIndexDashboard";  
    }

    @GetMapping("/a_Dashboard")
    public String dashboardA(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }
        model.addAttribute("usuario", usuario);
        return "aDashboard"; 
    }

    @GetMapping("/AdminUsuarios")
    public String admin_usuarios(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        List<Usuario> usuarios = serviceU.Listar();
        model.addAttribute("clientes", usuarios);
        model.addAttribute("usuario", usuario);
        return "aUsuarios";  
    }

    @GetMapping("/AdminProductos")
    public String admin_productos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        List<Producto> productos = serviceP.Listar();
        model.addAttribute("productos", productos); 
        model.addAttribute("usuario", usuario);
        return "aProductos";
    }

    @GetMapping("/AdminProductRegistro")
    public String admin_product_registro(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        List<Categoria_Producto> cat_productos = serviceC.Listar();
        List<Proveedor> proveedores = service.Listar();
        model.addAttribute("cat_productos", cat_productos);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("producto", new Producto()); // Assuming you have a Producto class
        model.addAttribute("usuario", usuario);
        return "aProductos_registrar";
    }

    @GetMapping("/AdminProveedores")
    public String admin_proveedores(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        List<Proveedor> proveedores = service.Listar();
        model.addAttribute("proveedores", proveedores); 
        model.addAttribute("usuario", usuario);
        return "aProveedores";  
    }

    @GetMapping("/AdminProvRegistro")
    public String admin_prov_registro(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }
        model.addAttribute("usuario", usuario);
        return "aProveedor_registrar";
    }

    @GetMapping("/AdminVentas")
    public String admin_ventas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }
        List<Pedido> pedidos = servicePedido.Listar().stream()
                                    .filter(p -> p.getEstado().getEstado_id() == 3)
                                    .collect(Collectors.toList());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);
        return "aVentas";
    }

    @GetMapping("/AdminPedidos")
    public String admin_pedidos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }
        List<Pedido> pedidos = servicePedido.Listar().stream()
                                .filter(p -> p.getEstado().getEstado_id() == 4)
                                .collect(Collectors.toList());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);
        return "aPedidos";
    }

    @GetMapping("/VendedorBienvenida")
    public String v_bienvenida(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        model.addAttribute("usuario", usuario);
        return "vBienvenida"; 
    }

    @GetMapping("/VendedorClientes")
    public String v_clientes(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        List<Usuario> usuarios = serviceU.Listar();
        List<Usuario> usuariosC = new ArrayList<>();
        for (Usuario usu : usuarios) {
            if (usu.getRol().getRol_id() == 2) {
                usuariosC.add(usu);
            }
        }
        model.addAttribute("clientes", usuariosC);
        model.addAttribute("usuario", usuario);
        return "vClientes"; 
    }

    @GetMapping("/VendedorProductos")
    public String v_productos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        List<Producto> productos = serviceP.Listar();
        model.addAttribute("productos", productos); 
        model.addAttribute("usuario", usuario);
        return "vProductos"; 
    }

    @GetMapping("/VendedorPuntoVenta")
    public String v_puntoventa(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }

        List<Usuario> usuarios = serviceU.Listar().stream()
                                    .filter(u -> u.getRol().getRol_id() == 2)
                                    .collect(Collectors.toList());
        List<Producto> productos = serviceP.Listar().stream()
                                        .filter(p -> p.getProduc_stock() > 0)
                                        .collect(Collectors.toList());

        List<Carrito> carritos = serviceCa.Listar().stream()
                                        .filter(c -> c.getUsu() != null && c.getUsu().getUs_id() == usuario.getUs_id() && c.getEstado().getEstado_id() == 1)    
                                        .collect(Collectors.toList());
                                  
        
        model.addAttribute("productos", productos);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", usuario);
        model.addAttribute("carritos", carritos);
        return "vPuntoVenta"; 
    }

    @GetMapping("/VendedorVentas")
    public String v_ventas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }
        List<Pedido> pedidos = servicePedido.Listar().stream()
                                    .filter(p -> p.getEstado().getEstado_id() == 3)
                                    .collect(Collectors.toList());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);
        return "vVentas"; 
    }

    @GetMapping("/VendedorPedidos")
    public String v_pedidos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        if (usuario == null) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }

        String correoString = usuario.getUs_correo();
        String tokeString = (String) session.getAttribute("tokenSesion");

        if (tokeString == null || tokeString.isEmpty()) {
            return "redirect:/"; // Redirige al login si no hay token de sesión
        }

        boolean inicio = UsuarioControlador.verificarTokenSesionA(correoString, tokeString);
        if (!inicio) {
            return "redirect:/"; // Redirige al login si el token no es válido
        }
        List<Pedido> pedidos = servicePedido.Listar().stream()
                                .filter(p -> p.getEstado().getEstado_id() == 4)
                                .collect(Collectors.toList());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);
        return "vPedidos"; 
    }
}
