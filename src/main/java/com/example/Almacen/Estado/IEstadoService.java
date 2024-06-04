package com.example.Almacen.Estado;

import java.util.List;
import java.util.Optional;

public interface IEstadoService {
    
    public List<Estado> Listar();
    public Optional<Estado> ConsultarId(int id);
    public void Guardar(Estado estado);
    public void Eliminar(int id);
    public List<Estado> BuscarAll(String desc);
}
