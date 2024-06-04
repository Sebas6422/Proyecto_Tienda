package com.example.Almacen.Estado;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEstado extends CrudRepository<Estado,Integer>{
    //Aqui pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM estado "
            + "WHERE estado_id LIKE %?1% ",nativeQuery=true)

            List<Estado> buscarPorTodo(String desc);
}
