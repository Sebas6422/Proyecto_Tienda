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
        return "uIndex";
    }

    @GetMapping("Nosotros")
    public String nosotros() {
        return "uNosotros";  
    }

    
    @GetMapping("/Login")
    public String login(HttpSession session) {
        if(session.getAttribute("correoUsuario") != null && session.getAttribute("tokenSesion") != null){
            return "aDashboard"; 
        }
        else if(session.getAttribute("correoUsuarioC") != null && session.getAttribute("tokenSesionC") != null){
            return "uLoginUsuario";
        }else{
            return "Login";  
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
        return "uLoginUsuario";
    }
    
    @GetMapping("/VolverLogin")
    public String getMethodName(Model model) {
        model.addAttribute("error", "Sesión expirada");
        return"Login";
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
        return "aIndexDashboard";  
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
        return "aDashboard"; 
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
        return "aUsuarios";  
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
        return "aProductos";
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
        return "aProductos_registrar";
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
        return "aProveedores";  

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
        return "aProveedor_registrar";
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
        return "aVentas";
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
        return "aPedidos";
    }

    @GetMapping("/VendedorBienvenida")
    public String v_bienvenida()
    {
        return "vBienvenida"; 
    }
    @GetMapping("/VendedorProductos")
    public String v_productos()
    {
        return "vProductos"; 
    }
    @GetMapping("/VendedorPuntoVenta")
    public String v_puntoventa()
    {
        return "vPuntoVenta"; 
    }
    @GetMapping("/VendedorVentas")
    public String v_ventas()
    {
        return "vVentas"; 
    }
    @GetMapping("/VendedorPedidos")
    public String v_pedidos()
    {
        return "vPedidos"; 
    }
  













}
