# CryptoTracker v2 - Progress Log

Roadmap kaynağı: `AI Destekli_plan1.pdf` (~150 günlük plan), kapsam sapmaları için bkz. `decisions.md`.
Bu dosya, aktif olarak neredeyiz sorusuna hızlı cevap vermek için oturum sonlarında güncellenir.

## Durum Özeti (2026-08-26)

**Faz:** Phase 1 — Monolitik Backend Geliştirme
**Sprint:** Sprint 7 — Premium & OpenAI (Gün 56-63) ✅ TAMAMLANDI
**Şu an:** Sprint 8 başlangıcı

### Tamamlanan
- [x] Gün 1-3 (Phase 0): requirements.md, decisions.md, ER diyagramı, repo + ilk commit
- [x] Gün 4-6: domain/application/infrastructure paket yapısı, BaseEntity
- [x] Gün 7-9: Tüm JPA entity'ler + Flyway V1__init.sql
- [x] Gün 10-12: Tüm Repository interface'leri (temel query'ler)
- [x] Gün 13-15: DTO'lar (Request/Response), MapStruct mapper'lar, Bean Validation
- [x] Gün 16-19: REST Controller'lar (AuthController, PortfolioController) + stub service'ler — Postman testleri geçti
- [x] Gün 20-23: Spring Security + JWT (JwtTokenProvider, JwtAuthenticationFilter, SecurityConfig, UserService.login/register) — tüm auth testleri geçti
- [x] Gün 24-26: Global Exception Handling (GlobalExceptionHandler, BusinessException, NotFoundException, ErrorResponse) — 400/404/409/500 testleri geçti

- [x] Gün 27-35: PortfolioService gerçek implementasyonu (holding ekle/çıkar, kar/zarar hesabı) + CoinGecko fiyat entegrasyonu
- [x] Gün 35 (ek): TransactionService + TransactionController — POST/GET /api/v1/transactions, Postman testleri geçti
- [x] Gün 34-36: Redis Cache — Docker Redis, @Cacheable CoinGecko fiyatlari, @CacheEvict — cache testi gecti
- [x] Gün 37-39: Scheduled Tasks — @EnableScheduling, MarketScheduler (5 dk fiyat), saatlik portfoy gecmisi
- [x] Gün 40-42: WebSocket & Real-Time — WebSocketConfig, /topic/prices kanali, SecurityConfig /ws/** acildi
- [ ] Gün 43-44: Alarm Sistemi — D002 karari geregi ATLANDI
- [x] Gün 45-47: Swagger UI (springdoc-openapi) + Actuator /health /metrics — Swagger'da test edildi
- [x] Gün 48-55: Docker multi-stage Dockerfile + docker-compose (app/postgres/redis) — build ve calistirma testi gecti
- [x] Gün 56-59: Subscription sistemi — SubscriptionService, SubscriptionController, JWT role claim, @EnableMethodSecurity
- [x] Gün 60-63: Groq AI entegrasyonu — AiAnalysisService (openai/gpt-oss-20b), AiController (@PreAuthorize PREMIUM), OpenApiConfig (Swagger JWT Authorize butonu) — Swagger'da 200 OK test edildi

### Sıradaki adım
Sprint 8: React Frontend (Phase 3 başlangıcı)

## Kapsam Notları (docs/decisions.md ile senkron)
- Tek portföy modeli (D001) — DB seviyesinde `Portfolio.user_id UNIQUE` ile zaten uygulanmış.
- Alarm sistemi kapsam dışı (D002).
- AI freemium ayrımı (D003) — FREE sabit izleme listesi 3 coin (BTC/ETH/SOL) olarak teyit edildi (2026-08-20).
- Rate limiting kapsam dışı (D005).
