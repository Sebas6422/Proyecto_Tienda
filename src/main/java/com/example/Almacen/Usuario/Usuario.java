package com.example.Almacen.Usuario;

import com.example.Almacen.Rol.Rol;

import jakarta.persistence.Column;
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
@Table(name="Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int us_id;
    @Column(unique = true)
    private String us_dni;
    private String us_nombre;
    private String us_apellido;

    @Column(unique = true)
    private String us_correo;
    
    private String us_contrasenha;
    private String us_direccion;
    private String us_telefono;

    @ManyToOne
    @JoinColumn(name = "Rol_rol_id", referencedColumnName = "rol_id")
    private Rol rol;
}
