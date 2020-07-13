package com.apploidxxx.rsaencexamplespring.controller;

import com.apploidxxx.rsaencexamplespring.data.KeyStorage;
import com.apploidxxx.rsaencexamplespring.data.KeyStoreService;
import com.apploidxxx.rsaencexamplespring.service.CryptoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Arthur Kupriyanov on 12.07.2020
 */
@RestController
@Slf4j
public class CryptoController {

    private final CryptoService cryptoService;
    private final KeyStoreService keyStoreService;

    /**
     * Test message for encryption
     */
    private static final String TEST_MESSAGE = "aloha";

    /**
     * Client cookie name
     */
    private static final String KEY_UUID = "KEY_UUID";

    /**
     * @param cryptoService   for encrypt and decrypt
     * @param keyStoreService storing keys and client identifier
     */
    public CryptoController(CryptoService cryptoService, KeyStoreService keyStoreService) {
        this.cryptoService = cryptoService;
        this.keyStoreService = keyStoreService;
    }

    /**
     * Generates public-key and sets cookie for browser (for check encoding)
     *
     * @return public key
     */
    @GetMapping("/open-key")
    public ResponseEntity<String> getOpenKey() {
        KeyPair pair = cryptoService.createKeys();
        UUID uuid = keyStoreService.storeKeys(pair);
        String encodedPublicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        log.info("Generated public key in Base64 format: " + encodedPublicKey);
        return ResponseEntity.status(200)
                .header("Set-Cookie", String.format("%s=%s", KEY_UUID, uuid.toString()))
                .body(encodedPublicKey);
    }

    /**
     * @param publicKeyString generated public key in url <code>/encrypt</code>
     * @param msg             message encrypt
     * @return encrypted message
     */
    @SneakyThrows
    @GetMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestParam("publicKey") String publicKeyString,
                                          @RequestParam("msg") String msg) {
        log.info("Public key is " + publicKeyString);
        byte[] byteKey = Base64.getDecoder().decode(publicKeyString.getBytes());
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        PublicKey pk = kf.generatePublic(X509publicKey);
        return ResponseEntity.ok(cryptoService.encryptText(msg, pk));
    }

    /**
     * @param msg     test message which require to be equal to "aloha"
     * @param request for get client cookies
     * @return <code>200</code> - success check, encryption is right <br/>
     * <code>404</code> - something went wrong
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkEnc(@RequestParam("msg") String msg, HttpServletRequest request) {
        String uuidString = null;
        for (Cookie cookie : request.getCookies()) {
            if (KEY_UUID.equals(cookie.getName())) {
                uuidString = cookie.getValue();
                break;
            }
        }

        if (uuidString == null) {
            return ResponseEntity.badRequest().build();
        }

        UUID uuid = UUID.fromString(uuidString);
        Optional<KeyStorage> ks = keyStoreService.getKey(uuid);
        if (ks.isPresent()) {
            if (TEST_MESSAGE.equals(cryptoService.decryptText(msg, ks.get().getPrivateKey()))) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
