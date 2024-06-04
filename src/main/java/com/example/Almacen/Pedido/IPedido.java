package com.example.Almacen.Pedido;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPedido extends CrudRepository<Pedido,Integer>{

    //Aqui pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM pedido "
            + "WHERE pedido_id LIKE %?1% "
            + "OR pedido_tipo LIKE %?1%",nativeQuery=true)

            List<Pedido> buscarPorTodo(String desc);
}
