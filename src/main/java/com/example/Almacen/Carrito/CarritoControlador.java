package com.example.Almacen.Carrito;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/carrito/")
@Controller
public class CarritoControlador {
    

    @PostMapping("/ComprarProducto")
    public String comprarProductos(@RequestParam("id_usu") int id,
                                   Model model, HttpSession session){
        return "redirect:/Cliente";
    }


}
