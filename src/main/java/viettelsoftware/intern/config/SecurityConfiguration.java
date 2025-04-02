package viettelsoftware.intern.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import viettelsoftware.intern.config.Jwt.JwtFilter;
import viettelsoftware.intern.config.Jwt.JwtUtil;
import viettelsoftware.intern.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfiguration {

    private static final Map<String, String[]> API_PERMISSIONS = new HashMap<>() {{
        put("/user/**", new String[]{"USER_VIEW", "USER_MANAGE"});
        put("/role/**", new String[]{"ROLE_VIEW", "ROLE_MANAGE"});
        put("/permission/**", new String[]{"PERMISSION_VIEW", "PERMISSION_MANAGE"});
        put("/book/**", new String[]{"BOOK_VIEW", "BOOK_MANAGE"});
        put("/borrowing/**", new String[]{"BORROW_VIEW", "BORROW_MANAGE"});
        put("/post/**", new String[]{"POST_VIEW_ALL", "POST_MANAGE"});
        put("/comment/**", new String[]{"COMMENT_VIEW_ALL", "COMMENT_MANAGE", "COMMENT_EDIT_OWN", "COMMENT_DELETE_OWN"});
        put("/genre/**", new String[]{"GENRE_VIEW", "GENRE_MANAGE"});
        put("/borrowing-book/**", new String[]{"BORROW_MANAGE"});
        put("/export/excel/**", new String[]{"EXPORT_DATA"}); // Tất cả API export
    }};

    private static final String[] AUTH_WHITELIST = {
            "/api/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };


    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    SecurityConfiguration(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()
//                {
//                    auth.requestMatchers(AUTH_WHITELIST).permitAll();
//
//                    API_PERMISSIONS.forEach((api, permission) ->
//                            auth.requestMatchers(api).hasAnyAuthority(permission)
//                    );
//
//                    auth.anyRequest().authenticated();
//                }
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(new JwtFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
