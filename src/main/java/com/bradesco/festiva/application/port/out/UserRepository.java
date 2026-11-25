package com.bradesco.festiva.application.port.out;

import com.bradesco.festiva.domain.model.UserAccount;

import java.util.Optional;

public interface UserRepository {

    Optional<UserAccount> findByEmail(String email);

    UserAccount save(UserAccount account);
}
