package com.example.Almacen.Rol;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService implements IRolService{

    @Autowired
    private IRol data;

    @Override
    public List<Rol> Listar() {
        return (List<Rol>) data.findAll();
    }

    @Override
    public Optional<Rol> ConsultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Rol rol) {
        data.save(rol);
    }

    @Override
    public void Eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Rol> BuscarAll(String desc) {
        return data.buscarPorTodo(desc);
    }
    
}
