package com.example.Almacen.CategoriaProducto;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaProductoService implements ICategoriaProductoService{

    @Autowired
    private ICategoriaProducto data;

    @Override
    public List<CategoriaProducto> listar() {
        return (List<CategoriaProducto>) data.findAll();
    }

    @Override
    public Optional<CategoriaProducto> consultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void guardar(CategoriaProducto catProducto) {
        data.save(catProducto);
    }

    @Override
    public void eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<CategoriaProducto> buscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
    
}
