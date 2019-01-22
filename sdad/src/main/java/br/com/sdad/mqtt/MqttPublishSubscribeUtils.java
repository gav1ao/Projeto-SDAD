package br.com.sdad.mqtt;

import br.com.sdad.services.ProcessaTopicoService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

@Component
class SdadMqttCallback implements MqttCallback {

    private Logger Log = LoggerFactory.getLogger(SdadMqttCallback.class);

    // Chamado quando a conexão com o Broker é perdida
    @Override
    public void connectionLost(Throwable arg0) {
        Log.error("Conexão perdida com o Broker");
    }

    // Chamado quando uma mensagem chega
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String mensagem = new String(message.getPayload());

        System.out.println("-------------------------------------------------");
        System.out.println("| Topic:" + topic);
        System.out.println("| Message: " + mensagem);
        System.out.println("-------------------------------------------------");

        ProcessaTopicoService.processaTopico(topic, mensagem);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.debug("Entrega realizada");
    }

}

/**
 * This class holds the MQTT methods to Connect, Publish & Subscribe to Broker
 *
 */

@Component("mqtt")
public class MqttPublishSubscribeUtils {
    private Logger Log = LoggerFactory.getLogger(MqttPublishSubscribeUtils.class);

    public MqttClient client;

    private final static String PROPERTIES_FILE_NAME = "/mqtt.properties";
    Properties props = new Properties();

    public void conectar() throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();

        // Carrega as configurações a partir do arquivo de propriedades
        try {
            props.load(MqttPublishSubscribeUtils.class.getResourceAsStream(PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            Log.error("Não foi possível efetuar a leitura do arquivo de propriedades");
        }

        Log.info("Conectando ao Broker com os seguintes parâmetros: BROKER_URL=" + props.getProperty("BROKER_URL") + " CLIENT_ID=" + props.getProperty("CLIENT_ID"));

        this.client = new MqttClient(props.getProperty("BROKER_URL"), props.getProperty("CLIENT_ID"), persistence);

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);

        connOpts.setUserName(props.getProperty("USER"));
        connOpts.setPassword(props.getProperty("PASSWORD").toCharArray());

        this.client.connect(connOpts);
        Log.info("Conectado ao Broker [{}]", props.getProperty("BROKER_URL"));
    }


    public void enviarInfo(String topico, String mensagem) {
        try {
            Log.debug("Messagem a ser publicada: [{}]", mensagem);
            MqttMessage message = new MqttMessage(mensagem.getBytes(Charset.forName("UTF-8")));

            if (props.getProperty("QOS") != null) {
                message.setQos(Integer.parseInt(props.getProperty("QOS")));
            }

            this.client.setCallback(new SdadMqttCallback());
            this.client.publish(topico, message);
            Log.debug("Mensagem publicada");

        } catch (MqttException me) {
            Log.error("Error ao enviar as informações ao Broker", me);
        }
    }

    public void receberInfo(String topic){
        try {
            this.client.subscribe(topic, 1);
            this.client.setCallback(new SdadMqttCallback());

        } catch (MqttException me) {
            Log.error("Error ao receber as informações do Broker", me);
        }
    }

    public void desconectar(){
        try {
            Log.debug("Efetuando desconexão com o Broker");
            this.client.disconnect();
        } catch (MqttException e) {
            Log.error("Erro ao desconectar do Broker");
        }

        Log.info("Deconectado do Broker com sucesso");
    }

    public boolean status(){
        return this.client.isConnected();
    }
}
