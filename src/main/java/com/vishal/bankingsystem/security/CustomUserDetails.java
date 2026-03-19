package com.vishal.bankingsystem.security;

import com.vishal.bankingsystem.auth.entity.PermissionEntity;
import com.vishal.bankingsystem.auth.entity.RoleEntity;
import com.vishal.bankingsystem.auth.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final UserEntity users;

    //Convert roles and permissions into Spring Security authorities
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (users.getRoles() == null || users.getRoles().isEmpty()) {
            return Collections.emptySet();
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (RoleEntity role : users.getRoles()) {
            if (role == null) {
                continue;
            }
            // Add role as authority
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getName())
            );
            // Add permissions as authorities
            if (role.getPermissions() == null) {
                continue;
            }

            for (PermissionEntity permission : role.getPermissions()) {
                if (permission == null) {
                    continue;
                }
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
        LocalDate accountExpiryDate = users.getAccountExpiryDate();
        return accountExpiryDate == null || !accountExpiryDate.isBefore(LocalDate.now());
    }

    // Account lock check
    @Override
    public boolean isAccountNonLocked() {
        return !users.isAdminUnlockRequired()
                && !users.isAccountLocked()
                && (users.getLockUntil() == null || !users.getLockUntil().isAfter(LocalDateTime.now()));
    }

    // Credentials expiration check
    @Override
    public boolean isCredentialsNonExpired() {
        LocalDate passwordExpiryDate = users.getPasswordExpiryDate();
        return passwordExpiryDate == null || !passwordExpiryDate.isBefore(LocalDate.now());
    }

    // Account enabled check
    @Override
    public boolean isEnabled() {
        return users.isEnabled();
    }
}
