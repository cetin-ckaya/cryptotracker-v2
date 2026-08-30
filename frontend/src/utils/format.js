// Fiyat bicimleme — TEK KAYNAK.
// CoinGecko fiyatlari "vs_currencies=usd" ile cekiyor, yani tum tutarlar DOLAR.
// Para birimi degisirse sadece burasi degistirilir.
export const CURRENCY = '$'

const n = v => (v == null ? 0 : Number(v))

// $ 67.245,30 — iki basamak kurus
export const money = v =>
  `${CURRENCY} ${n(v).toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`

// $ 67.245 — kurussuz, dar alanlar icin
export const money0 = v =>
  `${CURRENCY} ${n(v).toLocaleString('tr-TR', { maximumFractionDigits: 0 })}`

// $ 1.24M / $ 38.8K / $ 940,00 — grafik ekseni ve donut ortasi icin
export const moneyShort = v =>
  n(v) >= 1_000_000 ? `${CURRENCY} ${(n(v) / 1_000_000).toFixed(2)}M`
    : n(v) >= 1_000 ? `${CURRENCY} ${(n(v) / 1_000).toFixed(1)}K`
      : money(v)

// +12,34% / -3,10%
export const percent = v => (n(v) >= 0 ? '+' : '') + n(v).toFixed(2) + '%'

// Coin miktari: 0,5 BTC
export const amount = (v, sym) =>
  n(v).toLocaleString('tr-TR', { maximumFractionDigits: 8 }) + (sym ? ` ${sym}` : '')
