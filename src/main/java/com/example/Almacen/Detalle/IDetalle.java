package com.example.Almacen.Detalle;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetalle extends CrudRepository<Detalle,Integer>{
    //Aqui pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM detalle "
            + "WHERE det_id LIKE %?1% "
            + "OR det_codigo LIKE %?1%",nativeQuery=true)

            List<Detalle> buscarPorTodo(String desc);
}
