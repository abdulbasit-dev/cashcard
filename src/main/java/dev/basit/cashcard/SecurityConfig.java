package dev.basit.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // All HTTP requests to cashcards/ endpoints are required to be authenticated
        // using HTTP Basic Authentication security (username and password).
        // Also, do not require CSRF security
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/api/cashcards/**")
                // .authenticated()
                .hasRole("CARD-OWNER")) // enable RBAC: Replace the .authenticated() call with the hasRole(...) call.
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails sarah = users
                .username("sarah1")
                .password(passwordEncoder.encode("password"))
                .roles("CARD-OWNER") // no roles for now
                .build();

        UserDetails handOwnsNoCard = users
                .username("hank")
                .password(passwordEncoder.encode("password"))
                .roles("NON-OWNER")
                .build();

        UserDetails kumar = users
                .username("kumar2")
                .password(passwordEncoder.encode("password"))
                .roles("CARD-OWNER")
                .build();

        return new InMemoryUserDetailsManager(sarah, handOwnsNoCard, kumar);
    }
}
