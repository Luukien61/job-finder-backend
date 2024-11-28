package com.kienluu.jobfinderbackend.elasticsearch.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "jobs")
@Setting(settingPath = "static/elasticsearch/settings.json")
public class JobDocument {
    @Id
    private String id;

    @Field(type = Text, analyzer = "standard")
    private String title;

    @Field(type = Text,normalizer = "lowercase_normalizer")
    private String location;

    @Field(type = Text, analyzer = "standard")
    private String companyName;

    @Field(type = Keyword, index = false)
    private String companyId;

    @Field(type = Keyword, index = false)
    private String logo;

    @Field(type = FieldType.Integer)
    private int experience;

    @Field(type = FieldType.Integer)
    private int minSalary;

    @Field(type = FieldType.Integer)
    private int maxSalary;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate expiryDate;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate createDate;


//    @MultiField(
//            mainField = @Field(type = Text, fielddata = true),
//            otherFields = {
//                    @InnerField(suffix = "verbatim", type = Keyword,analyzer = "lowercase_normalizer")
//            }
//    )
//    private String example;

}

/*
for more information about how to config and set up an elastic search, follow this article:
https://www.baeldung.com/spring-data-elasticsearch-tutorial
 */