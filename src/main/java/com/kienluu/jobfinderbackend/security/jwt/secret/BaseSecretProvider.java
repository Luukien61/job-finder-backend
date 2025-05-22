package com.kienluu.jobfinderbackend.security.jwt.secret;


import com.kienluu.jobfinderbackend.security.jwt.algorithm.IAlgorithmProvider;
import lombok.Getter;

import java.security.Key;
import java.util.List;

@Getter
public abstract class BaseSecretProvider {
    protected String key;
    IAlgorithmProvider provider;


    abstract public List<Key> getKey();





}
