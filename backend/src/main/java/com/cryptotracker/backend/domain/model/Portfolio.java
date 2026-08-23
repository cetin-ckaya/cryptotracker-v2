package com.cryptotracker.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "portfolios")
@Getter
@Setter
public class Portfolio extends BaseEntity{

    //@OneToOne → "Bu Portfolio, tam olarak 1 User'a ait" demek
    //@JoinColumn(name = "user_id") → "Bu ilişkiyi veritabanında user_id isimli bir foreign key kolonuyla kur" demek
    //unique = true → Aynı User'a ait 2. bir Portfolio oluşturulamaz (D001 kararımızın DB seviyesinde garantisi)
    @OneToOne
    @JoinColumn(name = "user_id",unique = true,nullable = false)
    private User user;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER)
    private List<Holding> holdings;
}
