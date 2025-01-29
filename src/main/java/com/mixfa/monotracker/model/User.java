package com.mixfa.monotracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Document
@Getter
public class User implements UserDetails {
    @Id
    private final ObjectId id = ObjectId.get();
    private final String username;
    private final String passwordHash;

    private final String xToken; // mono token to setup webhook
    private final String clientId; // from mono
    private final String[] accountsIds; // Перелік доступних рахунків

    private final static Collection<SimpleGrantedAuthority> DEFAULT_AUTHORITIES = List.of(
            new SimpleGrantedAuthority("USER_ROLE")
    );

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return DEFAULT_AUTHORITIES;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public record RegisterRequest(
            String username,
            String password,
            String xToken
    ) {
    }
}
