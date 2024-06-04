package com.example.Almacen.Categoria_Producto;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Categoria_ProductoService implements ICategoria_ProductoService{

    @Autowired
    private ICategoria_Producto data;

    @Override
    public List<Categoria_Producto> Listar() {
        return (List<Categoria_Producto>) data.findAll();
    }

    @Override
    public Optional<Categoria_Producto> ConsultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Categoria_Producto catProducto) {
        data.save(catProducto);
    }

    @Override
    public void Eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Categoria_Producto> BuscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
    
}
