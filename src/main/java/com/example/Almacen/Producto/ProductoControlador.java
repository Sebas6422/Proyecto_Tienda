package com.example.Almacen.Producto;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.example.Almacen.Carrito.Carrito;
import com.example.Almacen.Carrito.ICarritoService;
import com.example.Almacen.Categoria_Producto.Categoria_Producto;
import com.example.Almacen.Categoria_Producto.ICategoria_Producto;
import com.example.Almacen.Categoria_Producto.ICategoria_ProductoService;
import com.example.Almacen.Estado.Estado;
import com.example.Almacen.Estado.IEstado;
import com.example.Almacen.Proveedor.IProveedor;
import com.example.Almacen.Proveedor.IProveedorService;
import com.example.Almacen.Proveedor.Proveedor;
import com.example.Almacen.Usuario.IUsuario;
import com.example.Almacen.Usuario.Usuario;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

@RequestMapping("/producto/")
@Controller
public class ProductoControlador {

    
    @Autowired
    private IProveedorService serviceProv;
    @Autowired
    private ICategoria_ProductoService serviceCat;

    @Autowired
    private ICarritoService serviceC;

    @Autowired
    private IProductoService service;
    
    @Autowired
    private IProducto iProducto;

    @Autowired
    private ICategoria_Producto cate;

    @Autowired
    private IUsuario usu;

    @Autowired
    private IProveedor prov;

    @Autowired
    private IEstado est;

    @GetMapping("/productos/")
    public String Mostrar(Model model) {
        List<Producto> productos = service.Listar();
        model.addAttribute("productos", productos);
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

    @PostMapping("/registrarProducto")
    public String registrarProducto(@RequestParam("produc_nombre") String nombre,
                                    @RequestParam("produc_tamanho") String tamanho,
                                    @RequestParam("produc_caracteristica") String caracteristica,
                                    @RequestParam("produc_precio") Double precio,
                                    @RequestParam("produc_stock") int stock,
                                    @RequestParam("produc_img") MultipartFile img,
                                    @RequestParam("categoria_producto") Integer cat_producto,
                                    @RequestParam("proveedor_id") Integer proveedor_id,
                                    Model model) {
        Producto pro = new Producto();

        if (!img.isEmpty()) {
            try {
                byte[] imgBytes = img.getBytes();
                Blob imgBlob = new SerialBlob(imgBytes);
                pro.setProduc_img(imgBlob);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pro.setProduc_nombre(nombre);
        pro.setProduc_tamanho(tamanho);
        pro.setProduc_caracteristica(caracteristica);
        pro.setProduc_precio(precio);
        pro.setProduc_stock(stock);

        // Buscamos y asignamos la categoría del Producto
        Categoria_Producto categoriaProducto = cate.findById(cat_producto)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        pro.setCategoriaProducto(categoriaProducto);

        // Buscamos y asignamos el proveedor del producto
        Proveedor proveedor = prov.findById(proveedor_id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        pro.setProveedor(proveedor);

        service.Guardar(pro);
        return "redirect:/producto/productos/";
    }

    @GetMapping("/eliminarProducto")
    public String eliminarProducto(@RequestParam("id") int id, Model model) {
        service.Eliminar(id);
        return Mostrar(model);
    }

    @GetMapping("/editarProducto")
    public String editarProducto(@RequestParam("id") int id, Model model) {
        Optional<Producto> productoOptional = service.ConsultarId(id);

        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            List<Categoria_Producto> cat_productos = serviceCat.Listar();
            List<Proveedor> proveedores = serviceProv.Listar();
            model.addAttribute("producto", producto);
            model.addAttribute("cat_productos", cat_productos);
            model.addAttribute("proveedores", proveedores);
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
                                    Model model) {
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
        Categoria_Producto categoriaProducto = cate.findById(cat_producto)
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

    @PostMapping("/AñadirCarrito")
    public String agregarCarrito(@RequestParam("produc_id") int idP,
                                      @RequestParam("cantidad") int cant,
                                      HttpSession session,
                                      Model model){
        Optional<Producto> produOptional = service.ConsultarId(idP);
        Carrito carrito = new Carrito();
        Double subtotal = 0.0;

        if(produOptional.isPresent()){
            Producto producto = produOptional.get();
            subtotal = cant * producto.getProduc_precio();

            carrito.setCarr_subtotal(subtotal);
            Usuario usua = (Usuario) session.getAttribute("usuarioLo");
            int idU = usua.getUs_id();
            Usuario usuario = usu.findById(idU)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            carrito.setUsu(usuario);

            Producto produc = iProducto.findById(idP)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            carrito.setProducto(produc);

            Estado estado = est.findById(1)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            
            carrito.setEstado(estado);
            carrito.setCantidad(cant);

            serviceC.Guardar(carrito);

            //para mostrar el carrito del cliente actual
            List<Carrito> carritos = serviceC.Listar();
            List<Carrito> carritoC = new ArrayList<>();
            for (Carrito carr : carritos){
                if (carr.getUsu() == usuario) {
                    carritoC.add(carr);
                }
            }
            model.addAttribute("carritosC", carritoC);
            return "/uCarritoCliente";
        }else{
            return "error";
        }
    }

    @PostMapping("/ComprarProducto")
    public String comprarProductos(@RequestParam("id_usu") int id){
        return "redirect:/Cliente";
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
