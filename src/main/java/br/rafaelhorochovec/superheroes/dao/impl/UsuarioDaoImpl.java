package br.rafaelhorochovec.superheroes.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import br.rafaelhorochovec.superheroes.dao.UsuarioDao;
import br.rafaelhorochovec.superheroes.jpa.JPAUtil;
import br.rafaelhorochovec.superheroes.model.Usuario;

public class UsuarioDaoImpl implements UsuarioDao {

	@Override
	public Usuario login(String login, String senha) {

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		try {
			Usuario usuario = (Usuario) entityManager
					.createQuery("SELECT u from Usuario u where u.usuario =:usuario and u.senha = :senha")
					.setParameter("usuario", login).setParameter("senha", senha).getSingleResult();
			
			return usuario;
		} catch (NoResultException e) {
			return null;
		}
	}
}
