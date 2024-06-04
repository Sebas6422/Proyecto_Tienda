package com.example.Almacen.Detalle;

import java.util.List;
import java.util.Optional;

public interface IDetalleService {
    
    public List<Detalle> Listar();
    public Optional<Detalle> ConsultarId(int id);
    public void Guardar(Detalle detalle);
    public void Eliminar(int id);
    public List<Detalle> BuscarAll(String desc);
}
