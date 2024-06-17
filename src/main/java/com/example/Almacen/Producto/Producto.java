package com.example.Almacen.Producto;

import java.sql.Blob;

import com.example.Almacen.Categoria_Producto.Categoria_Producto;
import com.example.Almacen.Proveedor.Proveedor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="Producto")
public class Producto {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int produc_id;
    private String produc_nombre;
    private String produc_tamanho;
    private String produc_caracteristica;
    private double produc_precio;
    private int produc_stock;
    private Blob produc_img;

    @ManyToOne
    @JoinColumn(name = "Proveedor_id", referencedColumnName = "id")
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "Categoria_Producto", referencedColumnName = "catProduct_id")
    private Categoria_Producto categoriaProducto;
}
