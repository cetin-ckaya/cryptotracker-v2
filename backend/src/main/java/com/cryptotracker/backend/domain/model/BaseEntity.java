package com.cryptotracker.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// @MappedSuperclass: Bu sınıfın KENDİSİ bir tablo DEĞİLDİR.
// Sadece "ortak alanları" (id, createdAt, updatedAt) diğer Entity'lere
// (User, Portfolio, Transaction vs.) miras bırakmak için var.
// Eğer @Entity kullansaydık, JPA bunun için de gereksiz bir "base_entity"
// tablosu oluşturmaya çalışırdı - biz bunu istemiyoruz.
@MappedSuperclass

// @EntityListeners: JPA'ya "bu sınıfın alanlarını AuditingEntityListener
// izlesin ve otomatik doldursun" diyoruz. Bu, aşağıdaki @CreatedDate ve
// @LastModifiedDate anotasyonlarının ÇALIŞMASI için ZORUNLUDUR.
// Bu olmadan o anotasyonlar hiçbir şey yapmaz.
@EntityListeners(AuditingEntityListener.class)

// @Getter, @Setter (Lombok): Normalde her alan için elle
// getId()/setId(), getCreatedAt()/setCreatedAt() yazman gerekirdi.
// Lombok, derleme sırasında bunları OTOMATİK üretir, sen elle yazmazsın.
// Kod daha kısa ve okunaklı olur. (pom.xml'de zaten Lombok dependency'si var)
@Getter
@Setter
public abstract class BaseEntity {

    // @Id: Bu alanın, veritabanı tablosundaki PRIMARY KEY olduğunu belirtir.
    @Id
    // @GeneratedValue: ID'nin nasıl üretileceğini belirtir.
    // IDENTITY stratejisi = veritabanının kendisi otomatik artan
    // (auto-increment) bir sayı üretsin demek. PostgreSQL'de bu,
    // "SERIAL" veya "BIGSERIAL" kolon tipine karşılık gelir.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @CreatedDate: Bu alan, kayıt İLK OLUŞTURULDUĞUNDA otomatik doldurulur.
    // Bir daha asla değişmez (update'lerde bu alana dokunulmaz).
    // "updatable = false" diyerek DB seviyesinde de bunun garantisini alırız,
    // yani birisi yanlışlıkla bu alanı güncellemeye çalışsa bile SQL UPDATE
    // sorgusuna bu kolon dahil edilmez.
    @CreatedDate
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    // @LastModifiedDate: Her kayıt GÜNCELLENDİĞİNDE (save/update işleminde)
    // bu alan otomatik olarak "şu anki zaman" ile güncellenir.
    // Elle "updatedAt = LocalDateTime.now()" yazmana gerek kalmaz.
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;




}
