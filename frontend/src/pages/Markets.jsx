import './Dashboard.css'

const MARKETS = [
  { symbol: 'BTC/USDT', name: 'Bitcoin', price: '67,245.30', change: '+2.45%', volume: '48.2B', positive: true, color: '#F7931A' },
  { symbol: 'ETH/USDT', name: 'Ethereum', price: '3,421.85', change: '+1.87%', volume: '22.1B', positive: true, color: '#627EEA' },
  { symbol: 'BNB/USDT', name: 'BNB', price: '595.40', change: '-0.23%', volume: '1.8B', positive: false, color: '#F3BA2F' },
  { symbol: 'SOL/USDT', name: 'Solana', price: '165.75', change: '+3.21%', volume: '4.5B', positive: true, color: '#9945FF' },
  { symbol: 'ADA/USDT', name: 'Cardano', price: '0.4852', change: '+1.23%', volume: '0.9B', positive: true, color: '#0033AD' },
  { symbol: 'XRP/USDT', name: 'XRP', price: '0.5123', change: '+0.87%', volume: '2.1B', positive: true, color: '#00AAE4' },
]

export default function Markets() {
  return (
    <div className="dashboard">
      <div className="page-header">
        <div>
          <h1 className="page-title">Piyasalar</h1>
          <p className="page-sub">Anlık kripto para fiyatları</p>
        </div>
        <span className="badge-live">CANLI</span>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">Tüm Piyasalar</span>
        </div>
        <table className="holdings-table">
          <thead>
            <tr>
              <th>Coin</th>
              <th>Fiyat</th>
              <th>24s Değişim</th>
              <th>Hacim</th>
            </tr>
          </thead>
          <tbody>
            {MARKETS.map(m => (
              <tr key={m.symbol}>
                <td>
                  <div className="coin-cell">
                    <div className="coin-icon" style={{ background: m.color }}>{m.name[0]}</div>
                    <div>
                      <div className="coin-name">{m.name}</div>
                      <div className="coin-symbol">{m.symbol}</div>
                    </div>
                  </div>
                </td>
                <td>$ {m.price}</td>
                <td><span className={m.positive ? 'pos' : 'neg'}>{m.change}</span></td>
                <td>$ {m.volume}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
