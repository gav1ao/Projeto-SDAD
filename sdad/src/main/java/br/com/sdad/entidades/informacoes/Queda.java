package br.com.sdad.entidades.informacoes;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
public class Queda extends InformacaoVital {

    private boolean queda;

    public Queda () {}

    public Queda (int idRegistro,
                  int idUsuario,
                  Timestamp dataHora,
                  boolean queda) {
        super(idRegistro, idUsuario, dataHora);
        this.queda = queda;
    }

    public boolean houveQueda() {
        return queda;
    }

    public void definirQueda(boolean queda) {
        this.queda = queda;
    }
}
