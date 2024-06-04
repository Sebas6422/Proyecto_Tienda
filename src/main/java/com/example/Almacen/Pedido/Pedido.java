package com.example.Almacen.Pedido;

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
@Table(name="pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int pedido_id;
    private String pedido_codigo;
    private String pedido_tipo;
    private String pedido_fecha;
    private String pedido_estado;
    private double pedido_subtotal;

    @ManyToOne
    @JoinColumn(name = "Usuario_us_dni", referencedColumnName = "us_dni")
    private Usuario usu;
}
