package br.com.sdad.daos;

import br.com.sdad.entidades.pessoas.UsuarioSistema;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UsuarioSistemaDAO {

    @PersistenceContext
    private EntityManager em;

    public void cadastrar(UsuarioSistema usuarioSistema) {
        em.persist(usuarioSistema);
    }
}
