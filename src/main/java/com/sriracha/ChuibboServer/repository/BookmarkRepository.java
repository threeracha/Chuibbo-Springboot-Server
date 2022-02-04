package com.sriracha.ChuibboServer.repository;

import com.sriracha.ChuibboServer.model.entity.Bookmark;
import com.sriracha.ChuibboServer.model.entity.JobPost;
import com.sriracha.ChuibboServer.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByUser(User user);

    Optional<Bookmark> findByUserAndJobPost(User user, JobPost jobPost);

    @Transactional
    void deleteByUserAndJobPost(User user, JobPost jobPost);
}
