package org.example.service;

import org.example.dao.CommentRepository;
import org.example.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class CommentService {
    //注入dao
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }
    public void updateComment(Comment comment) {
        commentRepository.save(comment);
    }
    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }
    public List<Comment> findCommentList() {
        return commentRepository.findAll();
    }
    public Comment findCommentById(String id) {
        return commentRepository.findById(id).get();
    }
    /**
     * 根据父id查询分页列表
     * @param parentid
     * @param page
     * @param size
     * @return
     */
    public Page<Comment> findCommentListPageByParentid(String parentid, int
            page , int size){
//为什么要page-1？因为参数传进去的是zero-based page index.，基于0开始的索引的页码
        return commentRepository.findByParentid(parentid,
                PageRequest.of(page-1,size));
    }
    public void updateCommentLikenum(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.inc("likenum");
        mongoTemplate.updateFirst(query,update,"comment");
    }
}
