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
**Port:** Uygulama `8081`'de çalışıyor (8080 başka bir process tarafından kullanılıyor)

---

## İşbirliği Kuralları — PM/Mentor Rolü

Claude bu projede **proje yöneticisi ve teknik mentor** rolündedir.
Kullanıcı uygulama kodunu **kendisi yazar** — öğrenmek için yapıyor.

### Claude'un yapacakları:
- Oturum başında `docs/progress.md` + git log'a bakarak "bugün ne yapılacak" sorusunu cevaplar
- Sprint görevlerini `AI Destekli_plan1.pdf` roadmap'e ve `docs/decisions.md`'deki kapsam kararlarına göre adapte eder
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
  "passwordHash": "$2a$10$..."  // guvenlik acigi
}

// Dogru — DTO ile
{
  "email": "user@mail.com",
  "fullName": "Cetin"           // sadece gerekli alanlar
}
```

### 3. İlk Kodu Claude Verir (Yorum Satırlı)
Görevdeki ilk/örnek sınıfı Claude yazar — her anotasyonun, her satırın neden orada olduğunu
açıklayan yorum satırlarıyla. Tüm kodlarda yorum satırları zorunludur — ne işe yaradığını
bu projede açıklamalı.

### 4. Geri Kalanı Kullanıcı Yazar
Claude sadece ne yapılacağını ayrıntılı açıklar — ipucu veya kod vermez.
Kullanıcı "yardım et" derse o zaman ipucu/kod verilir.

### 5. Review
Kullanıcı "yazdım" dediğinde Claude **dosyaları doğrudan okur** (kullanıcı kodu paylaşmak zorunda değil),
bulguları maddeler halinde yazar: doğru olanlar, eksik/hatalı olanlar.

---

## Mimari & Paket Yapısı

```
backend/src/main/java/com/cryptotracker/backend/
├── domain/
│   └── model/              <- JPA Entity siniflari (DB tablolarini temsil eder)
│       ├── BaseEntity.java         (@MappedSuperclass — id, createdAt, updatedAt)
│       ├── User.java
│       ├── Coin.java
│       ├── Portfolio.java          (OneToOne -> User, OneToMany -> Holdings EAGER)
│       ├── Holding.java            (ManyToOne -> Portfolio, Coin)
│       ├── Transaction.java        (ManyToOne -> Portfolio, Coin)
│       ├── Subscription.java       (OneToOne -> User)
│       ├── PriceHistory.java
│       ├── PortfolioValueHistory.java
│       ├── AiAnalysisLog.java
│       ├── UserBehaviorEvent.java
│       ├── TransactionType.java    (enum: BUY, SELL, HOLD)
│       └── SubscriptionTier.java   (enum: FREE, PREMIUM)
├── application/
│   ├── dto/                <- Veri transfer nesneleri (API katmani)
│   │   ├── request/        <- Kullanicidan gelen istekler (validation anotasyonlari burada)
│   │   └── response/       <- Kullaniciya donen yanitlar (validation anotasyonu yok)
│   ├── mapper/             <- MapStruct mapper interface'leri (Entity <-> DTO)
│   │   └── PortfolioMapper.java  (Holding, Transaction, Portfolio mapping)
│   ├── service/            <- Is mantiginin yasadigi yer
│   │   ├── UserService.java       (register, login — BCrypt + JWT)
│   │   ├── PortfolioService.java  (getPortfolio, addHolding, removeHolding + CoinGecko)
│   │   └── TransactionService.java (addTransaction, getTransaction)
│   └── exception/
│       ├── BusinessException.java   (409 Conflict)
│       └── NotFoundException.java   (404 Not Found)
├── infrastructure/
│   ├── persistence/        <- JpaRepository interface'leri
│   ├── security/           <- JWT filter, SecurityConfig
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── SecurityConfig.java
│   └── external/
│       └── CoinGeckoService.java   (RestClient ile canli fiyat cekimi)
└── presentation/
    ├── controller/
    │   ├── AuthController.java        (POST /api/v1/auth/register, /login)
    │   ├── PortfolioController.java   (GET/POST/DELETE /api/v1/portfolio/*)
    │   └── TransactionController.java (GET/POST /api/v1/transactions)
    └── exception/
        ├── GlobalExceptionHandler.java  (@RestControllerAdvice)
        └── ErrorResponse.java
```

**Katman Kuralı:** Bagimlilik yonu her zaman distan ice akar:
`presentation → application → domain` — domain katmani hicbir dis katmani import etmez.

---

## Entity Standartları

- Tüm entity'ler `BaseEntity`'den extend eder (`id`, `createdAt`, `updatedAt` ortak)
- `@Entity` + `@Table(name = "tablo_adi")` her entity'de zorunlu
- İlişkiler:
  - Bire-bir: `@OneToOne` + `@JoinColumn(unique = true, nullable = false)`
  - Çoka-bir: `@ManyToOne` + `@JoinColumn(nullable = false)`
- Enum alanlar: `@Enumerated(EnumType.STRING)` — integer değil string olarak sakla
- Lombok: `@Getter` `@Setter` (entity başına) — getter/setter elle yazılmaz
- `Portfolio.holdings` alanı `FetchType.EAGER` kullanır — lazy loading N+1 sorununu önler
- `PortfolioRepository.findByUserIdWithHoldings` — `LEFT JOIN FETCH` ile holdings'i tek sorguda çeker

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

## Güvenlik — Önemli Kurallar

- `application.properties` ve `pom.xml` dosyalarında **Turkce karakter kullanma** — Maven resource
  filtering sirasinda `MalformedInputException` hatasina neden olur
- JWT token `Authorization: Bearer <token>` header'i ile gönderilir
- `SecurityConfig`: `/api/v1/auth/**` herkese acik, diger her sey authenticated
- Authenticated userId çekme: `SecurityContextHolder` → email → `UserRepository.findByEmail`
- `PortfolioController` ve `TransactionController`'da `getAuthenticatedUserId()` private metodu kullanılır

---

## Tamamlanmış Sprint'ler (Referans)

### Sprint 1 — Temel Yapı & Veritabanı (Gün 4-15) ✅

- `domain`, `application`, `infrastructure`, `presentation` paket yapısı
- `BaseEntity` (JPA Auditing ile id, createdAt, updatedAt)
- Tüm JPA entity'ler + `V1__init.sql` Flyway migration
- Tüm Repository interface'leri + özel query metodları
- Request/Response DTO'lar, MapStruct mapper'lar, Bean Validation

### Sprint 2 — API & Güvenlik (Gün 16-26) ✅

- `AuthController` (register/login), `PortfolioController` (stub) — Postman testleri geçti
- Spring Security + JWT: `JwtTokenProvider`, `JwtAuthenticationFilter`, `SecurityConfig`, `UserService`
- Global Exception Handling: `GlobalExceptionHandler`, `BusinessException`, `NotFoundException`, `ErrorResponse`
- 400 / 404 / 409 / 500 hata senaryoları test edildi

### Sprint 3 — Service Layer (Gün 27-35) ✅

- `PortfolioService`: getPortfolio (CoinGecko fiyat entegrasyonu + kar/zarar hesabı), addHolding, removeHolding
- `CoinGeckoService`: `RestClient` ile BTC/ETH/SOL anlık fiyat çekimi
- `PortfolioRepository.findByUserIdWithHoldings`: `LEFT JOIN FETCH` ile boş liste sorunu çözüldü
- `TransactionService`: addTransaction, getTransaction
- `TransactionController`: POST/GET `/api/v1/transactions`
- Tüm endpoint'ler Postman'de test edildi

### Sprint 4 — Cache, Zamanlama & Real-Time (Gün 34-42) ✅

- Redis Cache: `@Cacheable` CoinGecko fiyatlari, `@CacheEvict`, Docker Redis
- `MarketScheduler`: 5 dk'da bir fiyat guncelleme, saatlik portfoy gecmisi kaydi
- `WebSocketConfig`: STOMP, `/topic/prices` kanali
- `SimpleClientHttpRequestFactory`: Windows loopback sorunu icin gerekli — `CoinGeckoService` ve `AiAnalysisService`'te kullanilir

### Sprint 5 — Swagger & Actuator (Gün 45-47) ✅

- `springdoc-openapi-starter-webmvc-ui` bagimliligı eklendi
- Actuator `/health` ve `/metrics` endpoint'leri acildi
- SecurityConfig'e `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/**` permit eklendi

### Sprint 6 — Tests & Docker (Gün 48-55) ✅

- `PortfolioServiceTest`: JUnit 5 + Mockito unit testleri
- `mock-maker-subclass`: Java 21 Mockito uyumlulugu icin `src/test/resources/mockito-extensions/` altinda
- Multi-stage `Dockerfile` (eclipse-temurin:21-jdk builder → 21-jre runtime)
- `docker-compose.yml`: app / postgres:16-alpine / redis:7-alpine servisleri

### Sprint 7 — Premium & AI (Gün 56-63) ✅

- `JwtTokenProvider`: `generateToken(email, role)` — token'a role claim eklendi
- `JwtAuthenticationFilter`: role'dan `SimpleGrantedAuthority` — null guard ile (eski token uyumluluğu)
- `SecurityConfig`: `@EnableMethodSecurity` eklendi
- `UserService`: login'de `subscription.tier` okunarak token'a `ROLE_FREE` / `ROLE_PREMIUM` yazılıyor
- `SubscriptionService` + `SubscriptionController`: GET `/api/v1/subscription`, POST `/api/v1/subscription/upgrade`
- `AiAnalysisService`: Groq API (`openai/gpt-oss-20b`) ile portfoy analizi, `@Value("${groq.api.key}")`
- `AiController`: `@PreAuthorize("hasRole('PREMIUM')")` ile korunan GET `/api/v1/ai/analyze`
- `OpenApiConfig`: Swagger UI'da JWT Authorize butonu icin `@SecurityScheme` + `SecurityRequirement`
- `application-local.properties`: Groq API key burada saklanir (gitignore'da) — calistirmak icin `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`

---

## Sıradaki Sprint

**Sprint 8 — Kullanici Davranis Takibi (Gün 67-69)**

- Gün 64-66: Raporlama Sistemi — D008 karari geregi ASKIYA ALINDI
- Gün 67-69: `@TrackerBehavior` AOP anotasyonu, `user_behavior_events` tablosuna kayit

---

## Kapsam Kararları (Özet — Tam liste: `docs/decisions.md`)

| Karar | Açıklama |
|---|---|
| D001 | Tek portföy modeli — `Portfolio.user_id UNIQUE` ile DB'de garantili |
| D002 | Alarm sistemi kapsam dışı (v2.1'e ertelendi) — plan'da Gün 43-44'te var, atlanacak |
| D003 | FREE: BTC/ETH/SOL için genel AL/SAT/TUT — PREMIUM: portföydeki her coin kişiselleştirilmiş |
| D004 | Tüm AI çıktılarına "Yatırım tavsiyesi değildir" uyarısı |
| D005 | Rate limiting yok — premium gating sadece AI Analiz sayfasına erişim üzerinden |
| D006 | Build tool: Maven |
| D007 | Monorepo — backend/, frontend/ aynı repoda |
| D008 | Raporlama sistemi (PDF) askıya alındı — proje bitince değerlendirilecek |

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
