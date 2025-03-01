package com.unir.elastiserch.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CreateBookRequest {
	private String title;
	private String image;
	private String description;
	private String year;
	private String isbn;
	private String author;
	private String category;
	private Double price;
	private Integer score;
	private Integer stock;
	private Boolean visible;
}

