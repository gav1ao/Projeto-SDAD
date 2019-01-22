package br.com.sdad.entidades.informacoes;

import br.com.sdad.entidades.pessoas.UsuarioDLO;

import javax.persistence.*;

@Entity
public class Parametros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int tempMin;

    private int tempMax;

    private int bpmMin;

    private int bpmMax;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = UsuarioDLO.class)
    private int idUsuario;


    public Parametros () {}

    public Parametros(int tempMin, int tempMax, int bpmMin, int bpmMax, int idUsuario) {
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.bpmMin = bpmMin;
        this.bpmMax = bpmMax;
        this.idUsuario = idUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }

    public int getBpmMin() {
        return bpmMin;
    }

    public void setBpmMin(int bpmMin) {
        this.bpmMin = bpmMin;
    }

    public int getBpmMax() {
        return bpmMax;
    }

    public void setBpmMax(int bpmMax) {
        this.bpmMax = bpmMax;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
