package com.example.Almacen.Login;



import java.util.Date;

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
@Table(name = "detalle_login")
public class DetalleLogin {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int us_id;
    private Date fechaInicio;
    private String ip;

    @ManyToOne
    @JoinColumn(name="Usuario_us_id", referencedColumnName = "us_id")
    Usuario usuario;
}
