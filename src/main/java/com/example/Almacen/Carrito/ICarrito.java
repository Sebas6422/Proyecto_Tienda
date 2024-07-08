package com.example.Almacen.carrito;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICarrito extends CrudRepository<Carrito,Integer>{

    //Aqui pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM carrito "
            + "WHERE carr_id LIKE %?1% ",nativeQuery=true)

            List<Carrito> buscarPorTodo(String desc);
}