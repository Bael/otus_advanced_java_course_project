package com.github.bael.otus.java.userservice.domain;


import com.github.bael.otus.java.userservice.data.UserAccountRepository;
import com.github.bael.otus.java.userservice.entity.UserAccount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<UserAccount> findAll() {
        return userAccountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> findById(UUID id) {
        return userAccountRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Transactional
    public UserAccount save(UserAccount user) {
        return userAccountRepository.save(user);
    }

    @Transactional
    public void disableById(UUID id) {
        var user = userAccountRepository.findById(id).orElse(null);
        if (user != null) {
            user.setEnabled(false);
            userAccountRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return userAccountRepository.existsById(id);
    }
}
