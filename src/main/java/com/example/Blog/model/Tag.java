package com.example.Blog.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Tags")
public class Tag extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}