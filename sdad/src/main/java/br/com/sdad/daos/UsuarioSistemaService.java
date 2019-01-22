package br.com.sdad.daos;

import br.com.sdad.entidades.pessoas.UsuarioSistema;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UsuarioSistemaService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String query = "SELECT u FROM UsuarioSistema u WHERE u.login = :login";

        List<UsuarioSistema> users = em.createQuery(query, UsuarioSistema.class)
                                       .setParameter("login", username)
                                       .getResultList();

        if(users.isEmpty()){
            throw new UsernameNotFoundException
                    ("O usuario " + username +" não existe em nossa base de dados");
        }

        return users.get(0);
    }
}
