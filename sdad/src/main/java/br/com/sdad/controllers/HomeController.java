package br.com.sdad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/home")
    public String index() {
        // FIXME transformar em log
        System.out.println("Carregando a página home!");
        return "homeUser";
    }

    @RequestMapping("/login")
    public String login() {
        // FIXME transformar em log
        System.out.println("Carregando a página de login!");
        return "login";
    }
}
