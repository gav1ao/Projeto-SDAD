package br.com.sdad.controllers;

import br.com.sdad.EnviarEmail;
import br.com.sdad.mqtt.MqttPublishSubscribeUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;

@Controller
@Transactional
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MqttPublishSubscribeUtils mqtt;

    @Autowired
    private EnviarEmail enviarEmail;

    @RequestMapping("/home")
    public ModelAndView home() {
        return new ModelAndView("homeUser");
    }

    @RequestMapping("/mqtt/conectar")
    public ModelAndView mqtt (){
        try {
            mqtt.conectar();
            mqtt.receberInfo("quedaAlerta");
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return new ModelAndView("redirect:/admin/mqtt/status");
    }

    @RequestMapping("/mqtt/status")
    public ModelAndView statusMqtt(){
        ModelAndView modelAndView = new ModelAndView("mqttStatus");
        Boolean status;
        try {
            status = mqtt.status();
        }
        catch (NullPointerException npe){
            System.out.println("Mqtt null");
            status = null;
        }

        modelAndView.addObject("status", status);

        return modelAndView;
    }

    @RequestMapping("/mqtt/testarMensagem")
    public ModelAndView testarMensagem(@RequestParam(value = "msg") String msg){
//        // teste envio email
//        enviarEmail.setAssunto("SDAD - Teste");
//        enviarEmail.setEmailDestinatario("vigal.jan@gmail.com");
//        enviarEmail.setMsg(msg);
//        enviarEmail.enviarGmail();

        mqtt.enviarInfo("teste", msg);
        return new ModelAndView("redirect:/admin/mqtt/status");
    }

    @RequestMapping("/mqtt/desconectar")
    public ModelAndView desconectar(){
        mqtt.desconectar();
        return new ModelAndView("redirect:/admin/mqtt/status");
    }
}
