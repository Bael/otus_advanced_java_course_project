package com.github.bael.otus.java.userservice.data;

import com.github.bael.otus.java.userservice.entity.UserAccount;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface ReactiveUserAccountRepository extends ReactiveCrudRepository<UserAccount, UUID> {

    @Query("SELECT * FROM user_accounts WHERE username = :username")
    Mono<UserAccount> findByUsername(String username);

    @Query("SELECT * FROM user_accounts WHERE email = :email")
    Mono<UserAccount> findByEmail(String email);

    @Query("SELECT COUNT(*) > 0 FROM user_accounts WHERE username = :username")
    Mono<Boolean> existsByUsername(String username);

    @Query("SELECT COUNT(*) > 0 FROM user_accounts WHERE email = :email")
    Mono<Boolean> existsByEmail(String email);
}