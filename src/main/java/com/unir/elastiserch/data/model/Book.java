package com.unir.elastiserch.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(indexName = "books", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "image")
    private String image;

    @Field(type = FieldType.Search_As_You_Type, name = "description")
    private String description;

    @Field(type = FieldType.Text, name = "year")
    private String year;

    @Field(type = FieldType.Text, name = "isbn")
    private String isbn;

    @Field(type = FieldType.Keyword, name = "author")
    private String author;

    @Field(type = FieldType.Keyword, name = "category")
    private String category;

    @Field(type = FieldType.Double, name = "price")
    private double price;

    @Field(type = FieldType.Integer, name = "score")
    private int score;

    @Field(type = FieldType.Integer, name = "stock")
    private int stock;

    @Field(type = FieldType.Boolean, name = "visible")
    private Boolean visible;

}
