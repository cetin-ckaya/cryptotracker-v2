import { Bell, ChevronDown } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import useLivePrices, { changePct } from '../hooks/useLivePrices'
import './Topbar.css'

// Topbar'da takip edilen pariteler — fiyatlari WebSocket'ten canli gelir
const COINS = [
  {
    key: 'BTC', label: 'BTC/USDT',
    icon: (
      <svg width="22" height="22" viewBox="0 0 22 22">
        <circle cx="11" cy="11" r="11" fill="#F7931A"/>
        <text x="11" y="15.5" textAnchor="middle" fill="white" fontSize="12" fontWeight="bold">₿</text>
      </svg>
    )
  },
  {
    key: 'ETH', label: 'ETH/USDT',
    icon: (
      <svg width="22" height="22" viewBox="0 0 22 22">
        <circle cx="11" cy="11" r="11" fill="#627EEA"/>
        <polygon points="11,4 15.5,11 11,13.5 6.5,11" fill="white" opacity="0.9"/>
        <polygon points="11,14.5 15.5,12 11,18 6.5,12" fill="white" opacity="0.6"/>
      </svg>
    )
  },
  {
    key: 'BNB', label: 'BNB/USDT',
    icon: (
      <svg width="22" height="22" viewBox="0 0 22 22">
        <circle cx="11" cy="11" r="11" fill="#F3BA2F"/>
        <text x="11" y="15.5" textAnchor="middle" fill="white" fontSize="11" fontWeight="bold">B</text>
      </svg>
    )
  },
  {
    key: 'SOL', label: 'SOL/USDT',
    icon: (
      <svg width="22" height="22" viewBox="0 0 22 22">
        <circle cx="11" cy="11" r="11" fill="#9945FF"/>
        <text x="11" y="15.5" textAnchor="middle" fill="white" fontSize="11" fontWeight="bold">S</text>
      </svg>
    )
  },
]

const fmtPrice = n => Number(n).toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })

export default function Topbar() {
  const { user } = useAuth()
  const { prices, connected } = useLivePrices()
  const displayName = user?.email?.split('@')[0] ?? 'Kullanıcı'

  return (
    <header className="topbar">
      <div className="topbar-prices">
        {COINS.map(c => {
          const entry = prices[c.key]
          const pct = changePct(entry)
          return (
            <div key={c.key} className="price-chip">
              <div className="price-coin-svg">{c.icon}</div>
              <div className="price-info">
                <div className="price-symbol">{c.label}</div>
                <div className="price-row">
                  <span className="price-value">
                    {entry ? fmtPrice(entry.price) : '—'}
                  </span>
                  {pct != null ? (
                    <span className={`price-change ${pct >= 0 ? 'pos' : 'neg'}`}>
                      {(pct >= 0 ? '+' : '') + pct.toFixed(2)}%
                    </span>
                  ) : (
                    <span className="price-change wait">
                      {entry ? 'güncel' : connected ? 'bekleniyor' : 'bağlanıyor'}
                    </span>
                  )}
                </div>
              </div>
            </div>
          )
        })}
      </div>

      <div className="topbar-user">
        <div className="topbar-bell">
          <Bell size={18} strokeWidth={1.8} />
          <span className="bell-badge">3</span>
        </div>
        <div className="user-avatar">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <circle cx="10" cy="7" r="3.5" fill="#8892b0"/>
            <path d="M3 18c0-3.866 3.134-7 7-7s7 3.134 7 7" stroke="#8892b0" strokeWidth="1.5" fill="none"/>
          </svg>
        </div>
        <div className="user-text">
          <div className="user-name">{displayName}</div>
          <div className="user-tier">Premium Üye</div>
        </div>
        <ChevronDown size={14} color="#5a6280" />
      </div>
    </header>
  )
}
