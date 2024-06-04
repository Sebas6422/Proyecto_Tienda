package com.example.Almacen.Pedido;

import java.util.List;
import java.util.Optional;

public interface IPedidoService {

    public List<Pedido> Listar();
    public Optional<Pedido> ConsultarId(int id);
    public void Guardar(Pedido pedido);
    public void Eliminar(int id);
    public List<Pedido> BuscarAll(String desc);
}
