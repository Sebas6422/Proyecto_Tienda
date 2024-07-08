package com.example.Almacen.Producto;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Almacen.CategoriaProducto.CategoriaProducto;
import com.example.Almacen.CategoriaProducto.ICategoriaProducto;
import com.example.Almacen.CategoriaProducto.ICategoriaProductoService;
import com.example.Almacen.Proveedor.IProveedor;
import com.example.Almacen.Proveedor.IProveedorService;
import com.example.Almacen.Proveedor.Proveedor;
import com.example.Almacen.Usuario.Usuario;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

@RequestMapping("/producto/")
@Controller
public class ProductoControlador {

    
    @Autowired
    private IProveedorService serviceProv;
    @Autowired
    private ICategoriaProductoService serviceCat;

    @Autowired
    private IProductoService service;
    
    @Autowired
    private IProducto iProducto;

    @Autowired
    private ICategoriaProducto cate;

    @Autowired
    private IProveedor prov;

    private Usuario usuario = new Usuario();
    @GetMapping("/productos/")
    public String Mostrar(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        List<Producto> productos = service.Listar();
        model.addAttribute("productos", productos);
        model.addAttribute("usuario", usuario);
        return "aProductos";
    }

    @GetMapping("/getImage/{id}")
    public ResponseEntity<byte[]> mostrarImagenProducto(@PathVariable int id) {
        Optional<Producto> productoOptional = service.ConsultarId(id);
        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            Blob blob = producto.getProduc_img();
            try {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("registrarProducto")
    public String mostrarProducto(HttpSession session, Model model){
        if (session.getAttribute("error") != null) {
            model.addAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        if (session.getAttribute("success") != null) {
            model.addAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        usuario = (Usuario) session.getAttribute("Usuario");
        List<Producto> productos = service.Listar();
        model.addAttribute("productos", productos);
        model.addAttribute("usuario", usuario);
        return "aProductos";
    }

    @PostMapping("/registrarProducto")
    public String registrarProducto(@RequestParam("produc_nombre") String nombre,
                                    @RequestParam("produc_tamanho") String tamanho,
                                    @RequestParam("produc_caracteristica") String caracteristica,
                                    @RequestParam("produc_precio") Double precio,
                                    @RequestParam("produc_stock") int stock,
                                    @RequestParam("produc_img") MultipartFile img,
                                    @RequestParam("categoria_producto") Integer cat_producto,
                                    @RequestParam("proveedor_id") Integer proveedor_id,
                                    Model model, HttpSession session) {
        Producto pro = new Producto();

        if (!img.isEmpty()) {
            try {
                byte[] imgBytes = img.getBytes();
                Blob imgBlob = new SerialBlob(imgBytes);
                pro.setProduc_img(imgBlob);
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("error", "Error al cargar la imagen");
                return "redirect:/proveedor/registraProducto";
            }
        }

        pro.setProduc_nombre(nombre);
        pro.setProduc_tamanho(tamanho);
        pro.setProduc_caracteristica(caracteristica);
        pro.setProduc_precio(precio);
        pro.setProduc_stock(stock);

        // Buscamos y asignamos la categoría del Producto
        CategoriaProducto categoriaProducto = cate.findById(cat_producto)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        pro.setCategoriaProducto(categoriaProducto);

        // Buscamos y asignamos el proveedor del producto
        Proveedor proveedor = prov.findById(proveedor_id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        pro.setProveedor(proveedor);

        service.Guardar(pro);
        session.setAttribute("success", "El producto se registró con éxito.");
        return "redirect:/producto/registrarProducto";
    }

    @GetMapping("/eliminarProducto")
    public String eliminarProducto(@RequestParam("id") int id, Model model, HttpSession session) {
        service.Eliminar(id);
        return Mostrar(model, session);
    }

    @GetMapping("/editarProducto")
    public String editarProducto(@RequestParam("id") int id,HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        Optional<Producto> productoOptional = service.ConsultarId(id);

        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            List<CategoriaProducto> cat_productos = serviceCat.listar();
            List<Proveedor> proveedores = serviceProv.Listar();
            model.addAttribute("producto", producto);
            model.addAttribute("cat_productos", cat_productos);
            model.addAttribute("proveedores", proveedores);
            model.addAttribute("usuario", usuario);
            return "aProducto_editar";
        } else {
            return "error";
        }
    }

    @PostMapping("/actualizarProducto")
    public String actualizarProducto(@RequestParam("produc_id") int id,
                                    @RequestParam("produc_nombre") String nombre,
                                    @RequestParam("produc_tamanho") String tamanho,
                                    @RequestParam("produc_caracteristica") String caracteristica,
                                    @RequestParam("produc_precio") Double precio,
                                    @RequestParam("produc_stock") int stock,
                                    @RequestParam("produc_img") MultipartFile img,
                                    @RequestParam("imagenExistente") String imagenExistente,
                                    @RequestParam("categoria_producto") Integer cat_producto,
                                    @RequestParam("proveedor_id") Integer proveedor_id,
                                    Model model, HttpSession session) {
        Producto producto = new Producto();

        if (!img.isEmpty()) {
            try {
                byte[] imgBytes = img.getBytes();
                Blob imgBlob = new SerialBlob(imgBytes);
                producto.setProduc_img(imgBlob);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Usar la imagen existente
            Producto productoExistente = iProducto.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            producto.setProduc_img(productoExistente.getProduc_img());
        }

        producto.setProduc_id(id);
        producto.setProduc_nombre(nombre);
        producto.setProduc_tamanho(tamanho);
        producto.setProduc_caracteristica(caracteristica);
        producto.setProduc_precio(precio);
        producto.setProduc_stock(stock);

        // Buscamos y asignamos la categoría del Producto
        CategoriaProducto categoriaProducto = cate.findById(cat_producto)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        producto.setCategoriaProducto(categoriaProducto);

        // Buscamos y asignamos el proveedor del producto
        Proveedor proveedor = prov.findById(proveedor_id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        producto.setProveedor(proveedor);

        service.Guardar(producto);
        return "redirect:/producto/productos/";
    }
    //Para el Cliente
    @GetMapping("/VerMasProducto")
    public String verMasProduct(@RequestParam("idP") int id,
                                Model model){

        Optional<Producto> productoOptional = service.ConsultarId(id);
        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            model.addAttribute("producto", producto);
            return "uDetalleProducto";
        }else{
            return "error";
        }
    }

    //Validacion de compra
    public static boolean prodcantidad(String cantidad) {
        if (cantidad == null || cantidad.isEmpty()) {
            return false;
        }
        return cantidad.matches("^[1-9]\\d*$");
    }

    public static boolean nombretarjeta(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        return nombre.matches("^[a-zA-Z\\s]+$");
    }

    public static boolean numerotarjeta(String numero) {
        if (numero == null || numero.isEmpty()) {
            return false;
        }
        return numero.matches("^[45]\\d{15}$");
    }

    public static boolean fechaexp(String fecha) {
        if (fecha == null || fecha.isEmpty()) {
            return false;
        }
        return fecha.matches("^(0[1-9]|1[0-2])/\\d{2}$");
    }

    public static boolean codseguridad(String cvv) {
        if (cvv == null || cvv.isEmpty()) {
            return false;
        }
        return cvv.matches("^\\d{3}$");
    }

    public static boolean metododepago(String metodoPago) {
        if (metodoPago == null || metodoPago.isEmpty()) {
            return false;
        }
        return metodoPago.matches("^(Contraentrega|Tarjeta)$");
    }

    public static boolean tipodemetodo(String metodoPago) {
        if (metodoPago == null || metodoPago.isEmpty()) {
            return false;
        }
        return metodoPago.matches("^(Yape|Plin|Visa|Mastercard|American_Express)$");
    }
}
