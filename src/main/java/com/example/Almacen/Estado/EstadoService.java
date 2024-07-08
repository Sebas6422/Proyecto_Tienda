package com.example.Almacen.Estado;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadoService implements IEstadoService{

    @Autowired
    private IEstado data;

    @Override
    public List<Estado> listar() {
        return (List<Estado>) data.findAll();
    }

    @Override
    public Optional<Estado> consultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void guardar(Estado estado) {
        data.save(estado);
    }

    @Override
    public void eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Estado> buscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
    
}
