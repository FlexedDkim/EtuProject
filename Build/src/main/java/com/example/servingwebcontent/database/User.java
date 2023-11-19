package com.example.servingwebcontent.database;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mail", unique = true)
    private String mail;

    @Column(name = "password")
    private String pass;

    @Column(name = "time")
    private Long time;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "salt")
    private String salt;

    @Column(name = "usertype")
    private int usertype;
    public void setId(Long id) {
        this.id = id;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public void setTime(Long time) {
        this.time = time;
    }
    public void setDelete(boolean deleted) {
        this.deleted = deleted;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
    public void setUsertype(String salt) {
        this.usertype = usertype;
    }
    public Long getId() {
        return id;
    }
    public String getMail() {
        return mail;
    }
    public Long getTime() {
        return time;
    }
    public String getSalt() {
        return salt;
    }
    public String getPass() {
        return pass;
    }
    public boolean getDeleted() {
        return deleted;
    }
    public int getUsertype() {return usertype;}
}
