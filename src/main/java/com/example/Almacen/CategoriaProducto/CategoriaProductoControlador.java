package com.example.Almacen.CategoriaProducto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/cat_producto")
@Controller
public class CategoriaProductoControlador {
    @Autowired
    private ICategoriaProductoService service;
    
    @GetMapping("/cat_productos")
    public String mostrar(Model model) {
        List<CategoriaProducto> catProductos = service.listar();
        model.addAttribute("cat_productos", catProductos);
        return "listarCategoriaP";
    }
    
}
