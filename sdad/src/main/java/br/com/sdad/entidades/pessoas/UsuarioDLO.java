package br.com.sdad.entidades.pessoas;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Access(AccessType.FIELD)
@Table(name = "usuarios")
public class UsuarioDLO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String login;

    @NotBlank
    private String senha;

    private String nome;

    private String sobrenome;

    private String email;
    
    private String idDispositivo;

    private boolean ehAdmin;

    public UsuarioDLO() {}

    public UsuarioDLO(String login,
                      String senha,
                      String nome,
                      String sobrenome,
                      String email,
                      String idDispositivo,
                      boolean ehAdmin) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.idDispositivo = idDispositivo;
        this.ehAdmin = ehAdmin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public boolean isAdmin() {
        return ehAdmin;
    }

    public void setAdmin(boolean ehAdmin) {
        this.ehAdmin = ehAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioDLO usuarioDLO = (UsuarioDLO) o;
        return id == usuarioDLO.id &&
                ehAdmin == usuarioDLO.ehAdmin &&
                Objects.equals(login, usuarioDLO.login) &&
                Objects.equals(senha, usuarioDLO.senha) &&
                Objects.equals(nome, usuarioDLO.nome) &&
                Objects.equals(sobrenome, usuarioDLO.sobrenome) &&
                Objects.equals(email, usuarioDLO.email) &&
                Objects.equals(idDispositivo, usuarioDLO.idDispositivo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, senha, nome, sobrenome, email, idDispositivo, ehAdmin);
    }

    @Override
    public String toString() {
        return "UsuarioDLO{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                ", nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", email='" + email + '\'' +
                ", idDispositivo='" + idDispositivo + '\'' +
                ", ehAdmin=" + ehAdmin +
                '}';
    }
}
