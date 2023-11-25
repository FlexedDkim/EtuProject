package com.example.servingwebcontent.database;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idown")
    private Long idOwn;

    @Column(name = "idcard")
    private Long idCard;

    @Column(name = "time")
    private Long time;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "body")
    private String body;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setIdOwn(Long idOwn) {
        this.idOwn = idOwn;
    }

    public Long getIdOwn() {
        return idOwn;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setIdCard(Long idCard) {
        this.idCard = idCard;
    }

    public Long getIdCard() {
        return idCard;
    }

}