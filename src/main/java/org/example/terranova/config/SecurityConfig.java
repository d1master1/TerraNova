package org.example.terranova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/login", "/include/registration/**", "/logout",
                                "/webjars/**", "/css/**", "/js/**", "/images/**", "/fonts/**",
                                "/header", "/footer", "/about", "/include/about",
                                "/uploads/**", "/img/**"
                        ).permitAll()
                        .requestMatchers("/", "/realty/realty_list", "/include/profile", "/include/profile/**", "/deal/deal_list")
                        .hasAnyRole("USER", "EMPLOYEE", "ADMIN")
                        .requestMatchers("/realty/realty_form", "/deal/deal_form", "/client/client_list", "/client/client_form")
                        .hasAnyRole("EMPLOYEE", "ADMIN")
                        .requestMatchers("/employee/employee_form", "/employee/employee_list", "/user/user_list", "/user/user_form")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/profile", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
