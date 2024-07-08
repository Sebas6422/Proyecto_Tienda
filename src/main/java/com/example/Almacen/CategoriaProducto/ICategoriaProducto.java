package com.example.Almacen.CategoriaProducto;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoriaProducto extends CrudRepository<CategoriaProducto, Integer> {

    // Aquí pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM categoria_producto WHERE "
    + "catProduct_id LIKE %?1%", nativeQuery = true)
    List<CategoriaProducto> buscarPorTodo(String desc);
}
