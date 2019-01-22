package br.com.sdad.controllers;

import br.com.sdad.daos.InfoVitalDAO;
import br.com.sdad.daos.UsuarioDAO;
import br.com.sdad.daos.UsuarioSistemaDAO;
import br.com.sdad.entidades.Role;
import br.com.sdad.entidades.informacoes.Parametros;
import br.com.sdad.entidades.pessoas.UsuarioDLO;
import br.com.sdad.entidades.pessoas.UsuarioSistema;
import br.com.sdad.services.ProcessaTopicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.Collections;

@Controller
@Transactional
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private UsuarioSistemaDAO usuarioSistemaDAO;

    @Autowired
    private InfoVitalDAO infoVitalDAO;

    @RequestMapping(value = "/cadastrar", method = RequestMethod.POST)
    public ModelAndView cadastrar(@ModelAttribute UsuarioDLO usuarioDLO,
                                  @RequestParam int tempMin,
                                  @RequestParam int tempMax,
                                  @RequestParam int bpmMax,
                                  @RequestParam int bpmMin) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String senhaCriptografada = passwordEncoder.encode(usuarioDLO.getSenha());

        usuarioDLO.setSenha(senhaCriptografada);
        usuarioDAO.cadastrar(usuarioDLO);

        UsuarioSistema usuarioSistema = new UsuarioSistema(usuarioDLO.getLogin(),
                                                           senhaCriptografada,
                                                           usuarioDLO.getNome(),
                                                      null);

        if(usuarioDLO.isAdmin()) {
            usuarioSistema.setRoles(Collections.singletonList(new Role(Role.TipoUsuario.ADMINISTRADOR)));
        } else {
            usuarioSistema.setRoles(Collections.singletonList(new Role(Role.TipoUsuario.USUARIO)));
        }

        usuarioSistemaDAO.cadastrar(usuarioSistema);

        int idUsuario = usuarioDAO.getIdUsuarioFromLogin(usuarioSistema.getLogin());

        Parametros parametros = new Parametros(tempMin, tempMax, bpmMin, bpmMax, idUsuario);

//        infoVitalDAO.salvarParametros(parametros);

        ProcessaTopicoService.atualizarParametros(usuarioDLO.getIdDispositivo(), parametros);

        return new ModelAndView("redirect:/admin");
    }

    @RequestMapping("/form")
    public ModelAndView form(){
        ModelAndView modelAndView = new ModelAndView("cadastrarUsuario");
        return modelAndView;
    }

    @RequestMapping("/home")
    public ModelAndView home(){

        ModelAndView modelAndView = new ModelAndView("homeUser");

        UsuarioSistema user = (UsuarioSistema) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String login = user.getUsername();

        int idUsuario = usuarioDAO.getIdUsuarioFromLogin(login);

        String nome = usuarioDAO.getNomeUsuarioFromId(idUsuario);

        modelAndView.addObject("userLogin", nome);
        modelAndView.addObject("batimentos", infoVitalDAO.listarBatimentos(idUsuario));
        modelAndView.addObject("quedas", infoVitalDAO.listarQuedas(idUsuario));
        modelAndView.addObject("temperaturas", infoVitalDAO.listarTemperaturas(idUsuario));

    return modelAndView;
    }
}
