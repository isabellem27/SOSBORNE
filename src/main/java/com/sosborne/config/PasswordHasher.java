package com.sosborne.config;

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Component
public class PasswordHasher {
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int PARALLELISM = 1;
    private static final int MEMORY = 65536;
    private static final int ITERATIONS = 3;

    private final Argon2PasswordEncoder encoder =
            new Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, PARALLELISM, MEMORY, ITERATIONS);

    public String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean verifyPassword(String password, String hash) {
        return encoder.matches(password, hash);
    }
}
