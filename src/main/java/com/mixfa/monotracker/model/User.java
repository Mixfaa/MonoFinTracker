package com.mixfa.monotracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
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
@With
public class User implements UserDetails {
    @Id
    private final ObjectId id;
    private final String username;
    private final String passwordHash;
    private final String xToken; // mono token to setup webhook
    private final String clientId; // from mono
    private final String[] accountsIds; // Перелік доступних рахунків
    private final int preferredCurrency; // can not be changed later

    public User(String username, String passwordHash, String xToken, String clientId, String[] accountsIds, int preferredCurrency) {
        this.id = ObjectId.get();
        this.username = username;
        this.passwordHash = passwordHash;
        this.xToken = xToken;
        this.clientId = clientId;
        this.accountsIds = accountsIds;
        this.preferredCurrency = preferredCurrency;
    }

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
            String xToken,
            int preferredCurrency
    ) {
    }

    public record UpdateRequest(
            String username,
            String xToken
    ) {
    }
}
