package com.example.Almacen.Producto;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProducto extends CrudRepository<Producto,Integer>{

    //Aqui pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM producto "
            + "WHERE produc_id LIKE %?1% ",nativeQuery=true)

            List<Producto> buscarPorTodo(String desc);
}
