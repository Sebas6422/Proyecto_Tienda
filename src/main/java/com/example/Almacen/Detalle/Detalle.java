package com.example.Almacen.Detalle;

import com.example.Almacen.Carrito.Carrito;
import com.example.Almacen.Pedido.Pedido;

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
@Table(name="Detalle")
public class Detalle {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int det_id;

    @ManyToOne
    @JoinColumn(name = "Carrito_carr_id", referencedColumnName = "carr_id")
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "Pedido_pedido_id", referencedColumnName = "pedido_id")
    private Pedido pedido;
}
