import { useQuery } from '@tanstack/react-query'
import api from '../api/axios'
import './Dashboard.css'

export default function Portfolio() {
  const { data: portfolio, isLoading } = useQuery({
    queryKey: ['portfolio'],
    queryFn: () => api.get('/portfolio').then(r => r.data),
  })

  const holdings = portfolio?.holdings ?? []

  return (
    <div className="dashboard">
      <div className="page-header">
        <div>
          <h1 className="page-title">Portföyüm</h1>
          <p className="page-sub">Tüm varlıklarınız ve performans bilgileri</p>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">Varlıklarım</span>
        </div>
        <table className="holdings-table">
          <thead>
            <tr>
              <th>Varlık</th>
              <th>Miktar</th>
              <th>Ortalama Alış Fiyatı</th>
              <th>Toplam Değer</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr><td colSpan={4} className="loading-cell">Yükleniyor...</td></tr>
            ) : holdings.length === 0 ? (
              <tr><td colSpan={4} className="loading-cell">Portföyünüzde henüz varlık yok</td></tr>
            ) : holdings.map(h => (
              <tr key={h.id}>
                <td>
                  <div className="coin-cell">
                    <div className="coin-icon">{h.coinSymbol[0]}</div>
                    <div>
                      <div className="coin-name">{h.coinName}</div>
                      <div className="coin-symbol">{h.coinSymbol}</div>
                    </div>
                  </div>
                </td>
                <td>{h.quantity} {h.coinSymbol}</td>
                <td>₺ {h.averageBuyPrice?.toLocaleString('tr-TR', { minimumFractionDigits: 2 })}</td>
                <td>₺ {(h.quantity * h.averageBuyPrice)?.toLocaleString('tr-TR', { minimumFractionDigits: 2 })}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
