package springboot.SpringBoot.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springboot.SpringBoot.config.security.handler.SuccessUserHandler;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService; // сервис, с помощью которого тащим пользователя
    private final SuccessUserHandler successUserHandler; // класс, в котором описана логика перенаправления пользователей по ролям

    @Autowired
    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService, SuccessUserHandler successUserHandler) {
        this.userDetailsService = userDetailsService;
        this.successUserHandler = successUserHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
        //(текстовый пароль+соль) = шифрованный пароль
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());// конфигурация для прохождения аутентификации
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/user")
                //указываем что будет видно ролям Admin и User
                .hasAnyRole("ADMIN, USER")
                .antMatchers("/**")
                // Все что дальше /** видно только роли Admin
                .hasAnyRole("ADMIN")
                .and()
                .formLogin() // Spring сам подставит свою логин форму
                .successHandler(successUserHandler) // подключаем наш SuccessHandler для перенаправления по ролям
                // Handler - обработчик успешной аутентификации
                //.failureHandler(authenticationFailureHandler) //указываем логику обработки при неудачном логине
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                //выключаем кроссдоменную секьюрность
                .and()
                // Нужен для защиты куки чтобы сайт чужак не получил jsession ID. CSRF токен уточняет что в post запросе
                // должны быть данные с дополнительным csrf токеном(Чтобы сайт чужак не потделал запрос) Timelife генерит автоматически
                .csrf()
                .disable();
    }
}
