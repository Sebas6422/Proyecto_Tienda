package com.example.Almacen.Proveedor;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.sql.rowset.serial.SerialBlob;

import java.util.regex.Matcher;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Almacen.Usuario.Usuario;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;



@RequestMapping("/proveedor/")
@Controller
public class ProveedorControlador {
    @Autowired
    private IProveedorService service;
    //private static Logger logger = (Logger) LoggerFactory.getLogger(ProveedorControlador.class);
    Usuario usuario = new Usuario();

    @Autowired
    private IProveedor iProveedor;

    @GetMapping("/proveedores")
    public String Mostrar(Model model, HttpSession session) {
        usuario = (Usuario) session.getAttribute("Usuario");
        List<Proveedor> proveedores = service.Listar();
        model.addAttribute("proveedores", proveedores); 
        model.addAttribute("usuario", usuario);
        return "aProveedores";
    }

    @GetMapping("/getImage/{id}")
    public ResponseEntity<byte[]> mostrarImagenProveedor(@PathVariable int id) {
        Optional<Proveedor> proveedorOptional = service.ConsultarId(id);
        if (proveedorOptional.isPresent()) {
            Proveedor proveedor = proveedorOptional.get();
            Blob blob = proveedor.getProv_img();
            try {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
            } catch (SQLException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/registrarProveedor")
    public String mostrarProveedores(HttpSession session, Model model){
        if (session.getAttribute("error") != null) {
            model.addAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        if (session.getAttribute("success") != null) {
            model.addAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        usuario = (Usuario) session.getAttribute("Usuario");
        List<Proveedor> proveedores = service.Listar();
        model.addAttribute("proveedores", proveedores); 
        model.addAttribute("usuario", usuario);
        return "aProveedores";
    }

    @PostMapping("/registrarProveedor")
    public String registrarProveedor(@RequestParam("prov_codigo") String codigo,
                                     @RequestParam("prov_nombre") String nombre,
                                     @RequestParam("prov_marca") String marca,
                                     @RequestParam("prov_celular") String celular,
                                     @RequestParam("prov_telefono") String telefono,
                                     @RequestParam("prov_direccion") String direccion,
                                     @RequestParam("prov_ciudad") String ciudad,
                                     @RequestParam("prov_estado") String estado,
                                     @RequestParam("prov_img") MultipartFile img,
                                     Model model, HttpSession session) {
        Proveedor prov = new Proveedor();
    

        //Validación
        // boolean vacios = vacio(codigo, nombre, marca, celular, telefono, direccion, ciudad, estado, img.toString());
         
        
        // if(vacios){
        //     return Mostrar(model, session);
        // }
        
        // boolean codg = validarCodigo(codigo);
        // boolean nombg = validarNombre(nombre);
        // boolean marcag = validarMarca(marca);
        // boolean telCg = validarTelefonoC(celular);
        // boolean telg = validarTelefono(telefono);
        // boolean dircg = validarDireccion(direccion);
        // boolean ciudadg = validarCiudad(ciudad);
        // boolean estag = validarEstado(estado);
        
        // if (!codg || !nombg || !marcag || !telCg
        // || !telg || !dircg || !ciudadg || !estag) {
        //     return Mostrar(model, session); 
        // }
        
        if(!img.isEmpty()){
            try{
                byte[] imgBytes = img.getBytes();
                Blob imgBlob = new SerialBlob(imgBytes);
                prov.setProv_img(imgBlob);
            }catch(IOException | SQLException e){
                e.printStackTrace();
                session.setAttribute("error", "Error al cargar la imagen");
                return "redirect:/proveedor/registraProveedor";
            }
        }

        prov.setProv_codigo(codigo);
        prov.setProv_nombre(nombre);
        prov.setProv_marca(marca);
        prov.setProv_celular(celular);
        prov.setProv_telefono(telefono);
        prov.setProv_direccion(direccion);
        prov.setProv_ciudad(ciudad);
        prov.setProv_estado(estado);
       

        service.Guardar(prov);
        session.setAttribute("success", "El proveedor se registró con éxito.");

        return "redirect:/proveedor/registraProveedor";
    }


    @GetMapping("/eliminarProveedor")
    public String eliminarProveedor(@RequestParam("id") int id,
                                    Model model, HttpSession session) {                            
        service.Eliminar(id);

        return Mostrar(model, session);
    }
    

    

    @GetMapping("/editarProveedor")
    public String editarProveedor(@RequestParam("id") int id,
                                Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("Usuario");
        Optional<Proveedor> proveedorOptional = service.ConsultarId(id);
        if (proveedorOptional.isPresent()) {
            Proveedor proveedor = proveedorOptional.get();
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("usuario", usuario);
            return "aProveedor_editar";
        } else {
            return "error";
        }
    }

       
    @PostMapping("/actualizarProveedor")
    public String actualizarProveedor(@RequestParam("id") int id,
                                    @RequestParam("prov_codigo") String codigo,
                                    @RequestParam("prov_nombre") String nombre,
                                    @RequestParam("prov_marca") String marca,
                                    @RequestParam("prov_celular") String celular,
                                    @RequestParam("prov_telefono") String telefono,
                                    @RequestParam("prov_direccion") String direccion,
                                    @RequestParam("prov_ciudad") String ciudad,
                                    @RequestParam("prov_estado") String estado,
                                    @RequestParam("prov_img") MultipartFile img,
                                    @RequestParam("imagenExistenteP") int imagenExistente,
                                    Model model, HttpSession session) {
        Proveedor proveedor = new Proveedor();

        if (!img.isEmpty()) {
            try {
                byte[] imgBytes = img.getBytes();
                Blob imgBlob = new SerialBlob(imgBytes);
                proveedor.setProv_img(imgBlob);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Usar la imagen existente
            Proveedor proveedorExistente = iProveedor.findById(id)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            proveedor.setProv_img(proveedorExistente.getProv_img());
        }

        proveedor.setId(id);
        proveedor.setProv_codigo(codigo);
        proveedor.setProv_nombre(nombre);
        proveedor.setProv_marca(marca);
        proveedor.setProv_celular(celular);
        proveedor.setProv_telefono(telefono);
        proveedor.setProv_direccion(direccion);
        proveedor.setProv_ciudad(ciudad);
        proveedor.setProv_estado(estado);

        service.Guardar(proveedor);

        session.setAttribute("success", "El proveedor se editó con éxito.");
        return Mostrar(model, session);
    }

    

    @PostMapping("/buscar")
    public String buscarProveedor(@RequestParam("prov_codigo") String codigo,
                                  Model model) {
        List<Proveedor> proveedor = service.BuscarAll(codigo);
        model.addAttribute("proveedor", proveedor);
        return "aIndex";
    }
    

    //Validaciones
     public static boolean vacio(String... strings) {
        for (String str : strings) {
            if (str == null || str.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean validarId(String id) {
        return id.matches("\\d+");
    }
    
    public static boolean validarCodigo(String codigo){
        if(codigo.length() > 24)
            return false;

        Pattern p = Pattern.compile("[A-Za-z]{1,20}prov");
        Matcher m = p.matcher(codigo);
        return m.find();
    }
    
    public static boolean validarNombre(String nombre){
        if(nombre.length() > 40)
            return false;
        
        Pattern p = Pattern.compile("[0-9a-zA-Z .&-/]{"+(nombre.length()-1)+"}");
        Matcher m = p.matcher(nombre);
        return m.find();
    }
    
    public static boolean validarMarca(String marca){   
        if(marca.length() > 50)
            return false;
        
        Pattern p = Pattern.compile("[0-9a-zA-Z .&]{"+(marca.length()-1)+"}");
        Matcher m = p.matcher(marca);
        return m.find();
    }

    public static boolean validarTelefono(String numero) {
        if(numero.length() > 11)
            return false;

        Pattern p = Pattern.compile("01\\s[0-9]{8}");
        Matcher m = p.matcher(numero);
        return m.find();
    }
    
    public static boolean validarTelefonoC(String numero) {
        if(numero.length() > 11)
            return false;
        Pattern p = Pattern.compile("9[0-9]{2}-[0-9]{3}-[0-9]{3}");
        Matcher m = p.matcher(numero);
        return m.find();
    }

   
    public static boolean validarDireccion(String direccion){
        if(direccion.length() > 100)
            return false;
        String dir = Normalizer.normalize(direccion, Normalizer.Form.NFD)
                                  .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        
        Pattern p = Pattern.compile("[0-9a-zA-z ,.°#]{"+(direccion.length()-1)+"}");
        Matcher m = p.matcher(dir);
        return m.find();
    }
    
    public static boolean validarCiudad(String ciudad){
        if(ciudad.length() > 100)
            return false;
        
        Pattern p = Pattern.compile("[a-zA-z ]{"+(ciudad.length()-1)+"}");
        Matcher m = p.matcher(ciudad);
        return m.find();
    }
    
    public static boolean validarEstado(String estado){
        if(estado.length() > 8)
            return false;
        
        Pattern p = Pattern.compile("[A-Z][a-z]{"+(estado.length()-1)+"}");
        Matcher m = p.matcher(estado);
        return m.find();
    }
    
    public static boolean validarImagen(String img){
        if(img.length() > 255)
            return false;
        System.out.print("asdasdasdImagen");
        Pattern p = Pattern.compile("[0-9a-zA-Z°|!#$%&/=)(¿?'}{_.:]{"+(img.length())+"}");
        Matcher m = p.matcher(img);
        return m.find();
    }
}
