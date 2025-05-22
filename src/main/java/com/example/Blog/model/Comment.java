package com.example.Blog.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Comments")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	private User commenter;
	private String name;
	private String comment;
	@Column(name = "post_id")
	private Integer postId;
	@Column(name="created_at")
	@CreationTimestamp
	private Timestamp createdAt;
	@Column(name="updated_at")
	@UpdateTimestamp
	private Timestamp updatedAt;
}
