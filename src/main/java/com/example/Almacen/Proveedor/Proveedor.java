package com.example.Almacen.Proveedor;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="Proveedor")
public class Proveedor {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String prov_codigo;
    private String prov_nombre;
    private String prov_marca;
    private String prov_celular;
    private String prov_telefono;
    private String prov_direccion;
    private String prov_ciudad;
    private String prov_estado;
    private Blob prov_img;
}
