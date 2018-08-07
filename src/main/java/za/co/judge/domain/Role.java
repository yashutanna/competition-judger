package za.co.judge.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class Role implements GrantedAuthority {
    String authority;
    @Override
    public String getAuthority(){
        return "ROLE_" + authority.toUpperCase();
    }
}
