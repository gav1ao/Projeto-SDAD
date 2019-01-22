package br.com.sdad.daos;

import br.com.sdad.entidades.pessoas.UsuarioDLO;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UsuarioDAO {

    @PersistenceContext
    private EntityManager manager;

    public void cadastrar(UsuarioDLO usuarioDLO){
        manager.persist(usuarioDLO);
    }

    public List<UsuarioDLO> list() {
        return manager.createQuery(
                "SELECT DISTINCT (u)" +
                        "FROM UsuarioDLO u " +
                        "ORDER BY u.nome",
                UsuarioDLO.class).getResultList();
    }

    public int getIdUsuarioFromLogin(String login){
        return manager
                .createQuery(
                        "SELECT DISTINCT u.id " +
                            "FROM UsuarioDLO u " +
                            "WHERE u.login = :login",
                            Integer.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public int getIdUsuarioFromIdDispositivo(String idDispositivo){
        return manager
                .createQuery(
                        "SELECT DISTINCT u.id " +
                                "FROM UsuarioDLO u " +
                                "WHERE u.idDispositivo = :idDispositivo",
                                Integer.class)
                .setParameter("idDispositivo", idDispositivo)
                .getSingleResult();
    }

    public String getNomeUsuarioFromId(int idUsuario){
        return manager
                .createQuery(
                        "SELECT DISTINCT u.nome " +
                                "FROM UsuarioDLO u " +
                                "WHERE u.id = :idUsuario",
                                String.class)
                .setParameter("idUsuario", idUsuario)
                .getSingleResult();
    }

    public String getEmailFromIdDispositivo(String idDispositivo){
        return manager
                .createQuery(
                        "SELECT DISTINCT u.email " +
                                "FROM UsuarioDLO u " +
                                "WHERE u.idDispositivo = :idDispositivo",
                        String.class)
                .setParameter("idDispositivo", idDispositivo)
                .getSingleResult();
    }
}
