package br.com.sdad.conf;

import br.com.sdad.EnviarEmail;
import br.com.sdad.controllers.AdminController;
import br.com.sdad.controllers.HomeController;
import br.com.sdad.controllers.UsuarioController;
import br.com.sdad.daos.InfoVitalDAO;
import br.com.sdad.daos.UsuarioDAO;
import br.com.sdad.daos.UsuarioSistemaDAO;
import br.com.sdad.mqtt.MqttPublishSubscribeUtils;
import br.com.sdad.services.ProcessaTopicoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses =
        {
                HomeController.class,
                UsuarioController.class,
                AdminController.class,
                UsuarioDAO.class,
                UsuarioSistemaDAO.class,
                InfoVitalDAO.class,
                ProcessaTopicoService.class,
                MqttPublishSubscribeUtils.class,
                EnviarEmail.class
        }
    )
public class AppWebConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public InternalResourceViewResolver
    internalResourceViewResolver() {
        InternalResourceViewResolver resolver =
                new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
}
