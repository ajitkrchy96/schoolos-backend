package com.school.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalDemoDataInitializerTest {

    @Test
    void initializerIsRestrictedToLocalDemoProfile() {
        Profile profile = LocalDemoDataInitializer.class.getAnnotation(Profile.class);

        assertArrayEquals(new String[]{"local-demo"}, profile.value());
    }

    @Test
    void missingCredentialsFailBeforeDatabaseAccess() {
        LocalDemoDataInitializer initializer = new LocalDemoDataInitializer(
            null, null, null, null, null, null, null, null, null, null, null,
                new BCryptPasswordEncoder(), "", ""
        );

        assertThrows(IllegalStateException.class, () -> initializer.run());
    }
}