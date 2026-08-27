import { useQuery } from '@tanstack/react-query'
import api from '../api/axios'
import './Dashboard.css'

export default function Transactions() {
  const { data: transactions, isLoading } = useQuery({
    queryKey: ['transactions'],
    queryFn: () => api.get('/transactions').then(r => r.data),
  })

  return (
    <div className="dashboard">
      <div className="page-header">
        <div>
          <h1 className="page-title">İşlemlerim</h1>
          <p className="page-sub">Tüm alım/satım işlem geçmişiniz</p>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">İşlem Geçmişi</span>
        </div>
        <table className="holdings-table">
          <thead>
            <tr>
              <th>Coin</th>
              <th>Tür</th>
              <th>Miktar</th>
              <th>Birim Fiyat</th>
              <th>Toplam</th>
              <th>Tarih</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr><td colSpan={6} className="loading-cell">Yükleniyor...</td></tr>
            ) : !transactions?.length ? (
              <tr><td colSpan={6} className="loading-cell">Henüz işlem yok</td></tr>
            ) : transactions.map(t => (
              <tr key={t.id}>
                <td>
                  <div className="coin-cell">
                    <div className="coin-icon">{t.coinSymbol?.[0]}</div>
                    <div>
                      <div className="coin-name">{t.coinName}</div>
                      <div className="coin-symbol">{t.coinSymbol}</div>
                    </div>
                  </div>
                </td>
                <td>
                  <span className={t.type === 'BUY' ? 'pos' : t.type === 'SELL' ? 'neg' : 'neutral'}>
                    {t.type === 'BUY' ? 'ALIŞ' : t.type === 'SELL' ? 'SATIŞ' : 'TUT'}
                  </span>
                </td>
                <td>{t.quantity}</td>
                <td>₺ {t.pricePerUnit?.toLocaleString('tr-TR', { minimumFractionDigits: 2 })}</td>
                <td>₺ {t.totalAmount?.toLocaleString('tr-TR', { minimumFractionDigits: 2 })}</td>
                <td>{t.transactionDate ? new Date(t.transactionDate).toLocaleDateString('tr-TR') : '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
