package com.example.honball;

import java.util.HashMap;
import java.util.Map;

public class MemberInfo {
    private String name;
    private String nickname;
    private String birth;
    private String phone;
    private String photoUrl;

    public MemberInfo(String name, String nickname, String phone, String birth, String photoUrl){
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.birth = birth;
        this.photoUrl = photoUrl;
    }

    public MemberInfo(String name, String nickname, String phone, String birth){
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.phone = phone;
    }

    public MemberInfo(String nickname, String photoUrl) {
        this.nickname = nickname;
        this.photoUrl = photoUrl;
    }

    public Map<String, Object> getMemberInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("nickname", nickname);
        docData.put("photoUrl", photoUrl);
        return docData;
    }

    public MemberInfo(){
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getNickname(){
        return this.nickname;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getBirth(){
        return this.birth;
    }
    public void setBirth(String birth){
        this.birth = birth;
    }

    public String getPhone(){
        return this.phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhotoUrl(){
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
