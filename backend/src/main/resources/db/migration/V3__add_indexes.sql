-- Flyway bu dosyayi bir kez calistirir ve migration_history'e kaydeder.
-- V2'den sonra gelmeli — versiyon sirasi zorunlu.

-- holdings tablosu: portfoy sorgulandiginda kullanilir
CREATE INDEX idx_holdings_portfolio_id ON holdings(portfolio_id);

-- transactions tablosu: kullanicinin islem gecmisi
CREATE INDEX idx_transactions_portfolio_id ON transactions(portfolio_id);

--price_history tablosuna coin_id için index
CREATE INDEX idx_price_history_coin_id on price_history(coin_id);

--portfolio_value_history tablosuna portfolio_id için index
CREATE INDEX idx_portfolio_value_history_portfolio_id ON portfolio_value_history(portfolio_id);

--ai_analysis_logs tablosuna user_id için index
CREATE INDEX idx_ai_analysis_logs_user_id ON ai_analysis_logs(user_id);

--user_behavior_events tablosuna user_id için index
CREATE INDEX idx_user_behavior_events_user_id ON user_behavior_events(user_id);

--coins tablosuna symbol için index
CREATE INDEX idx_coins_symbol ON coins(symbol);