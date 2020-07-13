package com.apploidxxx.rsaencexamplespring.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

/**
 * @author Arthur Kupriyanov on 13.07.2020
 */
@Entity
@NoArgsConstructor
@Data
public class KeyStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Lob
    @Column( length = 2000 )
    private PublicKey publicKey;

    @Lob
    @Column( length = 2000 )
    private PrivateKey privateKey;

    public KeyStorage(PublicKey pubKey, PrivateKey privKey) {
        publicKey = pubKey;
        privateKey = privKey;
    }

    public KeyStorage(KeyPair keyPair) {
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }
}
