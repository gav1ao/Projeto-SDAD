package br.com.sdad.daos;

import br.com.sdad.entidades.informacoes.BatimentoCardiaco;
import br.com.sdad.entidades.informacoes.Parametros;
import br.com.sdad.entidades.informacoes.Queda;
import br.com.sdad.entidades.informacoes.Temperatura;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class InfoVitalDAO {

    @PersistenceContext
    private EntityManager manager;

    public List<BatimentoCardiaco> listarBatimentos(int idUsuario){
        return manager
                .createQuery(
                        "SELECT DISTINCT (bc) " +
                            "FROM BatimentoCardiaco bc " +
                            "WHERE bc.idUsuario = :idUsuario " +
                            "ORDER BY bc.dataHora DESC",
                        BatimentoCardiaco.class
                )
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    public List<Queda> listarQuedas(int idUsuario){
        return manager
                .createQuery(
                        "SELECT DISTINCT (q) " +
                            "FROM Queda q " +
                            "WHERE q.idUsuario = :idUsuario " +
                            "ORDER BY q.dataHora DESC",
                        Queda.class
                )
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    public List<Temperatura> listarTemperaturas(int idUsuario){
        return manager
                .createQuery(
                        "SELECT DISTINCT (t) " +
                            "FROM Temperatura t " +
                            "WHERE t.idUsuario = :idUsuario " +
                            "ORDER BY t.dataHora DESC",
                        Temperatura.class
                )
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    public void inserirBatimentos(BatimentoCardiaco batimentoCardiaco){
        manager.persist(batimentoCardiaco);
    }

    public void salvarParametros(Parametros parametros) { manager.persist(parametros);}

    public Parametros getParametrosFromIdUsuario(int idUsuario){
        return manager
                .createQuery("SELECT DISTINCT *" +
                        "FROM Parametros p" +
                        "WHERE p.idUsuario = :idUsuario",
                        Parametros.class
                )
                .setParameter("idUsuario", idUsuario)
                .getSingleResult();
    }

    public void inserirQueda(Queda queda) { manager.persist(queda); }
}
