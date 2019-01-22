package br.com.sdad.entidades;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    private String name;

    public enum TipoUsuario {
        USUARIO("ROLE_USUARIO"),
        ADMINISTRADOR("ROLE_ADMIN");

        private String tipoUsuario;

        TipoUsuario(String tipoUsuario){
            this.tipoUsuario = tipoUsuario;
        }

        public String getTipoUsuario() {
            return tipoUsuario;
        }
    }

    public Role(){}

    public Role(@NotNull TipoUsuario tipoUsuario){
        this.name = tipoUsuario.getTipoUsuario();
    }

    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
