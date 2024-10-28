package com.intuit.biddingSystem.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails extends User implements UserDetails {

    private UUID uuid;
    public UUID getUuid() {
        return uuid;
    }

    public CustomUserDetails(UUID uuid, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.uuid = uuid;
    }
}
