# Architecture & Scope Decisions Log

Bu dosya, proje boyunca alınan önemli mimari ve kapsam kararlarını
ve bu kararların gerekçelerini kaydeder.

---

## D001 - Tek Portföy Modeli
**Tarih:** Proje başlangıcı
**Karar:** Kullanıcı başına çoklu portföy yerine tek portföy kullanılacak.
**Gerekçe:** MVP kapsamını sade tutmak, asıl değeri coin takibi ve
kar/zarar analizinde toplamak. Çoklu portföy yönetimi gereksiz karmaşıklık katar.

---

## D002 - Alarm Sistemi Kapsam Dışı
**Tarih:** Proje başlangıcı
**Karar:** Fiyat alarmı özelliği (email bildirimi) v2.0 kapsamından çıkarıldı.
**Gerekçe:** Proje zaten çok geniş bir kapsama sahip (150 gün). Alarm sistemi
çekirdek değer önerisine (portföy takibi + AI analiz) doğrudan katkı sağlamıyor.
v2.1'de değerlendirilebilir.

---

## D003 - AI Analiz Freemium Ayrımı
**Tarih:** Proje başlangıcı
**Karar:**
- FREE kullanıcı: Sabit izleme listesindeki (BTC, ETH, SOL) coinler için
  genel, herkese aynı AL/SAT/TUT sinyali görür.
- PREMIUM kullanıcı: Kendi portföyündeki her coin için kişiselleştirilmiş
  öneri + yön beklentisi (yükseliş/düşüş) görür.
  **Gerekçe:** Hem teknik olarak net bir ayrım sağlıyor (2 farklı prompt/endpoint)
  hem de gerçekçi bir "upgrade" motivasyonu yaratıyor (TradingView Free/Pro
  modeline benzer).

---

## D004 - AI Çıktılarında Yasal Uyarı
**Tarih:** Proje başlangıcı
**Karar:** Tüm AI analiz çıktılarının yanında "Yatırım tavsiyesi değildir"
uyarısı gösterilecek.
**Gerekçe:** LLM'ler (OpenAI GPT) gerçek sayısal fiyat tahmini yapamaz,
sadece kalitatif yorum üretebilir. Kullanıcıya yanıltıcı kesinlik vermemek
ve gerçekçi bir fintech ürünü davranışı sergilemek için eklendi.

---

## D005 - Premium Sisteminde Rate Limiting Kaldırıldı
**Tarih:** Proje başlangıcı
**Karar:** Bucket4j ile API istek limiti (rate limiting) kapsam dışı bırakıldı.
Premium ayrımı sadece "AI Analiz sayfasına erişim" üzerinden yapılacak.
**Gerekçe:** Rate limiting, projenin öğrenme hedefleri açısından öncelikli
değil ve ek karmaşıklık katıyor. RBAC (@PreAuthorize ile rol bazlı erişim)
zaten mülakat açısından yeterli değeri sağlıyor.

---

## D006 - Build Tool: Maven (Gradle Değil)
**Tarih:** Proje kurulumu
**Karar:** Gradle yerine Maven kullanılacak.
**Gerekçe:** Spring Boot resmi dokümantasyonu ve çoğu öğrenim kaynağı
Maven (pom.xml, XML tabanlı) üzerinden örnek veriyor. Öğrenme sürecinde
kaynak bulmayı kolaylaştırıyor.

---

## D008 - Raporlama Sistemi Askıya Alındı
**Tarih:** 2026-08-26
**Karar:** Plan'daki Gün 64-66 PDF Raporlama Sistemi (iText/PDFBox/Apache POI) özelliği askıya alındı.
**Gerekçe:** Proje tamamlandıktan sonra değerlendirilecek. Çekirdek değer önerisine (portföy takibi + AI analiz + microservice dönüşümü) doğrudan katkısı düşük, ekstra kütüphane bağımlılığı getiriyor.

---

## D007 - Proje Yapısı: Monorepo
**Tarih:** Proje kurulumu
**Karar:** Backend, frontend ve ileride microservice'ler tek bir GitHub
reposu (cryptotracker-v2) altında, ayrı klasörlerde (backend/, frontend/
vb.) tutulacak.
**Gerekçe:** Tek kişilik proje için ayrı repolar yönetim karmaşıklığı
yaratır. Monorepo, dokümantasyon ve kodun bir arada, kolay erişilebilir
olmasını sağlıyor.