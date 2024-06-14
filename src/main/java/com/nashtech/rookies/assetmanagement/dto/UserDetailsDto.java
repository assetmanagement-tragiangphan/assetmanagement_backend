package com.nashtech.rookies.assetmanagement.dto;

import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Getter
@Setter
public class UserDetailsDto implements UserDetails {
    private Integer id;
    private RoleConstant roleName;
    private String username;
    private String password;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authority = new ArrayList<SimpleGrantedAuthority>();
        authority.add(new SimpleGrantedAuthority("ROLE_" + this.roleName.name()));
        return authority;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
