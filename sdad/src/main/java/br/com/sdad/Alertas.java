package br.com.sdad;

public class Alertas {

    public enum TipoAlerta {
        EMAIL("email"),
        SMS("sms"),
        TELEGRAM("telegram");

        private String tipoAlerta;

        TipoAlerta(String tipoAlerta){
            this.tipoAlerta = tipoAlerta;
        }

        public String getTipoAlerta() {
            return tipoAlerta;
        }
    }
}
