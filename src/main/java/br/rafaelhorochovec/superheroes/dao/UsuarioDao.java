package br.rafaelhorochovec.superheroes.dao;

import br.rafaelhorochovec.superheroes.model.Usuario;

public interface UsuarioDao {

	Usuario login(String usuario, String senha);

}
