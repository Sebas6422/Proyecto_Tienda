package com.example.Almacen.Rol;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRol extends CrudRepository<Rol,Integer>{

    //Aqui pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM rol "
            + "WHERE rol_id LIKE %?1% ",nativeQuery=true)

            List<Rol> buscarPorTodo(String desc);
}
