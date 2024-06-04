package com.example.Almacen.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {

    public List<Usuario> Listar();
    public Optional<Usuario> ConsultarId(int id);
    public void Guardar(Usuario usuario);
    public void Eliminar(int id);
    public List<Usuario> BuscarAll(String desc);
}
