package com.apploidxxx.rsaencexamplespring.data;

import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Arthur Kupriyanov on 13.07.2020
 */
@Service
public class KeyStoreService {
    private final KeyStorageRepo keyStorageRepo;

    public KeyStoreService(KeyStorageRepo keyStorageRepo) {
        this.keyStorageRepo = keyStorageRepo;
    }

    public UUID storeKeys(KeyPair keyPair) {
        KeyStorage ks = new KeyStorage(keyPair);
        keyStorageRepo.saveAndFlush(ks);

        return ks.getUuid();
    }

    public Optional<KeyStorage> getKey(UUID uuid) {
        return keyStorageRepo.findById(uuid);
    }
}
