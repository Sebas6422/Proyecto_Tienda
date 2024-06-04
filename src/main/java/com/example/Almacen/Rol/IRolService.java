package com.example.Almacen.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolService {

    public List<Rol> Listar();
    public Optional<Rol> ConsultarId(int id);
    public void Guardar(Rol rol);
    public void Eliminar(int id);
    public List<Rol> BuscarAll(String desc);
}
