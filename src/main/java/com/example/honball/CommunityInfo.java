package com.example.honball;

import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommunityInfo implements Serializable {

    private String title;
    private ArrayList<String> contents;
    private ArrayList<String> formats;
    private String writer;
    private Date writeDay;
    private String id;
    private String nickname;
    private String photoUrl;

    public CommunityInfo(String title, ArrayList<String> contents, ArrayList<String> formats, String writer, Date writeDay, String id){
        this.title = title;
        this.contents = contents;
        this.formats = formats;
        this.writer = writer;
        this.writeDay = writeDay;
        this.id = id;
    }

    public CommunityInfo(String title, ArrayList<String> contents, ArrayList<String> formats, String writer, Date writeDay, String id, String nickname, String photoUrl){
        this.title = title;
        this.contents = contents;
        this.formats = formats;
        this.writer = writer;
        this.writeDay = writeDay;
        this.id = id;
        this.nickname = nickname;
        this.photoUrl = photoUrl;
    }

    public CommunityInfo(String title, ArrayList<String> contents, ArrayList<String> formats, String writer, Date writeDay){
        this.title = title;
        this.contents = contents;
        this.formats = formats;
        this.writer = writer;
        this.writeDay = writeDay;
    }

    public CommunityInfo( String nickname, String photoUrl){
        this.nickname = nickname;
        this.photoUrl = photoUrl;
    }

    public Map<String, Object> getCommunityInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("title", title);
        docData.put("contents", contents);
        docData.put("formats", formats);
        docData.put("writer", writer);
        docData.put("writeDay", writeDay);
        docData.put("nickname", nickname);
        docData.put("photoUrl", photoUrl);
        return docData;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public ArrayList<String> getContents(){
        return this.contents;
    }
    public void setContents(ArrayList<String> contents){
        this.contents = contents;
    }

    public ArrayList<String> getFormats(){
        return this.formats;
    }
    public void setFormats(ArrayList<String> formats){
        this.formats = formats;
    }

    public String getWriter(){
        return this.writer;
    }
    public void setWriter(String writer){
        this.writer = writer;
    }

    public Date getWriteDay(){
        return this.writeDay;
    }
    public void setWriteDay(Date writeDay){
        this.writeDay = writeDay;
    }

    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getNickname(){
        return this.nickname;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getPhotoUrl(){
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
