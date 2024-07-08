package com.example.Almacen.carrito;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService implements ICarritoService{

    @Autowired ICarrito data;

    @Override
    public List<Carrito> listar() {
        return (List<Carrito>) data.findAll();
    }   

    @Override
    public Optional<Carrito> consultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void guardar(Carrito carrito) {
        data.save(carrito);
    }

    @Override
    public void eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Carrito> buscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
    
}
