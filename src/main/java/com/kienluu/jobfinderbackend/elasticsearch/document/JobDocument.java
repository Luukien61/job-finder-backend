package com.kienluu.jobfinderbackend.elasticsearch.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "jobs")
public class JobDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String location;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String companyName;

    @Field(type = FieldType.Keyword, index = false)
    private String companyId;

    @Field(type = FieldType.Keyword, index = false)
    private String logo;

    @Field(type = FieldType.Integer, index = false)
    private int experience;

    @Field(type = FieldType.Keyword, index = false)
    private String salary;

}
