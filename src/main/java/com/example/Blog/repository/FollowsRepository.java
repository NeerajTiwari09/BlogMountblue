package com.example.Blog.repository;

import com.example.Blog.model.Follows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowsRepository extends JpaRepository<Follows, Long> {

    List<Follows> findByFollowerId(Integer followerId);

    List<Follows> findByFollowingId(Integer followingId);

    @Query("select f.followingId from Follows f where f.followerId= :followerId")
    List<Integer> findAllFollowingIdByFollowerId(Integer followerId);

    @Query("select f.followerId from Follows f where f.followingId= :followingId")
    List<Follows> findAllFollowerByFollowingId(Integer followingId);

    boolean existsByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    Optional<Follows> findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
}
