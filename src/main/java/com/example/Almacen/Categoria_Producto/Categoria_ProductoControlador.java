package com.example.Almacen.Categoria_Producto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/cat_producto")
@Controller
public class Categoria_ProductoControlador {
    @Autowired
    private ICategoria_ProductoService service;
    
    @GetMapping("/cat_productos")
    public String Mostrar(Model model) {
        List<Categoria_Producto> cat_productos = service.Listar();
        model.addAttribute("cat_productos", cat_productos);
        return "listarCategoriaP";
    }
    
}
