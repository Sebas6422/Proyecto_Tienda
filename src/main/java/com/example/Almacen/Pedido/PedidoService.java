package com.example.Almacen.Pedido;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PedidoService implements IPedidoService{

    @Autowired IPedido data;

    @Override
    public List<Pedido> Listar() {
       return (List<Pedido>) data.findAll();
    }

    @Override
    public Optional<Pedido> ConsultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Pedido pedido) {
        data.save(pedido);
    }

    @Override
    public void Eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Pedido> BuscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
    
}
