package com.example.honball;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentInfo implements Serializable {
    private String comment;
    private String nickname, posts;
    private Date commentWriteDay;
    private String id;
    private String writer;
    private String photoUrl;

    public CommentInfo(String comment, String nickname, Date commentWriteDay, String id, String writer, String photoUrl){
        this.comment = comment;
        this.nickname = nickname;
        this.commentWriteDay = commentWriteDay;
        this.id = id;
        this.writer = writer;
        this.photoUrl = photoUrl;
    }

    public CommentInfo(String comment, Date commentWriteDay, String id, String writer){
        this.comment = comment;
        this.commentWriteDay = commentWriteDay;
        this.id = id;
        this.writer = writer;
    }

    public CommentInfo(String nickname, String photoUrl) {
        this.nickname = nickname;
        this.photoUrl = photoUrl;
    }

    public Map<String, Object> getCommentInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("comment", comment);
        docData.put("nickname", nickname);
        docData.put("commentWriteDay", commentWriteDay);
        docData.put("commentId", id);
        docData.put("writer", writer);
        docData.put("photoUrl", photoUrl);

        return docData;
    }


    public void setComment(String comment){
        this.comment = comment;
    }
    public String getComment(){
        return this.comment;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public String getNickname(){
        return this.nickname;
    }

    public Date getCommentWriteDay(){
        return this.commentWriteDay;
    }
    public void setCommentWriteDay(Date commentWriteDay){
        this.commentWriteDay = commentWriteDay;
    }

    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getWriter(){
        return this.writer;
    }
    public void setWriter(String writer){
        this.writer = writer;
    }

    public String getPhotoUrl(){
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
