package com.example.Almacen.Proveedor;

import java.util.List;
import java.util.Optional;

public interface IProveedorService {

    public List<Proveedor> Listar();
    public Optional<Proveedor> ConsultarId(int id);
    public void Guardar(Proveedor proveedor);
    public void Eliminar(int id);
    public List<Proveedor> BuscarAll(String desc);
}