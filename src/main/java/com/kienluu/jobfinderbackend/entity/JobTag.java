package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class JobTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String tag;
}
