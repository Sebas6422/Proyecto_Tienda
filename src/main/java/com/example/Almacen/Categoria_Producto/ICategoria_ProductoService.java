package com.example.Almacen.Categoria_Producto;

import java.util.List;
import java.util.Optional;

public interface ICategoria_ProductoService {
    
    public List<Categoria_Producto> Listar();
    public Optional<Categoria_Producto> ConsultarId(int id);
    public void Guardar(Categoria_Producto catProducto);
    public void Eliminar(int id);
    public List<Categoria_Producto> BuscarAll(String desc);
}
