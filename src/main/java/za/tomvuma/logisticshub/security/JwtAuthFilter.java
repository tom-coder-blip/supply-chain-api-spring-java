package za.tomvuma.logisticshub.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends GenericFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String header = request.getHeader("Authorization");

        // ✅ No token → continue as anonymous
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String token = header.substring(7);
            var claims = jwtUtil.parse(token).getBody();

            String email = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            var authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();

            var authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ignored) {
            // Invalid token → treated as anonymous
        }

        chain.doFilter(req, res);
    }
}
