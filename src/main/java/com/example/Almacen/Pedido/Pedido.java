package com.example.Almacen.Pedido;

import com.example.Almacen.Estado.Estado;
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
    private String pedido_tipo;
    private String pedido_fecha;
    private double pedido_total;
    private String pedido_metodo_pago;

    @ManyToOne
    @JoinColumn(name = "Usuario_us_id", referencedColumnName = "us_id")
    private Usuario usu;

    @ManyToOne
    @JoinColumn(name = "Estado_estado_id", referencedColumnName = "estado_id")
    private Estado estado;
}
