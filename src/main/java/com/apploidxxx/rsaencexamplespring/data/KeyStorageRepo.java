package com.apploidxxx.rsaencexamplespring.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author Arthur Kupriyanov on 13.07.2020
 */
public interface KeyStorageRepo extends JpaRepository<KeyStorage, UUID> {
}
