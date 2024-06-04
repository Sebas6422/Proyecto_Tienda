package com.example.Almacen.Proveedor;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProveedor extends CrudRepository<Proveedor,Integer>{

    //Aqui pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM proveedor "
            + "WHERE prov_codigo LIKE %?1% "
            + "OR prov_nombre LIKE %?1%"
            + "OR prov_estado LIKE %?1%"
            + "OR prov_ciudad LIKE %?1%",nativeQuery=true)

            List<Proveedor> buscarPorTodo(String desc);
}
