package com.example.Almacen.Rol;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="Rol")
public class Rol {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int rol_id;
    private String rol_nombre;
}
