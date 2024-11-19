//package com.kienluu.jobfinderbackend.elasticsearch.document;
//
//import jakarta.persistence.Id;
//import lombok.*;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//
//@Document(indexName = "jobs")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class JobDocument {
//    @Id
//    private String id;
//
//    @Field(type = FieldType.Text, analyzer = "standard")
//    private String title;
//
//    @Field(type = FieldType.Text, analyzer = "standard")
//    private String location;
//}
