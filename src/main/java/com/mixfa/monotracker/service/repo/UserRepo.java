package com.mixfa.monotracker.service.repo;

import com.mixfa.monotracker.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByAccountsIdsContains(String accountsIds);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
