package com.kienluu.jobfinderbackend.security.jwt.secret;


import com.kienluu.jobfinderbackend.security.jwt.algorithm.IAlgorithmProvider;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class HS256Provider extends BaseSecretProvider{
    private final String JWT = "HS256_jwt_secretKey:";

    public HS256Provider(String key, IAlgorithmProvider provider) {
        this.provider = provider;
        this.key=key;
    }
    @Override
    public List<Key> getKey() {
        List<Key> keys = new ArrayList<>();
        byte[] decoded = Base64.getDecoder().decode(key); //(1)
        keys.add(new SecretKeySpec(
                decoded,
                0,
                decoded.length,
                provider.getAlgorithm().name())
        );
        return keys;
    }
}
/*
(1): after being decoded, the byte array is just raw data.
 So we need to convert it into SecretKey

(2): the key is byte format, so we need to encode it into string for storing.
 */