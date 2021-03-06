package za.co.judge.filters;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import za.co.judge.domain.Role;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final String HEADER_STRING = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    private String signingKey;

    @Autowired
    public JWTAuthorizationFilter(AuthenticationManager authManager, String signingKey) {
        super(authManager);
        this.signingKey = signingKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        token = token.replace(TOKEN_PREFIX, "");
        if (token != null) {
            String user = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(signingKey.getBytes())).parseClaimsJws(token).getBody().getSubject();
            String roles = (String) Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(signingKey.getBytes())).parseClaimsJws(token).getBody().get("roles");
            StringTokenizer roleTokenizer = new StringTokenizer(roles, ",");
            LinkedList<Role> userRoles = new LinkedList<>();
            while(roleTokenizer.hasMoreElements()){
                Role userRole = new Role();
                userRole.setAuthority(roleTokenizer.nextToken());
                userRoles.push(userRole);
            }
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, userRoles);
            }
            return null;
        }
        return null;
    }
}
