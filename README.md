# CryptoTracker v2 — AI-Powered Finance Platform

> **[English](#english) | [Türkçe](#türkçe)**

---

<a name="english"></a>
## English

I built CryptoTracker v2 as a personal learning project to grow from a junior to a mid-level Java developer. The platform lets users track their cryptocurrency portfolio in real time, get AI-powered analysis of their holdings, and manage a premium subscription. I designed and implemented the entire backend from scratch — from database schema to REST API, security, caching, messaging, and AI integration.

### Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Security | Spring Security + JWT |
| Database | PostgreSQL 16 + Flyway |
| Cache | Redis 7 |
| Messaging | RabbitMQ 3 |
| Real-Time | WebSocket + STOMP |
| AI | Groq API (openai/gpt-oss-20b) |
| External API | CoinGecko (BTC, ETH, SOL live prices) |
| Build | Maven |
| Docs | Swagger UI (springdoc-openapi) |

### Prerequisites

- Java 21
- Docker Desktop
- Maven (or use the included `mvnw` wrapper)

### Running the Application

#### Option 1 — Local (recommended for development)

**1. Start infrastructure services:**

```bash
docker compose up postgres redis rabbitmq -d
```

**2. Create `backend/src/main/resources/application-local.properties`:**

```properties
GROQ_API_KEY=your_groq_api_key_here
```

**3. Run the application:**

```bash
cd backend
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"
```

App runs at: `http://localhost:8081`

#### Option 2 — Full Docker

```bash
docker compose up --build
```

### API Documentation

Swagger UI: `http://localhost:8081/swagger-ui/index.html`

#### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Register a new user |
| POST | `/api/v1/auth/login` | Login and receive JWT token |

#### Portfolio
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/portfolio` | Get portfolio with live prices | Required |
| POST | `/api/v1/portfolio/holdings` | Add a holding | Required |
| DELETE | `/api/v1/portfolio/holdings/{coinId}` | Remove a holding | Required |

#### Transactions
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/transactions` | Add a transaction | Required |
| GET | `/api/v1/transactions` | Get transaction history | Required |

#### Subscription
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/subscription` | Get current subscription tier | Required |
| POST | `/api/v1/subscription/upgrade` | Upgrade to PREMIUM | Required |

#### AI Analysis (PREMIUM only)
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/ai/analyze` | Get AI portfolio analysis | PREMIUM |

#### Monitoring
| Method | Endpoint | Description |
|---|---|---|
| GET | `/actuator/health` | Health check |
| GET | `/actuator/metrics` | Application metrics |

### Authentication

All protected endpoints require a JWT token in the `Authorization` header:

```
Authorization: Bearer <your_token>
```

Get a token by calling `POST /api/v1/auth/login`. In Swagger UI, use the **Authorize** button (top right) to set the token globally.

### Subscription Tiers

| Feature | FREE | PREMIUM |
|---|---|---|
| Portfolio tracking | BTC, ETH, SOL | All coins |
| AI portfolio analysis | No | Yes |
| Real-time price updates | Yes | Yes |

### Project Structure

```
backend/src/main/java/com/cryptotracker/backend/
├── domain/model/          # JPA entities
├── application/
│   ├── dto/               # Request/Response DTOs
│   ├── mapper/            # MapStruct mappers
│   └── service/           # Business logic
├── infrastructure/
│   ├── persistence/       # JPA repositories
│   ├── security/          # JWT filter, SecurityConfig
│   ├── external/          # CoinGecko integration
│   ├── messaging/         # RabbitMQ config and consumer
│   └── scheduler/         # Scheduled price refresh
└── presentation/
    └── controller/        # REST controllers
```

### Real-Time Price Updates

The application broadcasts live cryptocurrency prices via WebSocket.

**Connect:** `ws://localhost:8081/ws`  
**Subscribe:** `/topic/prices`

Price updates are published every 5 minutes via a scheduled job → RabbitMQ → WebSocket.

---

<a name="türkçe"></a>
## Türkçe

CryptoTracker v2'yi junior'dan mid-level Java geliştiricisine geçiş sürecimde kişisel bir öğrenme projesi olarak geliştirdim. Platform, kullanıcıların kripto para portföylerini gerçek zamanlı takip etmelerini, yapay zeka destekli portföy analizi almalarını ve premium abonelik yönetimini sağlıyor. Veritabanı şemasından REST API'ye, güvenlik, cache, mesajlaşma ve AI entegrasyonuna kadar tüm backend'i sıfırdan tasarlayıp geliştirdim.

### Teknoloji Stack'i

| Katman | Teknoloji |
|---|---|
| Dil | Java 21 |
| Framework | Spring Boot 4.1 |
| Güvenlik | Spring Security + JWT |
| Veritabanı | PostgreSQL 16 + Flyway |
| Cache | Redis 7 |
| Mesajlaşma | RabbitMQ 3 |
| Gerçek Zamanlı | WebSocket + STOMP |
| Yapay Zeka | Groq API (openai/gpt-oss-20b) |
| Dış API | CoinGecko (BTC, ETH, SOL anlık fiyatlar) |
| Build | Maven |
| Dokümantasyon | Swagger UI (springdoc-openapi) |

### Gereksinimler

- Java 21
- Docker Desktop
- Maven (veya dahili `mvnw` wrapper kullanılabilir)

### Uygulamayı Çalıştırma

#### Seçenek 1 — Local (geliştirme için önerilen)

**1. Altyapı servislerini başlat:**

```bash
docker compose up postgres redis rabbitmq -d
```

**2. `backend/src/main/resources/application-local.properties` dosyasını oluştur:**

```properties
GROQ_API_KEY=groq_api_anahtarin
```

**3. Uygulamayı çalıştır:**

```bash
cd backend
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"
```

Uygulama adresi: `http://localhost:8081`

#### Seçenek 2 — Tam Docker

```bash
docker compose up --build
```

### API Dokümantasyonu

Swagger UI: `http://localhost:8081/swagger-ui/index.html`

#### Kimlik Doğrulama
| Metot | Endpoint | Açıklama |
|---|---|---|
| POST | `/api/v1/auth/register` | Yeni kullanıcı kaydı |
| POST | `/api/v1/auth/login` | Giriş yap ve JWT token al |

#### Portföy
| Metot | Endpoint | Açıklama | Auth |
|---|---|---|---|
| GET | `/api/v1/portfolio` | Anlık fiyatlı portföy getir | Gerekli |
| POST | `/api/v1/portfolio/holdings` | Varlık ekle | Gerekli |
| DELETE | `/api/v1/portfolio/holdings/{coinId}` | Varlık çıkar | Gerekli |

#### İşlemler
| Metot | Endpoint | Açıklama | Auth |
|---|---|---|---|
| POST | `/api/v1/transactions` | İşlem ekle | Gerekli |
| GET | `/api/v1/transactions` | İşlem geçmişi | Gerekli |

#### Abonelik
| Metot | Endpoint | Açıklama | Auth |
|---|---|---|---|
| GET | `/api/v1/subscription` | Mevcut abonelik bilgisi | Gerekli |
| POST | `/api/v1/subscription/upgrade` | PREMIUM'a yükselt | Gerekli |

#### AI Analizi (Yalnızca PREMIUM)
| Metot | Endpoint | Açıklama | Auth |
|---|---|---|---|
| GET | `/api/v1/ai/analyze` | AI portföy analizi | PREMIUM |

#### İzleme
| Metot | Endpoint | Açıklama |
|---|---|---|
| GET | `/actuator/health` | Sağlık kontrolü |
| GET | `/actuator/metrics` | Uygulama metrikleri |

### Kimlik Doğrulama

Korumalı tüm endpoint'ler `Authorization` header'ında JWT token gerektirir:

```
Authorization: Bearer <token>
```

Token almak için `POST /api/v1/auth/login` çağrısı yap. Swagger UI'da sağ üstteki **Authorize** butonunu kullanarak token'ı global olarak ayarlayabilirsin.

### Abonelik Seviyeleri

| Özellik | FREE | PREMIUM |
|---|---|---|
| Portföy takibi | BTC, ETH, SOL | Tüm coinler |
| AI portföy analizi | Hayır | Evet |
| Gerçek zamanlı fiyat | Evet | Evet |

### Proje Yapısı

```
backend/src/main/java/com/cryptotracker/backend/
├── domain/model/          # JPA entity siniflari
├── application/
│   ├── dto/               # Request/Response DTO'lari
│   ├── mapper/            # MapStruct mapper'lari
│   └── service/           # Is mantigi
├── infrastructure/
│   ├── persistence/       # JPA repository'leri
│   ├── security/          # JWT filter, SecurityConfig
│   ├── external/          # CoinGecko entegrasyonu
│   ├── messaging/         # RabbitMQ konfigurasyon ve consumer
│   └── scheduler/         # Zamanlanmis fiyat guncelleme
└── presentation/
    └── controller/        # REST controller'lar
```

### Gerçek Zamanlı Fiyat Güncellemeleri

Uygulama, WebSocket üzerinden anlık kripto fiyatlarını yayınlar.

**Bağlantı:** `ws://localhost:8081/ws`  
**Abone ol:** `/topic/prices`

Fiyat güncellemeleri her 5 dakikada bir zamanlanmış görev → RabbitMQ → WebSocket akışıyla iletilir.
