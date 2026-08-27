import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import ReactMarkdown from 'react-markdown'
import { Bot } from 'lucide-react'
import api from '../api/axios'
import './Dashboard.css'
import './AiAnalysis.css'

export default function AiAnalysis() {
  const [trigger, setTrigger] = useState(false)

  const { data, isLoading, error } = useQuery({
    queryKey: ['ai-analysis', trigger],
    queryFn: () => api.get('/ai/analyze').then(r => r.data),
    enabled: trigger,
    staleTime: Infinity,
  })

  return (
    <div className="dashboard">
      <div className="page-header">
        <div>
          <h1 className="page-title">AI Analiz</h1>
          <p className="page-sub">Groq AI destekli portföy analizi</p>
        </div>
        <span className="badge-new">YENİ</span>
      </div>

      <div className="card ai-full-card">
        <div className="ai-icon-wrap">
          <Bot size={40} color="var(--accent-purple)" />
        </div>
        <h2 className="ai-full-title">AI Destekli Portföy Analizi</h2>
        <p className="ai-full-sub">
          Portföyünüzdeki varlıkları yapay zeka ile analiz edin. Groq API (openai/gpt-oss-20b) modeli portföyünüzü değerlendirerek kişiselleştirilmiş öneriler sunar.
        </p>
        <p className="ai-disclaimer">⚠️ Bu analiz yatırım tavsiyesi değildir. Yalnızca bilgi amaçlıdır.</p>

        {!trigger && (
          <button className="btn-primary ai-trigger-btn" onClick={() => setTrigger(true)}>
            Analizi Başlat
          </button>
        )}

        {isLoading && (
          <div className="ai-loading">
            <div className="ai-spinner" />
            <span>AI portföyünüzü analiz ediyor...</span>
          </div>
        )}

        {error && (
          <div className="ai-error">
            Analiz alınamadı. Premium üye olduğunuzdan emin olun.
          </div>
        )}

        {data && (
          <div className="ai-result">
            <ReactMarkdown>{typeof data === 'string' ? data : JSON.stringify(data)}</ReactMarkdown>
          </div>
        )}
      </div>
    </div>
  )
}
