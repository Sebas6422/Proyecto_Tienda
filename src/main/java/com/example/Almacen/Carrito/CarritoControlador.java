package com.example.Almacen.Carrito;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Almacen.Detalle.Detalle;
import com.example.Almacen.Detalle.IDetalleService;
import com.example.Almacen.Estado.Estado;
import com.example.Almacen.Estado.IEstado;
import com.example.Almacen.Pedido.IPedidoService;
import com.example.Almacen.Pedido.Pedido;
import com.example.Almacen.Producto.IProducto;
import com.example.Almacen.Producto.IProductoService;
import com.example.Almacen.Producto.Producto;
import com.example.Almacen.Usuario.IUsuario;
import com.example.Almacen.Usuario.Usuario;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/carrito/")
@Controller
public class CarritoControlador {
    
    @Autowired
    ICarritoService service;

    @Autowired
    IProductoService serviceP;

    @Autowired
    IPedidoService servicePe;

    @Autowired
    IDetalleService serviceD;


    @Autowired
    private IUsuario usu;

    @Autowired
    private IEstado iEstado;

    @Autowired
    private IProducto iProducto;


    Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    @PostMapping("/AñadirCarrito")
    public String agregarCarrito(@RequestParam("produc_id") int idP,
                                @RequestParam("cantidad") int cant,
                                HttpSession session, Model model) {
        Optional<Producto> produOptional = serviceP.ConsultarId(idP);
        Usuario usua = (Usuario) session.getAttribute("Usuario");
        Carrito carrito = new Carrito();

        Double subtotal = 0.0;

        if (produOptional.isPresent()) {
            Producto producto = produOptional.get();
            subtotal = cant * producto.getProduc_precio();


            carrito.setCarr_subtotal(subtotal);

            int idU = usua.getUs_id();
            Usuario usuario = usu.findById(idU)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            carrito.setUsu(usuario);

            Producto produc = iProducto.findById(idP)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            carrito.setProducto(produc);

            Estado estado = iEstado.findById(1)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

            carrito.setEstado(estado);


            carrito.setCantidad(cant);

            service.Guardar(carrito);

            // para mostrar el carrito del cliente actual
            List<Carrito> carritos = service.Listar();
            List<Carrito> carritoC = carritos.stream()
                                        .filter(carr -> carr.getUsu().getUs_id() == usua.getUs_id() && carr.getEstado().getEstado_id() == 1)
                                        .collect(Collectors.toList());

            model.addAttribute("carritosC", carritoC);
            return "uCarritoCliente";
        } else {
            return "error";
        }
    }

    @GetMapping("/eliminarProductoCarrito")
    public String cancelarProducto(@RequestParam("id_carrito") int id,
                                   Model model, HttpSession session){
        Optional<Carrito> carrN = service.ConsultarId(id);   
        Carrito carrActualizado = new Carrito();
        carrActualizado.setCarr_id(carrN.get().getCarr_id());
        carrActualizado.setCantidad(carrN.get().getCantidad());
        carrActualizado.setCarr_subtotal(carrN.get().getCarr_subtotal());

        Estado estado = iEstado.findById(2)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));    
        carrActualizado.setEstado(estado);
        
        carrActualizado.setProducto(carrN.get().getProducto());
        carrActualizado.setUsu(carrN.get().getUsu());
        service.Guardar(carrActualizado);
        return "redirect:/CarritoP";
    } 

    
    @PostMapping("/Comprar")
    public String comprarTC(@RequestParam("metodo_pago") String metodo,
                            @RequestParam("tipo_pago") String tipo,
                            Model model, HttpSession session){
        Usuario usua = (Usuario) session.getAttribute("Usuario");
        final int estadoId;
        LocalDate fechaActual = LocalDate.now();
        Pedido pedido = new Pedido();
        List<Carrito> carrito = service.Listar();
        List<Carrito> carritoFiltrado = carrito.stream()
                                        .filter(c -> c.getUsu().getUs_id() == usua.getUs_id() && c.getEstado().getEstado_id() == 1)
                                        .collect(Collectors.toList());
        
        pedido.setPedido_fecha(fechaActual.toString());
        
        pedido.setPedido_metodo_pago(metodo);

        pedido.setPedido_tipo(tipo);

        double total = carritoFiltrado.stream()
                                    .mapToDouble(Carrito::getCarr_subtotal)
                                    .sum();
        pedido.setPedido_total(total);

        pedido.setUsu(usua);      
        
        if(metodo.equals("Contraentrega")){
            Estado estado = iEstado.findById(4)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));   
            
            pedido.setEstado(estado);  
            estadoId = 4;
        }else if(metodo.equals("Tarjeta")){
            Estado estado = iEstado.findById(3)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));   
            
            pedido.setEstado(estado); 
            estadoId = 3;
        }else {
            throw new RuntimeException("Método de pago no válido"); // Manejar el caso donde el método de pago no es válido
        }

        //Guardar el pedido en la base de datos
        servicePe.Guardar(pedido);

        //Para guardar el detalle del pedido
        carritoFiltrado.forEach(carr -> {
            Detalle detalle = new Detalle();

            detalle.setCarrito(carr);

            detalle.setPedido(pedido);

            Estado est = iEstado.findById(2)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));  
            carr.setEstado(est);

            //Si el estado de la compra es directa se disminuye la cantidad de los productos
            if(estadoId == 3){
                Producto productoN = carr.getProducto();
                int actualizarStock = productoN.getProduc_stock() - carr.getCantidad();
                productoN.setProduc_stock(actualizarStock);
                serviceP.Guardar(productoN);
            }
            //Actualizar el carrito
            service.Guardar(carr);

            //Registrar el detalle
            serviceD.Guardar(detalle);
        });

        if(usua.getRol().getRol_id() == 1)
            return "redirect:/AdminPedidos";
        else if (usua.getRol().getRol_id() == 2) 
            return "redirect:/Cliente";
        else if (usua.getRol().getRol_id() == 3) {
            return "/";
        }

        return "/";
    }


    @GetMapping("/ConfirmarCompra")
    public String confirmarCompraAdmin(@RequestParam("id_pedido") int id, Model model, HttpSession session){
        Optional<Pedido> pedOptional = servicePe.ConsultarId(id);

        if(pedOptional.isPresent()){
            Pedido pedido = pedOptional.get();

            Estado estado = iEstado.findById(3)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            pedido.setEstado(estado);

            servicePe.Guardar(pedido);
        }
        return "redirect:/AdminPedidos";
    }
    
    @GetMapping("/CancelarContraentrega")
    public String cancelarContraentrega(@RequestParam("id_pedido") int id, Model model){

        Optional<Pedido> pedidoOptional = servicePe.ConsultarId(id);

        if(pedidoOptional.isPresent()){
            Pedido pedido = pedidoOptional.get();

            Estado estado = iEstado.findById(5)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            pedido.setEstado(estado);

            servicePe.Guardar(pedido);
        }

        return "redirect:/AdminPedidos";
    }

    @GetMapping("/DetallePedido")
    public String verDetallePedido(@RequestParam("id_pedido") int id, Model model){
        Optional<Pedido> pedidoOptional = servicePe.ConsultarId(id);

        if (pedidoOptional.isPresent()) {
            Pedido pedido = pedidoOptional.get();
            List<Detalle> detalles = serviceD.Listar().stream()
                                        .filter(d -> d.getPedido().getPedido_id() == pedido.getPedido_id())
                                        .collect(Collectors.toList());
            model.addAttribute("detalles", detalles);
            return "aDetalleVentas";
        }else{
            return "error";
        }
    }
}
