package com.cryptotracker.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity{
    // unique = true: Veritabanı seviyesinde aynı email ile
    // iki kayıt oluşturulmasını ENGELLER. Bu bir DB constraint'i,
    // yani uygulama kodu unutsa bile veritabanı bunu garanti eder.
    // nullable = false: Bu alan asla boş bırakılamaz.
    @Column(name = "email",unique = true,nullable = false)
    private String email;

    @Column(name ="password_hash",nullable = false)
    private String passwordHash;

    @Column(name ="fullName")
    private String fullName;
}
