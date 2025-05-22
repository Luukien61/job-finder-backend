package com.kienluu.jobfinderbackend.security.jwt.algorithm;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import java.security.Key;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ES256Algorithm implements IAlgorithmProvider {
    private final SecretAlgorithm algorithm = SecretAlgorithm.EC;
    @Override
    public List<Key> generate() {
        KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.ES256);
        var publicKey = keyPair.getPublic();
        var privateKey = keyPair.getPrivate();
        List<Key> keys= new ArrayList<>();
        keys.add(privateKey);
        keys.add(publicKey);
        return keys;
    }
}
