# CryptoTracker v2 - Progress Log

Roadmap kaynağı: `AI Destekli_plan1.pdf` (~150 günlük plan), kapsam sapmaları için bkz. `decisions.md`.
Bu dosya, aktif olarak neredeyiz sorusuna hızlı cevap vermek için oturum sonlarında güncellenir.

## Durum Özeti (2026-08-20)

**Faz:** Phase 1 — Monolitik Backend Geliştirme
**Sprint:** Sprint 1 — Temel Yapı & Veritabanı (Gün 4-15)
**Şu an:** Gün 13-15 (DTO, MapStruct & Validation) — bitmek üzere, commit edilmedi

### Tamamlanan
- [x] Gün 1-3 (Phase 0): requirements.md, decisions.md, ER diyagramı, repo + ilk commit
- [x] Gün 4-6: domain/application/infrastructure paket yapısı, BaseEntity
- [x] Gün 7-9: Tüm JPA entity'ler + Flyway V1__init.sql
- [x] Gün 10-12: Tüm Repository interface'leri (temel query'ler)
- [~] Gün 13-15: CreateUserRequest/LoginRequest/AddHoldingRequest DTO + validation yazıldı,
      UserMapper (MapStruct) yazıldı. **Henüz commit edilmedi** (pom.xml + application/ paketi untracked).

### Bilinen küçük eksikler (bloklayıcı değil, ileride tamamlanacak)
- TransactionRepository henüz `Pageable` kullanmıyor (plan Gün 10-12'de isteniyordu) — Sprint 2/3'te
  transaction listeleme endpoint'i yazılırken eklenebilir.
- Portföy toplam değerini hesaplayan özel `@Query` henüz yok — Sprint 3 (Service Layer, Gün 27-30)
  kapsamında zaten yazılacak, şimdiden eklemeye gerek yok.
- `UserResponse.java` içinde kullanılmayan/yanlış import var (`org.springframework.cglib.core.Local`,
  kullanılmayan `LocalDate`) — commit öncesi temizlenmeli.

### Sıradaki adım
Sprint 2: API & Güvenlik (Gün 16-26) → Gün 16-19 REST Controller'lar.

## Kapsam Notları (docs/decisions.md ile senkron)
- Tek portföy modeli (D001) — DB seviyesinde `Portfolio.user_id UNIQUE` ile zaten uygulanmış.
- Alarm sistemi kapsam dışı (D002).
- AI freemium ayrımı (D003) — FREE sabit izleme listesi 3 coin (BTC/ETH/SOL) olarak teyit edildi (2026-08-20).
- Rate limiting kapsam dışı (D005).
