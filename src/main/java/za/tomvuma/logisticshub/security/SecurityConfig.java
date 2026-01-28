package za.tomvuma.logisticshub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtil jwtUtil(JwtProperties props) {
        return new JwtUtil(props.getSecret(), props.getExpiration());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {

        http
                // REQUIRED for JWT apps
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                // Stateless API
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers(
                                "/",
                                "/error",
                                "/health",
                                "/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Secured
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/suppliers/**").hasAnyRole("SUPPLIER", "ADMIN")
                        .requestMatchers("/warehouses/**").hasAnyRole("WAREHOUSE_MANAGER", "ADMIN")
                        .requestMatchers("/orders/**").hasAnyRole("CUSTOMER", "ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(new JwtAuthFilter(jwtUtil), BasicAuthenticationFilter.class);

        return http.build();
    }
}
