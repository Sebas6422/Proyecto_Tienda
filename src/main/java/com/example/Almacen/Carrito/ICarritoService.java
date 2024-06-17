package com.example.Almacen.Carrito;

import java.util.List;
import java.util.Optional;

public interface ICarritoService {
    public List<Carrito> Listar();
    public Optional<Carrito> ConsultarId(int id);
    public void Guardar(Carrito carrito);
    public void Eliminar(int id);
    public List<Carrito> BuscarAll(String desc);
}
