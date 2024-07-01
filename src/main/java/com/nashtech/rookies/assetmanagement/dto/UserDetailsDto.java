package com.nashtech.rookies.assetmanagement.dto;

import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto implements UserDetails {
    private Integer id;
    private RoleConstant roleName;
    private String username;
    private String password;
    private LocationConstant location;
    private StatusConstant status;

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

    @Override
    public boolean isEnabled() {
        return this.status == StatusConstant.ACTIVE;
    }
}
