import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  AreaChart, Area, LineChart, Line, BarChart, Bar, XAxis, YAxis,
  CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell,
} from 'recharts'
import {
  DollarSign, TrendingUp, TrendingDown, Wallet, Layers, Plus, ChevronDown,
  Info, ChevronRight, FileText, Loader2,
} from 'lucide-react'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import useLivePrices, { changePct } from '../hooks/useLivePrices'
import Coin from '../components/Coin'
import { money, money0, moneyShort, percent } from '../utils/format'
import './Dashboard.css'

/* ---------- Yardimcilar ---------- */
const num = v => (v == null ? 0 : Number(v))
// Para bicimleme utils/format.js'ten gelir — fiyatlar CoinGecko'dan dolar olarak cekiliyor
const tl = money
const tl0 = money0
const shortTl = moneyShort
const pctStr = percent

// Dagilim halkasi renkleri
const SLICE = ['#F7931A', '#7b6cf0', '#34d399', '#6c5ce7', '#8fb3ff', '#f0b429', '#f87171', '#22d3ee']

// 00:00 -> 24:00 arasi 15 dk araliklarla portfoy degeri egrisi.
// Egri guncel portfoy degerinde biter, gun basinda %8 asagidan baslar.
function makeSeries(end) {
  if (!end) return []
  const start = end * 0.92
  const out = []
  for (let i = 0; i <= 96; i++) {
    const trend = start + (end - start) * (i / 96)
    const noise = Math.sin(i / 7.3) * end * 0.012 + Math.sin(i / 3.1) * end * 0.007 + Math.sin(i / 1.27) * end * 0.003
    const hh = String(Math.floor(i / 4)).padStart(2, '0')
    const mm = String((i % 4) * 15).padStart(2, '0')
    out.push({ t: `${hh}:${mm}`, v: Math.round(i === 96 ? end : trend + noise) })
  }
  return out
}

function Spark({ data, color }) {
  return (
    <ResponsiveContainer width="100%" height="100%">
      <LineChart data={data.map((v, i) => ({ i, v }))}>
        <Line type="monotone" dataKey="v" stroke={color} strokeWidth={1.6} dot={false} />
      </LineChart>
    </ResponsiveContainer>
  )
}

/* AI metninden "BTC: AL" / "ETH - TUT" gibi sinyalleri ayikla */
function parseSignals(text) {
  if (!text) return []
  const out = []
  const re = /\b([A-Z]{2,6})\b\s*[:\-–—]\s*(AL|SAT|TUT)\b/g
  let m
  while ((m = re.exec(text.toUpperCase())) !== null) {
    if (!out.some(s => s.sym === m[1])) out.push({ sym: m[1], action: m[2] })
  }
  return out.slice(0, 4)
}

/* AI metnini madde listesine cevir — markdown isaretlerini ve baslik
   satirlarini ayikla, sadece anlamli cumleleri birak */
function toBullets(text) {
  if (!text) return []
  return text
    .split('\n')
    .map(l => l
      .replace(/\*\*/g, '')          // kalin isaretleri
      .replace(/^[\s*\-•>#\d.]+/, '') // satir basi madde/numara isaretleri
      .replace(/\|/g, ' ')            // tablo cizgileri
      .trim())
    .filter(l =>
      l.length > 30 &&                // cok kisa satir = baslik
      !l.endsWith(':') &&             // "Guclu Yonleri:" gibi basliklar
      /[.!?]$|[a-zçğıöşü]$/i.test(l)) // cumle gibi bitenler
    .slice(0, 4)
}

export default function Dashboard() {
  const [portfolio, setPortfolio] = useState(null)
  const [loading, setLoading] = useState(true)
  const [ai, setAi] = useState({ state: 'loading', text: '' })
  const navigate = useNavigate()
  const { user } = useAuth()
  const { prices: live, connected } = useLivePrices()
  const firstName = (user?.email?.split('@')[0] ?? 'Kullanıcı').replace(/^./, c => c.toUpperCase())

  useEffect(() => {
    api.get('/portfolio')
      .then(r => setPortfolio(r.data))
      .catch(() => setPortfolio(null))
      .finally(() => setLoading(false))

    api.get('/ai/analyze')
      .then(r => setAi({ state: 'ok', text: typeof r.data === 'string' ? r.data : JSON.stringify(r.data) }))
      .catch(err => setAi({ state: err?.response?.status === 403 ? 'locked' : 'error', text: '' }))
  }, [])

  /* ----- Tum ozet degerleri kullanicinin gercek portfoyunden ----- */
  const holdings = portfolio?.holdings ?? []
  const totalValue = num(portfolio?.totalValue)
  const invested = num(portfolio?.totalInvested)
  const pnl = num(portfolio?.totalProfitLoss)
  const pnlPct = num(portfolio?.totalProfitLossPercentage)
  const up = pnl >= 0
  const assetCount = holdings.length

  // Gunluk kar/zarar: 24 saat onceki portfolio_value_history snapshot'ina gore.
  // Backend bu alanlari doldurana kadar null kalir — kart "—" gosterir.
  const daily = portfolio?.dailyProfitLoss != null ? Number(portfolio.dailyProfitLoss) : null
  const dailyPct = portfolio?.dailyProfitLossPercentage != null
    ? Number(portfolio.dailyProfitLossPercentage)
    : null
  const dailyUp = (daily ?? 0) >= 0

  // Coin bazli maliyet = miktar x ortalama alis fiyati (API'den gelen gercek alanlar)
  const rows = holdings.map((h, i) => {
    const cost = num(h.quantity) * num(h.averageBuyPrice)
    return {
      id: h.id ?? h.coinSymbol,
      sym: h.coinSymbol,
      name: h.coinName ?? h.coinSymbol,
      qty: num(h.quantity),
      avg: num(h.averageBuyPrice),
      cost,
      // Backend coin bazli guncel fiyat doner ise dogrudan onu kullan
      price: h.currentPrice != null ? num(h.currentPrice) : null,
      value: h.currentValue != null ? num(h.currentValue) : null,
      chg: h.profitLoss != null ? num(h.profitLoss) : null,
      chgPct: h.profitLossPercentage != null ? num(h.profitLossPercentage) : null,
      color: SLICE[i % SLICE.length],
    }
  })

  const hasLivePrices = rows.length > 0 && rows[0].price != null
  const costTotal = rows.reduce((s, r) => s + r.cost, 0)

  // Varlik dagilimi: canli fiyat varsa guncel degere, yoksa maliyete gore
  const allocation = rows.map(r => {
    const base = hasLivePrices ? (r.value ?? 0) : r.cost
    const denom = hasLivePrices ? totalValue : costTotal
    return {
      name: `${r.name} (${r.sym})`,
      pct: denom ? Number(((base / denom) * 100).toFixed(1)) : 0,
      value: base,
      color: r.color,
    }
  })

  const series = makeSeries(totalValue)
  const sparkSeed = up ? [12, 15, 13, 18, 16, 21, 19, 24, 22, 27, 25, 30] : [30, 26, 28, 23, 25, 20, 22, 17, 19, 14, 16, 12]
  const barSeed = [22, 30, 18, 36, 26, 42, 30, 48, 38, 55, 44, 62, 50, 68]

  const signals = parseSignals(ai.text)
  const bullets = toBullets(ai.text)
  const topAsset = allocation.slice().sort((a, b) => b.pct - a.pct)[0]

  const dash = <span className="muted">—</span>

  return (
    <div className="dash">
      {/* Baslik */}
      <div className="dash-head">
        <div>
          <h1>Dashboard</h1>
          <p>Portföyünüzün genel görünümü</p>
        </div>
        <div className="dash-head-actions">
          <div className="range-select">Son 24 Saat <ChevronDown size={16} color="#7d87a4" /></div>
          <button className="btn-add" onClick={() => navigate('/portfolio')}><Plus size={16} /> İşlem Ekle</button>
        </div>
      </div>

      {/* Ozet kartlari — hepsi kullanicinin portfoyunden */}
      <div className="stats">
        <div className="card stat">
          <div className="stat-top">
            <span className="stat-label">Toplam Portföy Değeri</span>
            <div className="stat-ico green"><DollarSign size={20} /></div>
          </div>
          <div className="stat-value">{loading ? '—' : tl(totalValue)}</div>
          {!loading && (
            <div className={`stat-sub ${up ? 'green' : 'red'}`}>
              {up ? '+ ' : '- '}{tl(Math.abs(pnl))} ({pnlPct.toFixed(2)}%)
            </div>
          )}
          {totalValue > 0 && (
            <div className="stat-spark">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={sparkSeed.map((v, i) => ({ i, v }))}>
                  <defs>
                    <linearGradient id="sg" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor={up ? '#34d399' : '#f87171'} stopOpacity="0.35" />
                      <stop offset="100%" stopColor={up ? '#34d399' : '#f87171'} stopOpacity="0" />
                    </linearGradient>
                  </defs>
                  <Area type="monotone" dataKey="v" stroke={up ? '#34d399' : '#f87171'} strokeWidth={2} fill="url(#sg)" dot={false} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>

        <div className="card stat">
          <div className="stat-top">
            <span className="stat-label">Günlük Kar/Zarar</span>
            <div className={`stat-ico ${dailyUp ? 'blue' : 'red'}`}>
              {dailyUp ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
            </div>
          </div>
          <div className={`stat-value ${daily == null ? '' : dailyUp ? 'green' : 'red'}`}>
            {loading || daily == null ? '—' : (dailyUp ? '+ ' : '- ') + tl(Math.abs(daily))}
          </div>
          {!loading && (
            daily == null
              ? <div className="stat-sub">Son 24 saatlik kayıt bekleniyor</div>
              : <div className={`stat-sub ${dailyUp ? 'green' : 'red'}`}>{pctStr(dailyPct)}</div>
          )}
          {daily != null && (
            <div className="stat-spark">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={barSeed.map((v, i) => ({ i, v }))}>
                  <Bar dataKey="v" fill={dailyUp ? '#4a8ef0' : '#f87171'} fillOpacity={0.75} radius={[2, 2, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>

        <div className="card stat">
          <div className="stat-top">
            <span className="stat-label">Toplam Yatırım</span>
            <div className="stat-ico amber"><Wallet size={20} /></div>
          </div>
          <div className="stat-value">{loading ? '—' : tl(invested)}</div>
          {!loading && <div className="stat-sub">Yatırılan toplam tutar</div>}
        </div>

        <div className="card stat">
          <div className="stat-top">
            <span className="stat-label">Aktif Varlık Sayısı</span>
            <div className="stat-ico violet"><Layers size={20} /></div>
          </div>
          <div className="stat-value">{loading ? '—' : assetCount}</div>
          {!loading && <div className="stat-sub">Farklı kripto para</div>}
        </div>
      </div>

      {/* Grafik + Dagilim */}
      <div className="mid-grid">
        <div className="card">
          <div className="card-head">
            <div className="card-title">Portföy Değeri Grafiği <Info size={14} color="#6f7b96" /></div>
            <div className="ranges">
              <button className="on">1G</button><button>1H</button><button>1A</button>
              <button>3A</button><button>1Y</button><button>Tümü</button>
            </div>
          </div>
          <div className="big-chart">
            {series.length === 0 ? (
              <div className="empty">Grafik için portföyünüze varlık ekleyin.</div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={series} margin={{ top: 8, right: 8, bottom: 0, left: 0 }}>
                  <defs>
                    <linearGradient id="pg" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor="#6c5ce7" stopOpacity="0.28" />
                      <stop offset="100%" stopColor="#6c5ce7" stopOpacity="0" />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 4" stroke="#1e2334" vertical={false} />
                  <XAxis dataKey="t" tick={{ fontSize: 11, fill: '#6f7b96' }} axisLine={false} tickLine={false} interval={15} />
                  <YAxis tick={{ fontSize: 11, fill: '#6f7b96' }} axisLine={false} tickLine={false} width={62}
                    domain={['dataMin', 'dataMax']} tickFormatter={shortTl} />
                  <Tooltip formatter={v => [tl(v), '']}
                    contentStyle={{ background: '#1b2033', border: '1px solid #2b3149', borderRadius: 8, fontSize: 12 }}
                    labelStyle={{ color: '#8b95b0', fontSize: 11 }} itemStyle={{ color: '#f1f5f9' }} />
                  <Area type="monotone" dataKey="v" stroke="#7b6cf0" strokeWidth={2} fill="url(#pg)"
                    dot={false} activeDot={{ r: 4, fill: '#7b6cf0', stroke: '#fff', strokeWidth: 1.5 }} />
                </AreaChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-head"><div className="card-title">Varlık Dağılımı</div></div>
          {allocation.length === 0 ? (
            <div className="empty tall">Henüz varlığınız yok.</div>
          ) : (
            <div className="alloc-body">
              <div className="donut">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie data={allocation} dataKey="pct" nameKey="name"
                      innerRadius={62} outerRadius={98} paddingAngle={1} stroke="none">
                      {allocation.map(a => <Cell key={a.name} fill={a.color} />)}
                    </Pie>
                  </PieChart>
                </ResponsiveContainer>
                <div className="donut-mid">
                  <small>Toplam</small>
                  <b>{shortTl(totalValue)}</b>
                </div>
              </div>
              <div className="legend">
                {allocation.map(a => (
                  <div className="legend-row" key={a.name}>
                    <i style={{ background: a.color }} />
                    <span className="legend-name">{a.name}</span>
                    <span className="legend-pct">{a.pct}%</span>
                    <span className="legend-val">{tl0(a.value)}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Varliklar + AI + Canli fiyatlar */}
      <div className="bot-grid">
        <div className="card">
          <div className="card-head"><div className="card-title">Portföy Varlıklarım</div></div>
          <table className="assets-table">
            <thead>
              <tr>
                <th>Varlık</th><th>Miktar</th>
                <th>{hasLivePrices ? 'Güncel Fiyat' : 'Ort. Alış Fiyatı'}</th>
                <th>{hasLivePrices ? 'Toplam Değer' : 'Toplam Maliyet'}</th>
                <th>{hasLivePrices ? 'Günlük Değişim' : 'Portföy Payı'}</th>
              </tr>
            </thead>
            <tbody>
              {rows.slice(0, 5).map((r, i) => (
                <tr key={r.id}>
                  <td>
                    <div className="asset-cell">
                      <Coin sym={r.sym} size={30} />
                      <div><b>{r.name}</b><span>({r.sym})</span></div>
                    </div>
                  </td>
                  <td className="muted">{r.qty} {r.sym}</td>
                  <td>
                    {tl(hasLivePrices ? r.price : r.avg)}
                    {hasLivePrices && (
                      <span className={`cell-sub ${r.chgPct >= 0 ? 'pos' : 'neg'}`}>{pctStr(r.chgPct)}</span>
                    )}
                  </td>
                  <td>{tl(hasLivePrices ? r.value : r.cost)}</td>
                  {hasLivePrices ? (
                    <td className={r.chg >= 0 ? 'pos' : 'neg'}>
                      {r.chg >= 0 ? '+ ' : '- '}{tl(Math.abs(r.chg))}
                      <span className={`cell-sub ${r.chgPct >= 0 ? 'pos' : 'neg'}`}>{pctStr(r.chgPct)}</span>
                    </td>
                  ) : (
                    <td>{allocation[i]?.pct ?? 0}%</td>
                  )}
                </tr>
              ))}
              {!loading && rows.length === 0 && (
                <tr><td colSpan={5}><div className="empty">Portföyünüzde henüz varlık yok.</div></td></tr>
              )}
            </tbody>
          </table>
          <div className="card-foot" onClick={() => navigate('/portfolio')}>
            Tüm Varlıkları Görüntüle <ChevronRight size={15} />
          </div>
        </div>

        <div className="card">
          <div className="card-head">
            <div className="card-title">AI Destekli Portföy Analizi <span className="badge-new">YENİ</span></div>
            <span className="ai-model">Groq · gpt-oss</span>
          </div>
          <div className="ai-box">
            <div className="ai-hi">Merhaba {firstName}! 👋</div>
            {ai.state === 'loading' && (
              <div className="ai-lead"><Loader2 size={14} className="spin" /> Portföyünüz analiz ediliyor...</div>
            )}
            {ai.state === 'locked' && (
              <div className="ai-lead">AI portföy analizi Premium üyeliğe özeldir. Yükseltmek için AI Analiz sayfasına göz atın.</div>
            )}
            {ai.state === 'error' && (
              <div className="ai-lead">Analiz şu anda alınamadı. AI Analiz sayfasından tekrar deneyebilirsiniz.</div>
            )}
            {ai.state === 'ok' && (
              <>
                <div className="ai-lead">
                  {assetCount > 0
                    ? `Portföyünüzde ${assetCount} farklı varlık var${topAsset ? `, en büyüğü ${topAsset.name} (%${topAsset.pct})` : ''}. İşte analizim:`
                    : 'Henüz varlığınız yok. İlk işleminizi ekleyince analiz burada görünecek.'}
                </div>
                <div className="ai-list">
                  {bullets.map((b, i) => (
                    <div className="ai-item" key={i}><span>{['✅', '✅', '⚠️', '💡'][i] ?? '•'}</span><span>{b}</span></div>
                  ))}
                </div>
              </>
            )}
          </div>
          {signals.length > 0 && (
            <>
              <div className="ai-sign-title">Al/Sat Önerisi:</div>
              <div className="ai-signals">
                {signals.map(s => (
                  <span key={s.sym} className={`sig ${s.action === 'AL' ? 'buy' : s.action === 'SAT' ? 'sell' : 'hold'}`}>
                    {s.sym}: {s.action}
                  </span>
                ))}
              </div>
            </>
          )}
          <div className="ai-note">* Yatırım tavsiyesi değildir.</div>
          <button className="ai-btn" onClick={() => navigate('/ai')}>
            Detaylı Analiz Raporu <FileText size={16} />
          </button>
        </div>

        <div className="card">
          <div className="card-head">
            <div className="card-title">Canlı Fiyatlar</div>
            <span className={`live-badge ${connected ? '' : 'off'}`}>
              <i /> {connected ? 'CANLI' : 'BAĞLANIYOR'}
            </span>
          </div>
          <div className="live-list">
            {rows.length === 0 && <div className="empty">Takip edilen varlık yok.</div>}
            {rows.map(r => {
              // WebSocket'ten gelen canli fiyat varsa onu, yoksa ortalama alis fiyatini goster
              const entry = live[r.sym?.toUpperCase()]
              const wsPct = changePct(entry)
              const shown = entry?.price ?? r.price ?? r.avg
              const pct = wsPct ?? r.chgPct
              return (
                <div className="live-row" key={r.id}>
                  <Coin sym={r.sym} size={30} />
                  <span className="live-pair">{r.sym}/USDT</span>
                  <div className="live-nums">
                    <span className="live-price">{tl(shown)}</span>
                    <span className={`live-chg ${pct == null ? 'muted' : pct >= 0 ? 'pos' : 'neg'}`}>
                      {pct == null ? (entry ? 'güncel' : 'ort. alış') : pctStr(pct)}
                    </span>
                  </div>
                  <div className="live-spark">
                    <Spark data={sparkSeed.slice(0, 8)} color={pct == null || pct >= 0 ? '#34d399' : '#f87171'} />
                  </div>
                </div>
              )
            })}
          </div>
          <div className="card-foot" onClick={() => navigate('/markets')}>
            Tüm Piyasaları Görüntüle <ChevronRight size={15} />
          </div>
        </div>
      </div>
    </div>
  )
}
