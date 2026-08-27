import { NavLink, useNavigate } from 'react-router-dom'
import { LayoutDashboard, Wallet, ArrowLeftRight, TrendingUp, Bot, Bell, FileText, Settings, LogOut, Activity, Server, ScrollText } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import './Sidebar.css'

const navItems = [
  { to: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/portfolio', icon: Wallet, label: 'Portföyüm' },
  { to: '/transactions', icon: ArrowLeftRight, label: 'İşlemlerim' },
  { to: '/markets', icon: TrendingUp, label: 'Piyasalar', badge: 'CANLI', badgeType: 'green' },
  { to: '/ai', icon: Bot, label: 'AI Analiz', badge: 'YENİ', badgeType: 'purple' },
  { to: '/alerts', icon: Bell, label: 'Alarmlar' },
  { to: '/reports', icon: FileText, label: 'Raporlar' },
  { to: '/settings', icon: Settings, label: 'Ayarlar' },
]

const systemItems = [
  { icon: Activity, label: 'API Durumu' },
  { icon: Server, label: 'Servis Sağlığı' },
  { icon: ScrollText, label: 'Loglar' },
]

export default function Sidebar() {
  const { logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">
          <svg width="26" height="26" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="9.5" stroke="#22d3ee" strokeWidth="2.4" fill="none"/>
            <circle cx="12" cy="12" r="4.5" stroke="#67e8f9" strokeWidth="2" fill="none"/>
            <circle cx="12" cy="12" r="1.6" fill="#a5f3fc"/>
          </svg>
        </div>
        <div>
          <div className="sidebar-logo-title">CryptoTracker</div>
          <div className="sidebar-logo-sub">AI-Powered Finance Platform</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        {navItems.map(item => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            className={({ isActive }) => `sidebar-item ${isActive ? 'active' : ''}`}
          >
            <item.icon size={17} />
            <span>{item.label}</span>
            {item.badge && (
              <span className={`sidebar-badge badge-${item.badgeType}`}>{item.badge}</span>
            )}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-section-label">SİSTEM</div>
      <nav className="sidebar-nav sidebar-system">
        {systemItems.map(item => (
          <div key={item.label} className="sidebar-item">
            <item.icon size={17} />
            <span>{item.label}</span>
          </div>
        ))}
        <div className="sidebar-item logout" onClick={handleLogout}>
          <LogOut size={17} />
          <span>Çıkış Yap</span>
        </div>
      </nav>

      <div className="sidebar-status">
        <span className="status-dot green" />
        <div>
          <div className="status-title">Sistem Durumu</div>
          <div className="status-sub">Tüm servisler çalışıyor</div>
          <div className="status-time">Son güncelleme: {new Date().toLocaleTimeString('tr-TR')}</div>
        </div>
      </div>
    </aside>
  )
}
