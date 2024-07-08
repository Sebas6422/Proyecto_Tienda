package com.example.Almacen.Detalle;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleService implements IDetalleService{
    @Autowired IDetalle data;

    @Override
    public List<Detalle> listar() {
        return (List<Detalle>) data.findAll();
    }

    @Override
    public Optional<Detalle> consultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void guardar(Detalle detalle) {
        data.save(detalle);
    }

    @Override
    public void eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Detalle> buscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
}
