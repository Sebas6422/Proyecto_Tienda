package com.example.Almacen.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoService {
    public List<Producto> Listar();
    public Optional<Producto> ConsultarId(int id);
    public void Guardar(Producto producto);
    public void Eliminar(int id);
    public List<Producto> BuscarAll(String desc);
}
