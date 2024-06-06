package com.example.Almacen;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.Almacen.Categoria_Producto.Categoria_Producto;
import com.example.Almacen.Categoria_Producto.ICategoria_ProductoService;
import com.example.Almacen.Producto.IProductoService;
import com.example.Almacen.Producto.Producto;
import com.example.Almacen.Proveedor.IProveedorService;
import com.example.Almacen.Proveedor.Proveedor;
import com.example.Almacen.Usuario.UsuarioControlador;

import jakarta.servlet.http.HttpSession;




@Controller
public class Controlador {

    @Autowired
    private IProveedorService service;
    @Autowired
    private IProductoService serviceP;
    @Autowired
    private ICategoria_ProductoService serviceC;

    @GetMapping("/")
    public String inicio()
    {
        return "/uIndex";
    }

    @GetMapping("Nosotros")
    public String nosotros() {
        return "Usuario/uNosotros";  
    }

    @GetMapping("/Login")
    public String login(HttpSession session) {
        if(session.getAttribute("correoUsuario") != null && session.getAttribute("tokenSesion") != null){
            return "Administracion/aDashboard"; 
        }
        else if(session.getAttribute("correoUsuarioC") != null && session.getAttribute("tokenSesionC") != null){
            return "Usuario/uLoginUsuario";
        }else{
            return "Usuario/Login";  
        }
    }

    @GetMapping("/Cliente")
    public String cliente_index(HttpSession session) {
        if(session.getAttribute("correoUsuarioC") == null || session.getAttribute("tokenSesionC")== null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionU(session.getAttribute("correoUsuarioC").toString(), session.getAttribute("tokenSesionC").toString());
        if (session.getAttribute("correoUsuarioC") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        return "Usuario/uLoginUsuario";
    }
    
    @GetMapping("/VolverLogin")
    public String getMethodName(Model model) {
        model.addAttribute("error", "Sesión expirada");
        return"Usuario/Login";
    }
    
    
    //CONTROLADORES PARA ADMINISTRACION
    @GetMapping("/AdminDashIn")
    public String admin_indexDash(HttpSession session) {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion") == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        return "Administracion/aIndexDashboard";  
    }

    @GetMapping("/a_Dashboard")
    public String dashboardA(HttpSession session)
    {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion") == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        return "Administracion/aDashboard"; 
    }

    @GetMapping("/AdminUsuarios")
    public String admin_usuarios(HttpSession session)
    {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion") == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        return "Administracion/aUsuarios";  
    }

    @GetMapping("/AdminProductos")
    public String admin_productos(Model model, HttpSession session)
    {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion") == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        List<Producto> productos = serviceP.Listar();
        model.addAttribute("productos", productos); 
        return "Administracion/aProductos";
    }

    @GetMapping("/AdminProductRegistro")
    public String admin_product_registro(Model model, HttpSession session) {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion").toString() == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        List<Categoria_Producto> cat_productos = serviceC.Listar();
        List<Proveedor> proveedores = service.Listar();
        model.addAttribute("cat_productos", cat_productos);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("producto", new Producto()); // Assuming you have a Producto class
        return "Administracion/aProductos_registrar";
    }
    

    @GetMapping("/AdminProveedores")
    public String admin_proveedores(Model model, HttpSession session)
    {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion") == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        List<Proveedor> proveedores = service.Listar();
        model.addAttribute("proveedores", proveedores); 
        return "Administracion/aProveedores";  

    }

    @GetMapping("/AdminProvRegistro")
    public String admin_prov_registro(HttpSession session) {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion") == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        return "Administracion/aProveedor_registrar";
    }

    @GetMapping("/AdminVentas")
    public String admin_ventas(HttpSession session)
    {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion") == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        return "Administracion/aVentas";
    }

    @GetMapping("/AdminPedidos")
    public String admin_pedidos(HttpSession session)
    {
        if(session.getAttribute("correoUsuario") == null || session.getAttribute("tokenSesion") == null){
            return "error";
        }
        boolean inicio = UsuarioControlador.verificarTokenSesionA(session.getAttribute("correoUsuario").toString(), session.getAttribute("tokenSesion").toString());
        if (session.getAttribute("correoUsuario") == null && !inicio ) {
            return "redirect:/"; // Redirige al login si no hay usuario en sesión
        }
        return "Administracion/aPedidos";
    }
}
