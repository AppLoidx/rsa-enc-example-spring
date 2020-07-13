# RSA encryption example in Spring Boot

Using standart java encryption API (JCE)

Usage (java 11):
```shell
mvn clean install
java -jar target/*.jar
```

## Get public key

To get public key go to the following link:
```url
http://localhost:8080/open-key
```

## Encrypt message
```url
http://localhost:8080/encrypt?msg=aloha&publicKey=YOUR_PUBLIC_KEY_ON_PREV_STEP
```

## Check with decrpyt
```url
http://localhost:8080/check?msg=ENCRYPTED_MESSAGE_FROM_PREV_STEP
```


## WARNINGS
For each operation where you provide key or message - you need to use URL Encoder for params. Because, keys generally have char like "+" which is will be converted to " " (space).

For example you have Public Key:
```
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq2yyZCZBY35Y8eveXx8qvp441+6G/8MVYgLKh4gOOV3aiWFTR5fota6OtJ9R4T2Dcq4ELy/APHEgxZrwPkd5EzgY8iJkOyF76wMurkn0phWHZJEKACxwuaE2tw+2Eu5O21nmiiPvizDRnau9LN724+oeiJyxdm+t7TYv+aOCnhE6g7089THp5q+z4+do9S9eGu31iM6/MLvzuGQuE/91KqAqMPIIMz6iKV+RZoHcedLHKFViaVasMXvzwpdUcWXF59UCEwdTrUqzenut/gmknx0jfFrY/NyV97hwaLDec+h+xmqbNrjflYjHZwdIuorGggFCLU9pbmEiaVhaIc6vTQIDAQAB
```

which contains **plus**. Each plus from this key will be converted to space in processing request.

`hsdf+asdfasf` -> `hsdf asdfasf`

So, use URL Encoder to avoid this problem in each param in request

## Source code
Global params:
```java
private final Cipher cipher = Cipher.getInstance("RSA");
private final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
```

Encrpyt text with Public key:

```java
public String encryptText(String msg, PublicKey key) {
    this.cipher.init(Cipher.ENCRYPT_MODE, key);
    return Base64.encodeBase64String(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
}
```

Decypt text with Private key:

```java
public String decryptText(String encMessage, PrivateKey key) {
    this.cipher.init(Cipher.DECRYPT_MODE, key);
    return new String(cipher.doFinal(Base64.decodeBase64(encMessage)), StandardCharsets.UTF_8);
}
```

See implementation of CryptoService: [Source Code](src/main/java/com/apploidxxx/rsaencexamplespring/service/CryptoService.java)

See controller from this project : [Source Code](src/main/java/com/apploidxxx/rsaencexamplespring/controller/CryptoController.java)

## Additional things

Sometimes (in client, for example) you have to get Public Key from param (string).

For do this, you can use the following code:
```java
byte[] byteKey = Base64.getDecoder().decode(publicKeyString.getBytes());
X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
KeyFactory kf = KeyFactory.getInstance("RSA");

PublicKey pk = kf.generatePublic(X509publicKey);
```

You can see usage of this code in [CryptoController](src/main/java/com/apploidxxx/rsaencexamplespring/controller/CryptoController.java):


```java
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
```
