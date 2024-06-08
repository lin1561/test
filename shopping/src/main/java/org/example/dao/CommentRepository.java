package org.example.dao;

import org.example.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
//评论的持久层接口
public interface CommentRepository extends MongoRepository<Comment, String>{
    Page<Comment> findByParentid(String parentid, Pageable pageable);
}
