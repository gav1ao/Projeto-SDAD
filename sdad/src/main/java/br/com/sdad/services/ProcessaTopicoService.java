package br.com.sdad.services;

import br.com.sdad.EnviarEmail;
import br.com.sdad.daos.InfoVitalDAO;
import br.com.sdad.daos.UsuarioDAO;
import br.com.sdad.entidades.informacoes.BatimentoCardiaco;
import br.com.sdad.entidades.informacoes.Parametros;
import br.com.sdad.entidades.informacoes.Queda;
import br.com.sdad.mqtt.MqttPublishSubscribeUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;

// Classe responsável por processar os tópicos recebidos pelo MQTT
@Component
public class ProcessaTopicoService {

    public static final String TOPICO_ATUALIZAR_FREQUENCIA = "atualizarFrequencia";
    public static final String TOPICO_DETECTAR_QUEDA = "quedaAlerta";
    private static final String MSG_QUEDA_TITULO = "Queda detectada";
    private static final String MSG_QUEDA_DETECTADA = "Uma possível queda foi detectada às <hora>.";

    @Autowired
    private InfoVitalDAO infoVitalDAO0;

    @Autowired
    private UsuarioDAO usuarioDAO0;

    @Autowired
    private MqttPublishSubscribeUtils mqtt0;

    @Autowired
    private EnviarEmail enviarEmail0;

    private static InfoVitalDAO infoVitalDAO;

    private static UsuarioDAO usuarioDAO;

    private static MqttPublishSubscribeUtils mqtt;

    private static EnviarEmail enviarEmail;

    @PostConstruct
    private void initStatic() {
        infoVitalDAO = this.infoVitalDAO0;
        usuarioDAO = this.usuarioDAO0;
        mqtt = this.mqtt0;
        enviarEmail = this.enviarEmail0;
    }

    public static void processaTopico(String topico, String mensagem) {
        switch (topico) {
            case TOPICO_ATUALIZAR_FREQUENCIA:
                String jsonBatimentos = mensagem;

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonRoot = objectMapper.readTree(jsonBatimentos);
                    JsonNode jsonNodeIdSdad = jsonRoot.get("idDispositivo");
                    JsonNode jsonNodeBatimentos = jsonRoot.get("batimentos");

                    String idDispositivo = jsonNodeIdSdad.asText();
                    String stBatimentos = jsonNodeBatimentos.asText();

                    if(StringUtils.isNumeric(stBatimentos) && StringUtils.isNotBlank(idDispositivo)){

                        // Buscar id do usuário na base
                        int idUsuario = usuarioDAO.getIdUsuarioFromIdDispositivo(idDispositivo);

                        int batimentos = Integer.parseInt(stBatimentos);

                        BatimentoCardiaco batimentoCardiaco = new BatimentoCardiaco();

                        batimentoCardiaco.setIdUsuario(7);
                        batimentoCardiaco.setDataHora(new Timestamp(System.currentTimeMillis()));
                        batimentoCardiaco.setBatimentos(batimentos);

                        infoVitalDAO.inserirBatimentos(batimentoCardiaco);
                    }

                } catch (IOException e){
                    // TODO Corrigir exception
                    // Do nothing
                    System.out.println("Erro parser json batimentos");

                } catch (EntityNotFoundException enf){
                    String msg = "O usuário do dispositivo de ID [] não foi encontrado";
                    System.out.println(msg);
                }

                break;
            case TOPICO_DETECTAR_QUEDA:
                String jsonQueda = mensagem;
                Timestamp hora = new Timestamp(System.currentTimeMillis());

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonRoot = objectMapper.readTree(jsonQueda);
                    JsonNode jsonNodeIdSdad = jsonRoot.get("idDispositivo");
                    JsonNode jsonNodeQueda = jsonRoot.get("queda");

                    String idDispositivo = jsonNodeIdSdad.asText();
                    boolean quedaDetectada = jsonNodeQueda.asBoolean();

                    if(quedaDetectada){
                        String email = usuarioDAO.getEmailFromIdDispositivo(idDispositivo);
                        String msgQueda = MSG_QUEDA_DETECTADA.replace("<hora>", hora.toString());

                        // Enviar email
                        enviarEmail.setEmailDestinatario(email);
                        enviarEmail.setAssunto(MSG_QUEDA_TITULO);
                        enviarEmail.setMsg(msgQueda);
                        enviarEmail.enviarGmail();

                        int idUsuario = usuarioDAO.getIdUsuarioFromIdDispositivo(idDispositivo);

                        Queda queda = new Queda();
                        queda.setDataHora(hora);
                        queda.setIdUsuario(idUsuario);
                        queda.definirQueda(true);

                        infoVitalDAO.inserirQueda(queda);
                    }

                } catch (Exception e) {
                    System.out.println(e.toString());
                }

                break;
            default:
                break;
        }
    }

    /*public static void atualizarParametros(int tempMin,
                                           int tempMax,
                                           int bpmMin,
                                           int bpmMax){

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // lógica criar json

            String json = "";

            if (!mqtt.status()) {
                mqtt.conectar();
            }

            mqtt.enviarInfo("atualizarParametros", json);

        } /*catch (IOException ioe){

            System.out.println(ioe.toString());

        }

        catch (MqttException e) {
            e.printStackTrace();
        }
    }*/

    public static void atualizarParametros(String idDispositivo, Parametros parametros) {
        String json = "{\"idDispositivo\" : \"" + idDispositivo + "\"," +
                "\"temperatura\" : { \"limite_temp_alta\" : \"" + parametros.getTempMax()+ "\", \"limite_temp_baixa\" : \""+ parametros.getTempMin() + "\" }" +
                "\"batimentos\" : { \"limite_bpm_max\" : \"" + parametros.getBpmMax() + "\", \"limite_bpm_min\" : \"" + parametros.getBpmMin() + "\"} }";


        try {
            if (!mqtt.status()) {
                mqtt.conectar();
            }

            mqtt.enviarInfo("atualizarParametros", json);

        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

}
