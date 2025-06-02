package com.example.Blog.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "follows")
@Getter
@Setter
public class Follows extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer followerId;

    private Integer followingId;

}
