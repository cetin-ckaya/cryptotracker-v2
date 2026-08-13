# CryptoTracker v2 - Requirements

## Epic 1: Kullanıcı Yönetimi & Kimlik Doğrulama
- Bir ziyaretçi olarak, email/şifre ile kayıt olabilmeliyim.
- Bir kullanıcı olarak, JWT tabanlı güvenli giriş yapabilmeliyim.
- Bir kullanıcı olarak, profil bilgilerimi güncelleyebilmeliyim.

## Epic 2: Portföy Yönetimi (Tek Portföy Modeli)
- Bir kullanıcı olarak, kayıt olduğumda otomatik bir portföyüm oluşmalı.
- Bir kullanıcı olarak, portföyüme coin ekleyip çıkarabilmeliyim.
- Bir kullanıcı olarak, portföyümün toplam değerini ve kar/zararını anlık görebilmeliyim.
- Bir kullanıcı olarak, varlık dağılımımı (pasta grafik) görsel olarak görebilmeliyim.

## Epic 3: İşlem (Transaction) Yönetimi
- Bir kullanıcı olarak, alım/satım işlemi kaydedebilmeliyim.
- Bir kullanıcı olarak, geçmiş işlemlerimi sayfalanmış tablo halinde görebilmeliyim.
- Bir kullanıcı olarak, işlem eklediğimde bakiyemin otomatik güncellenmesini istiyorum.

## Epic 4: Piyasa Verisi & Canlı Fiyat Takibi
- Bir kullanıcı olarak, güncel kripto fiyatlarını anlık (WebSocket) görebilmeliyim.
- Sistem olarak, dış API'den (CoinGecko) periyodik veri çekmeli ve cache'lemeliyim.

## Epic 5: AI Destekli Analiz (Freemium Model)
- FREE kullanıcı olarak, sabit izleme listesindeki (BTC, ETH, SOL) coinler için 
  genel AL/SAT/TUT sinyalini görebilmeliyim (herkese aynı gösterilir, 
  kişiselleştirme yok).
- PREMIUM kullanıcı olarak, sahip olduğum her coin için kişiselleştirilmiş 
  AL/SAT/TUT önerisi ve yön beklentisi (yükseliş/düşüş görünümü) görebilmeliyim.
- PREMIUM kullanıcı olarak, genel portföyüm hakkında özet bir AI yorumu alabilmeliyim.
- Sistem olarak, tüm AI çıktılarının yanına "Yatırım tavsiyesi değildir" 
  uyarısı eklemeliyim.

## Epic 6: Alarm Sistemi
[KAPSAM DIŞI - v2.1'de değerlendirilecek]

## Epic 7: Premium Üyelik & Abonelik
- Bir kullanıcı olarak, FREE'den PREMIUM'a yükseltme yapabilmeliyim 
  (simülasyon, gerçek ödeme entegrasyonu yok).
- FREE: Dashboard, Portföy, İşlemler, PDF Rapor → serbest kullanım.
- PREMIUM: Yukarılara ek olarak kişiselleştirilmiş AI Analiz sayfası.
- Rate limiting kapsam dışı bırakıldı.

## Epic 8: Raporlama
- Bir kullanıcı olarak, portföy performansımı PDF olarak indirebilmeliyim.

## Epic 9: Bildirim & Real-Time
- Bir kullanıcı olarak, fiyat değiştikçe ekranımın anlık güncellenmesini istiyorum.