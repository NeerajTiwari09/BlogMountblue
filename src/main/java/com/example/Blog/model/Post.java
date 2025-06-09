package com.example.Blog.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import org.hibernate.annotations.Cache;

@Data
@Entity
@Table(name = "Posts")
@ToString
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "posts")
public class Post extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String excerpt;
    @Lob
    @Column(name = "content", columnDefinition = "text")
    private String content;
    @ManyToOne
    private User author;

    @Column(name = "published_at")
    private String publishedAt;
    @Column(name = "is_published")
    private boolean isPublished;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "post_tags",
            joinColumns = {@JoinColumn(name = "postId")},
            inverseJoinColumns = {@JoinColumn(name = "tagId")})
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    public Integer getLikesCount() {
        return this.likes.size();
    }
}
