package com.example.Almacen.Estado;

import java.util.List;
import java.util.Optional;

public interface IEstadoService {
    
    public List<Estado> listar();
    public Optional<Estado> consultarId(int id);
    public void guardar(Estado estado);
    public void eliminar(int id);
    public List<Estado> buscarAll(String desc);
}
