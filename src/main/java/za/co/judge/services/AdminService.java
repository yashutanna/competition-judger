package za.co.judge.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.co.judge.domain.Admin;
import za.co.judge.repositories.AdminRepository;

import java.security.Key;
import java.util.HashMap;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRespository;
    @Value("${signingKey}")
    private String signingKey;

    public String authenticate(String name, String password) {
        Admin resolvedAdminUser = adminRespository.findByNameAndPassword(name, password);
        if(resolvedAdminUser == null){
            return null;
        }
        Key key = Keys.hmacShaKeyFor(signingKey.getBytes());
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("roles", "admin");
        claims.put("sub", resolvedAdminUser.getName());
        String jwt = Jwts.builder().setClaims(claims).signWith(key).compact();
        assert Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody().getSubject().equals(resolvedAdminUser.getName());
        return jwt;
    }
}
