-- ============================================
-- USERS
-- ============================================
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255),
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP
);

-- ============================================
-- COINS
-- ============================================
CREATE TABLE coins (
                       id BIGSERIAL PRIMARY KEY,
                       symbol VARCHAR(50) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       coingecko_id VARCHAR(255),
                       icon_url VARCHAR(500),
                       is_free_tier_watchlist BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP
);

-- ============================================
-- SUBSCRIPTIONS (1-1 -> users)
-- ============================================
CREATE TABLE subscriptions (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
                               tier VARCHAR(50) NOT NULL,
                               started_at TIMESTAMP,
                               expires_at TIMESTAMP,
                               created_at TIMESTAMP,
                               updated_at TIMESTAMP
);

-- ============================================
-- PORTFOLIOS (1-1 -> users)
-- ============================================
CREATE TABLE portfolios (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
                            created_at TIMESTAMP,
                            updated_at TIMESTAMP
);

-- ============================================
-- HOLDINGS (N-1 -> portfolios, N-1 -> coins)
-- ============================================
CREATE TABLE holdings (
                          id BIGSERIAL PRIMARY KEY,
                          portfolio_id BIGINT NOT NULL REFERENCES portfolios(id),
                          coin_id BIGINT NOT NULL REFERENCES coins(id),
                          quantity NUMERIC(20,8),
                          average_buy_price NUMERIC(20,8),
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
);

-- ============================================
-- TRANSACTIONS (N-1 -> portfolios, N-1 -> coins)
-- ============================================
CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              portfolio_id BIGINT NOT NULL REFERENCES portfolios(id),
                              coin_id BIGINT NOT NULL REFERENCES coins(id),
                              type VARCHAR(20) NOT NULL,
                              quantity NUMERIC(20,8) NOT NULL,
                              price_per_unit NUMERIC(20,8) NOT NULL,
                              total_amount NUMERIC(20,8) NOT NULL ,
                              transaction_date TIMESTAMP,
                              created_at TIMESTAMP,
                              updated_at TIMESTAMP
);

-- ============================================
-- PRICE_HISTORY (N-1 -> coins)
-- ============================================
CREATE TABLE price_history (
                               id BIGSERIAL PRIMARY KEY,
                               coin_id BIGINT NOT NULL REFERENCES coins(id),
                               price NUMERIC(20,8),
                               recorded_at TIMESTAMP,
                               created_at TIMESTAMP,
                               updated_at TIMESTAMP
);

-- ============================================
-- PORTFOLIO_VALUE_HISTORY (N-1 -> portfolios)
-- ============================================
CREATE TABLE portfolio_value_history (
                                         id BIGSERIAL PRIMARY KEY,
                                         portfolio_id BIGINT NOT NULL REFERENCES portfolios(id),
                                         total_value NUMERIC(20,2),
                                         recorded_at TIMESTAMP,
                                         created_at TIMESTAMP,
                                         updated_at TIMESTAMP
);

-- ============================================
-- AI_ANALYSIS_LOGS (N-1 -> users, N-1 -> coins nullable)
-- ============================================
CREATE TABLE ai_analysis_logs (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT NOT NULL REFERENCES users(id),
                                  coin_id BIGINT REFERENCES coins(id),
                                  analysis_type VARCHAR(50) NOT NULL,
                                  recommendation VARCHAR(20),
                                  trend_outlook VARCHAR(20),
                                  content TEXT,
                                  created_at TIMESTAMP,
                                  updated_at TIMESTAMP
);

-- ============================================
-- USER_BEHAVIOR_EVENTS (N-1 -> users)
-- ============================================
CREATE TABLE user_behavior_events (
                                      id BIGSERIAL PRIMARY KEY,
                                      user_id BIGINT NOT NULL REFERENCES users(id),
                                      event_type VARCHAR(100) NOT NULL,
                                      metadata JSONB,
                                      created_at TIMESTAMP,
                                      updated_at TIMESTAMP
);