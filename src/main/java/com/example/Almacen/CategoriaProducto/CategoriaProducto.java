package com.example.Almacen.CategoriaProducto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="Categoria_producto")
public class CategoriaProducto {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int catProduct_id;
    private String catProduct_tipo;
}
