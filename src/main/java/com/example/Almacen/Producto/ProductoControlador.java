package com.example.Almacen.Producto;

import java.sql.Blob;
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
import org.springframework.web.bind.annotation.PostMapping;

@RequestMapping("/producto/")
@Controller
public class ProductoControlador {
    @Autowired
    private IProductoService service;

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
        }else{
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
                                    Model model ) {
        Producto pro = new Producto();

        if(!img.isEmpty()){
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

        service.Guardar(pro);
        return "redirect:/producto/productos/";
    }
    
}
