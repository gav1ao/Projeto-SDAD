package br.com.sdad.entidades.pessoas;

import br.com.sdad.entidades.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class UsuarioSistema implements UserDetails {

    @Id
    private String login;
    private String password;
    private String nome;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Role.class)
    @JoinTable(name="user_role", joinColumns=
            {@JoinColumn(name="user_login")}, inverseJoinColumns=
            {@JoinColumn(name="role_name")})
    private List<Role> roles = new ArrayList<>();

    public UsuarioSistema(){}

    public UsuarioSistema(String login,
                          String password,
                          String nome,
                          List<Role> roles) {
        this.login = login;
        this.password = password;
        this.nome = nome;
        this.roles = roles;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
