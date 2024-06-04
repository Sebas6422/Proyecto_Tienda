package com.example.Almacen.Estado;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="Estado")
public class Estado {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int estado_id;
    private String estado_info;
}
