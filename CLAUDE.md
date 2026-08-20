# CryptoTracker v2 — CLAUDE.md

Bu dosya Claude Code'un her oturumda projeyi sıfırdan anlaması için oluşturulmuştur.
Kapsam kararları için her zaman `docs/decisions.md`'yi, ilerleme için `docs/progress.md`'yi oku.

---

## Proje Özeti

**Ad:** CryptoTracker v2 — AI-Powered Finance Platform
**Hedef:** Junior → Mid-Level Java geliştiricisi yetiştirmek için referans proje
**Strateji:** Monolith-first → sonra Microservice dönüşümü (Phase 4)
**AI:** Önce OpenAI API (Phase 2), sonra kendi ML modeli (Phase 5)
**Toplam plan:** ~150 gün (`AI Destekli_plan1.pdf` — kullanıcının Downloads klasöründe)
**Stack:** Java 21 / Spring Boot 4.1 / Maven / PostgreSQL / Flyway / React 18+ (Phase 3)

---

## İşbirliği Kuralları — PM/Mentor Rolü

Claude bu projede **proje yöneticisi ve teknik mentor** rolündedir.
Kullanıcı uygulama kodunu **kendisi yazar** — öğrenmek için yapıyor.

### Claude'un yapacakları:
- Oturum başında `docs/progress.md` + git log'a bakarak "bugün ne yapılacak" sorusunu cevaplar
- Sprint görevlerini roadmap PDF'e ve `docs/decisions.md`'deki kapsam kararlarına göre adapte eder
- Kodu review eder, hataları gösterir, düzeltme yapılacaksa kullanıcıya bırakır
- Kapsam/mimari kararlar gerektirinde kullanıcıya sorar, cevabı `docs/decisions.md`'ye kaydeder
- Her sprint bitiminde `docs/progress.md`'yi günceller

### Claude'un yapmayacakları:
- Üretim kodunu (controller, service, entity vb.) doğrudan yazmak — önce açıklar, örnek verir, kalanı kullanıcı yazar
- Kapsam kararlarını kullanıcıya sormadan almak

---

## Görev Anlatım Formatı

Her yeni görevde şu sıra izlenir:

### 1. Bağlam — "Bu projede ne işe yarıyor?"
Görevin projemizin hangi katmanına, hangi kullanıcı hikayesine denk düştüğünü açıkla.

### 2. Kavram Açıklaması
İlk defa görülen bir kavramsa önce ne olduğunu anlat, ardından basit bir karşılaştırma yap:

```java
// Yanlış — entity direkt API'de kullanılırsa
{
  "passwordHash": "$2a$10$..."  // güvenlik açığı
}

// Doğru — DTO ile
{
  "email": "user@mail.com",
  "fullName": "Çetin"           // sadece gerekli alanlar
}
```

### 3. İlk Kodu Claude Verir (Yorum Satırlı)
Görevdeki ilk/örnek sınıfı Claude yazar — her anotasyonun, her satırın neden orada olduğunu
açıklayan yorum satırlarıyla:

```java
// @NotBlank: null, "" veya "   " olamaz — kullanıcı bu alanı göndermek ZORUNDA
// @Email: geçerli email formatı zorunlu (@ işareti, domain vs.)
@NotBlank(message = "Email cannot be blank")
@Email(message = "Invalid email format")
private String email;
```

### 4. Geri Kalanı Kullanıcı Yazar
Claude "şimdi sıra sende" diyerek benzer sınıfları kullanıcıya bırakır.

### 5. Review
Kullanıcı "yazdım" dediğinde Claude **dosyaları doğrudan okur** (kullanıcı kodu paylaşmak zorunda değil),
bulguları maddeler halinde yazar: ✅ doğru olanlar, ❌ eksik/hatalı olanlar.

---

## Mimari & Paket Yapısı

```
backend/src/main/java/com/cryptotracker/backend/
├── domain/
│   └── model/              ← JPA Entity sınıfları (DB tablolarını temsil eder)
│       ├── BaseEntity.java         (@MappedSuperclass — id, createdAt, updatedAt)
│       ├── User.java
│       ├── Coin.java
│       ├── Portfolio.java          (OneToOne → User)
│       ├── Holding.java            (ManyToOne → Portfolio, Coin)
│       ├── Transaction.java        (ManyToOne → Portfolio, Coin)
│       ├── Subscription.java       (OneToOne → User)
│       ├── PriceHistory.java
│       ├── PortfolioValueHistory.java
│       ├── AiAnalysisLog.java
│       ├── UserBehaviorEvent.java
│       ├── TransactionType.java    (enum: BUY, SELL, HOLD)
│       └── SubscriptionTier.java   (enum: FREE, PREMIUM)
├── application/
│   └── dto/                ← Veri transfer nesneleri (API katmanı)
│       ├── request/        ← Kullanıcıdan gelen istekler (validation anotasyonları burada)
│       └── response/       ← Kullanıcıya dönen yanıtlar (validation anotasyonu yok)
│   └── mapper/             ← MapStruct mapper interface'leri (Entity ↔ DTO)
├── infrastructure/
│   └── persistence/        ← JpaRepository interface'leri
└── presentation/           ← Controller'lar (Sprint 2'de oluşturulacak)
```

**Katman Kuralı:** Bağımlılık yönü her zaman dıştan içe akar:
`presentation → application → domain` — domain katmanı hiçbir dış katmanı import etmez.

---

## Entity Standartları

- Tüm entity'ler `BaseEntity`'den extend eder (`id`, `createdAt`, `updatedAt` ortak)
- `@Entity` + `@Table(name = "tablo_adi")` her entity'de zorunlu
- İlişkiler:
  - Bire-bir: `@OneToOne` + `@JoinColumn(unique = true, nullable = false)`
  - Çoka-bir: `@ManyToOne` + `@JoinColumn(nullable = false)`
- Enum alanlar: `@Enumerated(EnumType.STRING)` — integer değil string olarak sakla
- Lombok: `@Getter` `@Setter` (entity başına) — getter/setter elle yazılmaz
- `BaseEntity`'de yanlış import var (`org.springframework.cglib.core.Local`) — Sprint 2 öncesi temizlenecek

---

## DTO Standartları

**Request DTO:**
- Validation anotasyonları burada kullanılır: `@NotBlank`, `@NotNull`, `@Email`, `@Size`
- `String` alanlar için `@NotBlank` (null + boş + sadece boşluk yakalar)
- `Long`, `BigDecimal` gibi non-String alanlar için `@NotNull`
- Lombok: `@Getter` `@Setter`

**Response DTO:**
- Validation anotasyonu **kullanılmaz** — bu veriyi biz üretiyoruz, kullanıcıdan gelmiyor
- `passwordHash` gibi hassas alanlar **asla** response DTO'ya eklenmez
- Lombok: `@Getter` `@Setter`

---

## MapStruct Standartları

- Tüm mapper'lar `application/dto/` altında, interface olarak tanımlanır
- `@Mapper(componentModel = "spring")` — Spring bean olarak kayıt için zorunlu
- Alan isimleri eşleşiyorsa `@Mapping` anotasyonu gerekmez, MapStruct otomatik yapar
- İç içe nesne alanları için `@Mapping(source = "coin.symbol", target = "coinSymbol")`
- Aynı mapper içinde birden fazla metot tanımlanabilir; MapStruct `List<>` dönüşümlerini otomatik halleder
- `pom.xml`'de Lombok + MapStruct'ın birlikte çalışması için `maven-compiler-plugin`'e her ikisi de
  `annotationProcessorPaths` içinde tanımlanmalı (Lombok önce gelir)

---

## Tamamlanmış Sprint'ler (Referans)

### Sprint 1 — Temel Yapı & Veritabanı (Gün 4-15) ✅

**Gün 4-6 — Clean Architecture & Paket Yapısı**
- `domain`, `application`, `infrastructure`, `presentation` paketleri oluşturuldu
- `BaseEntity` (id, createdAt, updatedAt — JPA Auditing ile)

**Gün 7-9 — JPA Entity & Flyway**
- Tüm entity'ler kodlandı (User, Coin, Portfolio, Holding, Transaction, Subscription, PriceHistory,
  PortfolioValueHistory, AiAnalysisLog, UserBehaviorEvent)
- `V1__init.sql` Flyway migration dosyası oluşturuldu

**Gün 10-12 — Repository & Data Access**
- Tüm entity'ler için `JpaRepository` interface'leri oluşturuldu
- `PortfolioRepository.findByUserId`, `TransactionRepository.findByPortfolioIdOrderByTransactionDateDesc`
  gibi özel query metodları eklendi
- `Pageable` desteği: transaction listesi endpoint'i yazılırken eklenecek (Sprint 2/3)

**Gün 13-15 — DTO, MapStruct & Validation**
- Request DTO'lar: `CreateUserRequest`, `LoginRequest`, `AddHoldingRequest`, `CreateTransactionRequest`
- Response DTO'lar: `UserResponse`, `HoldingResponse`, `PortfolioResponse`, `TransactionResponse`
- Mapper'lar: `UserMapper`, `PortfolioMapper` (Holding + Transaction + Portfolio mapping)
- `mvn compile` — hatasız

---

## Sıradaki Sprint

**Sprint 2 — API & Güvenlik (Gün 16-26)**

- **Gün 16-19:** REST Controller'lar (`UserController`, `PortfolioController`) — boş servis çağrılarıyla,
  Postman'de test
- **Gün 20-23:** Spring Security & JWT (`SecurityConfig`, `JwtTokenProvider`, BCrypt)
- **Gün 24-26:** Global Exception Handling (`@ControllerAdvice`, `BusinessException`, `NotFoundException`)

---

## Kapsam Kararları (Özet — Tam liste: `docs/decisions.md`)

| Karar | Açıklama |
|---|---|
| D001 | Tek portföy modeli — `Portfolio.user_id UNIQUE` ile DB'de garantili |
| D002 | Alarm sistemi kapsam dışı (v2.1'e ertelendi) |
| D003 | FREE: BTC/ETH/SOL için genel AL/SAT/TUT — PREMIUM: portföydeki her coin kişiselleştirilmiş |
| D004 | Tüm AI çıktılarına "Yatırım tavsiyesi değildir" uyarısı |
| D005 | Rate limiting yok — premium gating sadece AI Analiz sayfasına erişim üzerinden |
| D006 | Build tool: Maven |
| D007 | Monorepo — backend/, frontend/ aynı repoda |

---

## Git Commit Standartları

Conventional Commits formatı kullanılır:

```
feat: add request/response DTOs with validation and MapStruct mapper
fix: remove unused imports from UserResponse and BaseEntity
docs: add architecture decision for rate limiting removal
chore: add MapStruct dependency and compiler plugin configuration
refactor: move transaction mapping into PortfolioMapper
test: add unit tests for PortfolioService profit/loss calculation
```

---

## Önemli Dosyalar

| Dosya | Açıklama |
|---|---|
| `docs/decisions.md` | Mimari ve kapsam kararları (D001+) — kural değişirse buraya yaz |
| `docs/requirements.md` | Epic'ler ve user story'ler |
| `docs/progress.md` | Sprint/gün bazlı ilerleme takibi — oturum sonlarında güncelle |
| `docs/design/dashboard-reference.png` | Hedef dashboard görsel tasarımı (Phase 3 için referans) |
| `docs/design/er-diagram.png` | Veritabanı ER diyagramı |
| `AI Destekli_plan1.pdf` | Ana yol haritası (kullanıcının Downloads klasöründe) |
