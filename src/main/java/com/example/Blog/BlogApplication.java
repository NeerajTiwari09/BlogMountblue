package com.example.Blog;

import com.example.Blog.model.Post;
import com.example.Blog.model.PostTag;
import com.example.Blog.model.Role;
import com.example.Blog.model.Tag;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class BlogApplication {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PostRepository postRepository;

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
		System.out.println("Application has started...");
	}

	@PostConstruct
	public void init(){
		List<Role> roles = roleRepository.findAll();
		if(roles.isEmpty()){
			Role role = new Role();
			role.setName("AUTHOR");
			roleRepository.save(role);
		}
		Post post = new Post();
		post.setTitle("1233");
		post.setAuthor("1233");
		post.setContent("1233");
		Tag tag = new Tag();
		tag.setName("abcd");
//		tag = tagRepository.save(tag);
		Set<PostTag> tags = new HashSet<>();
		PostTag postTag = new PostTag();
//		postTag.setTag(tag);
//		postTag.setPost(post);
//		tags.add(postTag);
//		post.setPostTags(tags);
//		postRepository.save(post);
	}
}
