package br.com.sdad.entidades;

import br.com.sdad.Alertas;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Objects;

public class AlertaDLO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAlerta;

    @Column(columnDefinition="TimestampTimeFormat")
    private Timestamp dataHora;

    private int idRegistro;

    private Alertas.TipoAlerta tipoAlerta;

    public AlertaDLO(Timestamp dataHora,
                     int idRegistro,
                     Alertas.TipoAlerta tipoAlerta){
        this.dataHora = dataHora;
        this.idRegistro = idRegistro;
        this.tipoAlerta = tipoAlerta;
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }

    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Alertas.TipoAlerta getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(Alertas.TipoAlerta tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertaDLO alertaDLO = (AlertaDLO) o;
        return idAlerta == alertaDLO.idAlerta &&
                idRegistro == alertaDLO.idRegistro &&
                Objects.equals(dataHora, alertaDLO.dataHora) &&
                tipoAlerta == alertaDLO.tipoAlerta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAlerta, dataHora, idRegistro, tipoAlerta);
    }
}
