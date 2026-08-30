-- ============================================
-- COINS SEED
-- MarketScheduler.refreshPrice() fiyatlari coinRepository.findAll() uzerinden
-- yayinliyor. Yani bir coin bu tabloda yoksa fiyati hic cekilmez ve
-- /topic/prices kanalina hic dusmez. Takip edilen coinler burada tanimli olmali.
--
-- Her satir WHERE NOT EXISTS ile korunuyor: coins tablosunda symbol uzerinde
-- UNIQUE kisit yok, migration daha once eklenmis satirlari tekrarlamasin diye.
-- ============================================

INSERT INTO coins (symbol, name, coingecko_id, is_free_tier_watchlist, created_at, updated_at)
SELECT 'BTC', 'Bitcoin', 'bitcoin', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM coins WHERE symbol = 'BTC');

INSERT INTO coins (symbol, name, coingecko_id, is_free_tier_watchlist, created_at, updated_at)
SELECT 'ETH', 'Ethereum', 'ethereum', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM coins WHERE symbol = 'ETH');

-- DIKKAT: CoinGecko'da BNB'nin id'si "bnb" degil "binancecoin".
INSERT INTO coins (symbol, name, coingecko_id, is_free_tier_watchlist, created_at, updated_at)
SELECT 'BNB', 'Binance Coin', 'binancecoin', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM coins WHERE symbol = 'BNB');

INSERT INTO coins (symbol, name, coingecko_id, is_free_tier_watchlist, created_at, updated_at)
SELECT 'SOL', 'Solana', 'solana', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM coins WHERE symbol = 'SOL');

-- Elle eklenmis eski satirlarda coingecko_id bos kalmis olabilir — dolduruyoruz.
UPDATE coins SET coingecko_id = 'bitcoin'     WHERE symbol = 'BTC' AND (coingecko_id IS NULL OR coingecko_id = '');
UPDATE coins SET coingecko_id = 'ethereum'    WHERE symbol = 'ETH' AND (coingecko_id IS NULL OR coingecko_id = '');
UPDATE coins SET coingecko_id = 'binancecoin' WHERE symbol = 'BNB' AND (coingecko_id IS NULL OR coingecko_id = '');
UPDATE coins SET coingecko_id = 'solana'      WHERE symbol = 'SOL' AND (coingecko_id IS NULL OR coingecko_id = '');
