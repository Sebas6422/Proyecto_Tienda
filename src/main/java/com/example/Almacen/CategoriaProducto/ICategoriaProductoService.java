package com.example.Almacen.CategoriaProducto;

import java.util.List;
import java.util.Optional;

public interface ICategoriaProductoService {
    
    public List<CategoriaProducto> listar();
    public Optional<CategoriaProducto> consultarId(int id);
    public void guardar(CategoriaProducto catProducto);
    public void eliminar(int id);
    public List<CategoriaProducto> buscarAll(String desc);
}
