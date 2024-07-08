package com.example.Almacen.Detalle;

import java.util.List;
import java.util.Optional;

public interface IDetalleService {
    
    public List<Detalle> listar();
    public Optional<Detalle> consultarId(int id);
    public void guardar(Detalle detalle);
    public void eliminar(int id);
    public List<Detalle> buscarAll(String desc);
}
