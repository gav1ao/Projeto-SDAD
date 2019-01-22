/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sdad;


import org.springframework.stereotype.Component;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component("enviarEmail")
public class EnviarEmail {
    
    private String emailDestinatario;  //e-mail do responsavél

    private String assunto;  //assunto "informação"
    private String msg;     //mensagem a passar no e-mail"

    public String getEmailDestinatario() {
        return emailDestinatario;
    }

    public void setEmailDestinatario(String emailDestinatario) {
        this.emailDestinatario = emailDestinatario;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean enviarGmail() {
        boolean retorno = false;
        //Obter o objeto da sessão 
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session s = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {

                        return new PasswordAuthentication("sdad.infor@gmail.com", "SDAD12345");
                    }
                });

        //compor o email
        try {
            MimeMessage message = new MimeMessage(s);
            message.setFrom(new InternetAddress("sdad.infor@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.emailDestinatario));

            message.setSubject(this.assunto);
            message.setContent(this.msg, "text/html; charset=utf-8");

            //enviar menssagem 
            Transport.send(message);

            retorno = true;

        } catch (MessagingException e) {
            retorno = false;
            e.printStackTrace();
        }
        return retorno;
    }
    


}
