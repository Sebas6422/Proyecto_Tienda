package com.example.Almacen.Categoria_Producto;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoria_Producto extends CrudRepository<Categoria_Producto, Integer> {

    // Aquí pueden ir consultas a BD adicionales
    @Query(value = "SELECT * FROM categoria_producto WHERE "
    + "catProduct_id LIKE %?1%", nativeQuery = true)
    List<Categoria_Producto> buscarPorTodo(String desc);
}
