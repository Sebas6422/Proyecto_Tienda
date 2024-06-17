package com.example.Almacen.Carrito;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService implements ICarritoService{

    @Autowired ICarrito data;

    @Override
    public List<Carrito> Listar() {
        return (List<Carrito>) data.findAll();
    }

    @Override
    public Optional<Carrito> ConsultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Carrito carrito) {
        data.save(carrito);
    }

    @Override
    public void Eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Carrito> BuscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
    
}
