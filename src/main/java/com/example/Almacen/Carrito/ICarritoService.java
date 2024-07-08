package com.example.Almacen.carrito;

import java.util.List;
import java.util.Optional;

public interface ICarritoService {
    public List<Carrito> listar();
    public Optional<Carrito> consultarId(int id);
    public void guardar(Carrito carrito);
    public void eliminar(int id);
    public List<Carrito> buscarAll(String desc);
}
