package com.tissue.core.profile;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User {

    public static enum Gender {
        Male, Female
    }

    //general
    private String id;

    private String displayName;
    private String email;
    private String resume;

    //account
    private String username;
    private String password;

    //profile
    private String name;
    private String aboutMe;
    private String photo;
    private Gender gender;
    private Date birthday;

    //internal
    private Date createTime;
    private Date updateTime;
    private Boolean verified = false;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getResume() {
        return resume;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean isVerified() {
        return verified;
    }

    public Boolean isSame(String userId) {
        return id.equals(userId);   
    }
}
