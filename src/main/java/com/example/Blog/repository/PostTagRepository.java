//package com.example.Blog.repository;
//
//import com.example.Blog.model.PostTag;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.Timestamp;
//import java.util.List;
//import java.util.Set;
//
//public interface PostTagRepository extends JpaRepository<PostTag, Integer> {
//
//    @Query("SELECT p.tagId FROM PostTag p where p.postId = :postId")
//    List<Integer> findAllByPostId(@Param("postId") Integer postId);
//
//    @Query("SELECT p.postId FROM PostTag p where p.tagId in ?1")
//    Set<Integer> findPostIdByTagId(@Param("tagIds") List<Integer> tagIds);
//
//    @Query(value = "select post_tags.post_id from post_tags inner join tags on" +
//            " tags.id = post_tags.tag_id where tags.name like :searchString",
//            nativeQuery = true)
//    Set<Integer> findAllPostIdByTagName(@Param("searchString") String searchString);
//
//    @Query("SELECT p from PostTag p WHERE p.postId = ?1")
//    List<PostTag> findPostTagByPostId(Integer id);
//
//    @Transactional
//    void deleteAllByPostId(Integer id);
//}
