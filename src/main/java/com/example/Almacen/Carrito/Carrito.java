package com.example.Almacen.Carrito;

import com.example.Almacen.Estado.Estado;
import com.example.Almacen.Producto.Producto;
import com.example.Almacen.Usuario.Usuario;

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
@Table(name="Carrito")
public class Carrito {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int carr_id;
    private double carr_subtotal;

    @ManyToOne
    @JoinColumn(name = "Usuario_us_dni", referencedColumnName = "us_dni")
    private Usuario usu;

    @ManyToOne
    @JoinColumn(name = "Estado_estado_id", referencedColumnName = "estado_id")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "Producto_produc_id", referencedColumnName = "produc_id")
    private Producto producto;
}
