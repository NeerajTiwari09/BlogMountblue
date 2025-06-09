package com.example.Blog.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Comments")
public class Comment extends AuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	private User commenter;
	private String name;
	@Column(name = "comment", columnDefinition = "text")
	private String comment;
	@Column(name = "post_id")
	private Integer postId;
}
