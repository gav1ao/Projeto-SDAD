package br.com.sdad.entidades.informacoes;

import javax.persistence.*;
import java.sql.Timestamp;

@MappedSuperclass
public class InformacaoVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRegistro;

    private int idUsuario;

    @Column(columnDefinition="TimestampTimeFormat")
    private Timestamp dataHora;

    public InformacaoVital () {}

    public InformacaoVital (int idRegistro,
                            int idUsuario,
                            Timestamp dataHora){
        this.idRegistro = idRegistro;
        this.idUsuario = idUsuario;
        this.dataHora = dataHora;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }

    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }
}
