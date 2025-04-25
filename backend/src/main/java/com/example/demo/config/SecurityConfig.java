package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Wyłącz CSRF (dla prostoty / H2, REST itp.)
                .csrf(csrf -> csrf.disable())

                // 2. Reguły dostępu
                .authorizeHttpRequests(auth -> auth
                        // strona logowania i przetwarzanie POST nie wymagają uwierzytelnienia
                        .requestMatchers("/login", "/login-error", "/perform_login").permitAll()
                        // dostęp do konsoli H2 i assets (jeżeli z niej korzystasz)
                        .requestMatchers("/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
                        // reszta pod ochroną
                        .anyRequest().authenticated()
                )

                // 3. Konfiguracja formularza logowania
                .formLogin(form -> form
                        .loginPage("/login")               // URL do Twojego kontrolera GET /login
                        .loginProcessingUrl("/perform_login") // gdzie wysyłasz POST z credentials
                        .defaultSuccessUrl("/tasks", true) // po udanym loginie trafi na /tasks
                        .failureUrl("/login?error=true")   // w razie porażki
                        .permitAll()                       // permitAll() obejmie tu GET i POST
                )

                // 4. Wylogowanie
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // 5. H2-console w iframe
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }
}
