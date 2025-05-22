package com.kienluu.jobfinderbackend.security.jwt.algorithm;

import java.security.Key;
import java.util.List;

public interface IAlgorithmProvider {
    List<Key> generate() ;
    SecretAlgorithm getAlgorithm();
}
