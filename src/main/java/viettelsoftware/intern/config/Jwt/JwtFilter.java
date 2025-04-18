package viettelsoftware.intern.config.Jwt;

import io.jsonwebtoken.Claims;
import io.micrometer.common.lang.NonNullApi;
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
import viettelsoftware.intern.config.UserPrincipal;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.CustomException;
import viettelsoftware.intern.repository.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
@NonNullApi
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        if (StringUtils.hasText(token) && !jwtUtil.isTokenExpired(token)) {
            String username = jwtUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserEntity userEntity = userRepository.findByUsernameIgnoreCase(username)
                        .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND));

                Claims claims = jwtUtil.getClaims(token);
                Object rolesClaim = claims.get("scope");

                Collection<? extends GrantedAuthority> authorities = Arrays.stream(rolesClaim.toString().split(" "))
                        .filter(auth -> !auth.trim().isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UserPrincipal userPrincipal = new UserPrincipal(userEntity);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}