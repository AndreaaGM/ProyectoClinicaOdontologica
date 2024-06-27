package BackEndC2.ClinicaOdontologica.security;

import BackEndC2.ClinicaOdontologica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class ConfigWebSecurity {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Bean //este metodo va a proveernos de la autenticacion de la aplicacion
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(usuarioService);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/h2-console/**","/odontologos/**", "/pacientes/**","/turnos/**").permitAll()
                        .requestMatchers("/home_admin.html","/get_pacientes.html", "/post_pacientes.html", "/get_odontologos.html", "/post_odontologos.html").hasRole("ADMIN")
                        .requestMatchers("/home_user.html","/get_turnos.html", "/post_turnos.html").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .successHandler(new AuthenticationSucessHandler())
                            .permitAll();
                })
                .logout(withDefaults());

        return http.build();
    }

}

