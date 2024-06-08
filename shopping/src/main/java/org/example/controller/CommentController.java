package org.example.controller;
import org.example.entity.Comment;
import org.example.service.CommentService;
import org.example.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.LocalDateTime;

@RestController
public class CommentController {
    //注入Service
    @Autowired
    private CommentService commentService;

    @RequestMapping("/listall")
    public List<Comment> testFindAll(){
        List<Comment> list = commentService.findCommentList();
        return list;
    }
    @RequestMapping("/new")
    public void testSaveComment(String articleid,String content){
        Comment comment=new Comment();
        comment.setArticleid(articleid);
        comment.setContent(content);
        comment.setCreatedatetime(LocalDateTime.now());
        comment.setUserid("1003");
        comment.setNickname("新入数据");
        comment.setParentid("1003");
        comment.setState("1");
        comment.setLikenum(0);
        comment.setReplynum(0);
        commentService.saveComment(comment);
        System.out.println("写入数据成功");
    }
    @RequestMapping("/page")
    public String testFindCommentListPageByParentid(@RequestParam("parentid") String parentid, @RequestParam("page") int
            page, @RequestParam("size") int size ){
        Page<Comment> pageResponse =
                commentService.findCommentListPageByParentid(parentid,page,size);
        String result="----总记录数："+pageResponse.getTotalElements()+"----当前页数据："
                +pageResponse.getContent();
        return result;
    }
    @RequestMapping("/updatelikenum")
    public Result testUpdateCommentLikenum(){
//对1号文档的点赞数+1
        commentService.updateCommentLikenum("2");
        return Result.ok();
    }
}
