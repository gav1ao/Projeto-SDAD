package br.com.sdad.entidades.informacoes;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
public class Temperatura extends InformacaoVital {

    private Double temperatura;

    public Temperatura () {}

    public Temperatura (int idRegistro,
                        int idUsuario,
                        Timestamp dataHora,
                        Double temperatura){
        super(idRegistro, idUsuario, dataHora);
        this.temperatura = temperatura;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }
}
