package com.example.Almacen.Proveedor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProveedorService implements IProveedorService{

    @Autowired
    private IProveedor data;

    @Override
    public List<Proveedor> Listar() {
        return (List<Proveedor>) data.findAll();
    }

    @Override
    public Optional<Proveedor> ConsultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Proveedor proveedor) {
        data.save(proveedor);
    }

    @Override
    public void Eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Proveedor> BuscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
    
}
