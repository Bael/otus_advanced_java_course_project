package com.github.bael.otus.java.userservice.domain;

import com.github.bael.otus.java.userservice.data.ReactiveUserAccountRepository;
import com.github.bael.otus.java.userservice.entity.UserAccount;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccountService {

    private final ReactiveUserAccountRepository userAccountRepository;
//    private final PasswordEncoder passwordEncoder;

    public Flux<UserAccount> findAll() {
        return userAccountRepository.findAll();
    }

    public Flux<UserAccount> findByIds(Set<UUID> ids) {
        return userAccountRepository.findAllById(ids);
    }

    public Mono<UserAccount> findById(UUID id) {
        return userAccountRepository.findById(id);
    }

    public Mono<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    public Mono<UserAccount> createUser(UserAccount userAccount, String rawPassword) {
        log.info("Creating user {}", userAccount);

//        userAccount.setPasswordHash(passwordEncoder.encode(rawPassword));
        userAccount.setPasswordHash(rawPassword);
        userAccount.setCreatedAt(Instant.now());
//        userAccount.setUpdatedAt(LocalDateTime.now());
        userAccount.setEnabled(true);

        var user = userAccountRepository.save(userAccount);
        log.info("User {} created" , userAccount);
        return user;
    }

    public Mono<UserAccount> updateUser(UserAccount userAccount) {
//        userAccount.setUpdatedAt(LocalDateTime.now());
        return userAccountRepository.save(userAccount);
    }

    public Mono<Void> deleteById(UUID id) {
        return userAccountRepository.deleteById(id);
    }

    public Mono<Boolean> existsByUsername(String username) {
        return userAccountRepository.existsByUsername(username);
    }

    public Mono<Boolean> existsByEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    public Mono<UserAccount> findByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }
}