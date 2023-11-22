package com.example.servingwebcontent.database;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idown")
    private Long idOwn;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "time")
    private Long time;

    @Column(name = "idobject")
    private Long idObject;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "status")
    private String status;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public void setIdObject(Long idObject) {
        this.idObject = idObject;
    }

    public Long getIdObject() {
        return idObject;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}


