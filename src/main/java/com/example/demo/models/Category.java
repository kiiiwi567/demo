package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "category", schema =  "public")
public class Category {
    @Id
    @Column(name ="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;
}
