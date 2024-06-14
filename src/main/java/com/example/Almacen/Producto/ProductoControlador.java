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

import com.example.Almacen.Categoria_Producto.Categoria_Producto;
import com.example.Almacen.Categoria_Producto.ICategoria_Producto;
import com.example.Almacen.Categoria_Producto.ICategoria_ProductoService;
import com.example.Almacen.Proveedor.IProveedor;
import com.example.Almacen.Proveedor.IProveedorService;
import com.example.Almacen.Proveedor.Proveedor;

import org.springframework.web.bind.annotation.PostMapping;

@RequestMapping("/producto/")
@Controller
public class ProductoControlador {

    
    @Autowired
    private IProveedorService serviceProv;
    @Autowired
    private ICategoria_ProductoService serviceCat;

    @Autowired
    private IProductoService service;

    @Autowired
    private ICategoria_Producto cate;

    @Autowired
    private IProveedor prov;

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
                                    @RequestParam("produc_stock") String stock,
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
    public String actualizarProducto(@RequestParam("id") int id,
                                     @RequestParam("produc_nombre") String nombre,
                                     @RequestParam("produc_tamanho") String tamanho,
                                     @RequestParam("produc_caracteristica") String caracteristica,
                                     @RequestParam("produc_precio") Double precio,
                                     @RequestParam("produc_stock") String stock,
                                     @RequestParam("produc_img") MultipartFile img,
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
}
