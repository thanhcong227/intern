package viettelsoftware.intern.config.Jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (StringUtils.hasText(token) && !jwtUtil.isTokenExpired(token)) {
            String username = jwtUtil.extractUsername(token);
            UserEntity userEntity = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            if (userEntity != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Claims claims = jwtUtil.getClaims(token);

                Object rolesClaim = claims.get("scope");
                if (rolesClaim == null) {
                    throw new AppException(ErrorCode.USER_NOT_FOUND);
                } else {
                    log.info("rolesClaim: {}", rolesClaim);
                }
                Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
                authorities = Arrays.stream(rolesClaim.toString().split(" "))
                        .filter(auth -> !auth.trim().isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userEntity, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("Authorization Header: {}", bearerToken); // Kiểm tra giá trị header
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.info("Extracted Token: {}", token); // Kiểm tra token đã cắt đúng chưa
            return token;
        }
        return null;
    }
}
