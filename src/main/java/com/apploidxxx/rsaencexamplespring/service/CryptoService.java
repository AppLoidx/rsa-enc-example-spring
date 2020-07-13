package com.apploidxxx.rsaencexamplespring.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * @author Arthur Kupriyanov on 12.07.2020
 */
@Service
public class CryptoService {
    private final Cipher cipher = Cipher.getInstance("RSA");
    private final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

    public CryptoService() throws NoSuchPaddingException, NoSuchAlgorithmException {
    }

    public KeyPair createKeys() {
        return this.keyGen.generateKeyPair();
    }

    @SneakyThrows
    public String decryptText(String encMessage, PrivateKey key) {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(encMessage)), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public String encryptText(String msg, PublicKey key) {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
    }

    @Data
    @AllArgsConstructor
    public static class EncryptedMessage {
        private String message;
        private PublicKey publicKey;
        private PrivateKey privateKey;
    }

}
