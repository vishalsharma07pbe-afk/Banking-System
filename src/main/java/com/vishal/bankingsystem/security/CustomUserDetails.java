package com.vishal.bankingsystem.security;

import com.vishal.bankingsystem.auth.entity.PermissionEntity;
import com.vishal.bankingsystem.auth.entity.RoleEntity;
import com.vishal.bankingsystem.auth.entity.UsersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final UsersEntity users;

    //Convert roles and permissions into Spring Security authorities
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (RoleEntity role : users.getRole()) {
            // Add role as authority
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getName())
            );
            // Add permissions as authorities
            for (PermissionEntity permission : role.getPermissions()) {
                authorities.add(
                        new SimpleGrantedAuthority(permission.getName())
                );
            }
        }
        return authorities;
    }


    // Return password from database
    @Override
    public String getPassword() {
        return users.getPassword();
    }

    //Return username from database
    @Override
    public String getUsername() {
        return users.getUserName();
    }

    // Account expiration check
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Account lock check
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Credentials expiration check
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Account enabled check
    @Override
    public boolean isEnabled() {
        return true;
    }
}

