import { useEffect, useState } from 'react'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

// Backend zinciri:
//   MarketScheduler (5 dk'da bir CoinGecko'dan fiyat ceker)
//     -> RabbitMQ price queue
//       -> PriceMessageConsumer
//         -> STOMP /topic/prices  { symbol: "BTC", price: 67245.30 }
//
// Bu hook o kanala abone olur ve { BTC: { price, prev, at } } seklinde
// bir sozluk dondurur. Topbar ve dashboard'daki canli fiyatlar bunu kullanir.
const WS_URL = 'http://localhost:8081/ws'

export default function useLivePrices() {
  const [prices, setPrices] = useState({})
  const [connected, setConnected] = useState(false)

  useEffect(() => {
    const client = new Client({
      // SockJS fallback ile baglan — backend .withSockJS() kullaniyor
      webSocketFactory: () => new SockJS(WS_URL),
      // Baglanti koparsa 5 sn sonra otomatik tekrar dene
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true)
        client.subscribe('/topic/prices', frame => {
          try {
            const msg = JSON.parse(frame.body)
            const sym = String(msg.symbol ?? '').toUpperCase()
            const price = Number(msg.price)
            if (!sym || !Number.isFinite(price)) return
            // Onceki fiyati saklariz — yuzde degisimi bundan hesaplanir
            setPrices(prev => ({
              ...prev,
              [sym]: { price, prev: prev[sym]?.price ?? null, at: Date.now() },
            }))
          } catch {
            // Bozuk mesaj gelirse sessizce yok say
          }
        })
      },
      onWebSocketClose: () => setConnected(false),
      onStompError: () => setConnected(false),
    })

    client.activate()
    return () => { client.deactivate() }
  }, [])

  return { prices, connected }
}

// Iki fiyat arasindaki yuzde degisim — onceki fiyat yoksa null
export function changePct(entry) {
  if (!entry?.prev || !entry.price) return null
  return ((entry.price - entry.prev) / entry.prev) * 100
}
