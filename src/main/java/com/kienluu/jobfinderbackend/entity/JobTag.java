package com.kienluu.jobfinderbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class JobTag {
    @Id
    private String id;
    private String tag;
}
