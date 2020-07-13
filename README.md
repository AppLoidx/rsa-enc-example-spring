# RSA encryption example in Spring Boot

Using standart java encryption API (JCE)

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

So, use URL Encoder for avoid this problem in each param in request
