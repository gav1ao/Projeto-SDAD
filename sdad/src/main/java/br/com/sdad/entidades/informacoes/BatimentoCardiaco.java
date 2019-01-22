package br.com.sdad.entidades.informacoes;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
public class BatimentoCardiaco extends InformacaoVital {

    private int batimentos;

    public BatimentoCardiaco () {}

    public BatimentoCardiaco (int idRegistro,
                              int idUsuario,
                              Timestamp dataHora,
                              int batimentos) {
        super(idRegistro, idUsuario, dataHora);
        this.batimentos = batimentos;
    }

    public int getBatimentos() {
        return batimentos;
    }

    public void setBatimentos(int batimentos) {
        this.batimentos = batimentos;
    }
}
