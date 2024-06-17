package com.example.Almacen.Usuario;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface IUsuario extends CrudRepository<Usuario,Integer>{
    
    //Aqui pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM usuario "
            + "WHERE us_dni LIKE %?1% "
            + "OR us_nombre LIKE %?1%" 
            + "oOR us_correo LIKE %?1%"
            + "OR us_apellido LIKE %?1%",nativeQuery=true)

            List<Usuario> buscarPorTodo(String desc);
}
