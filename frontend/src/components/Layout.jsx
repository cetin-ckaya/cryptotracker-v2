import { Outlet } from 'react-router-dom'
import Sidebar from './Sidebar'
import Topbar from './Topbar'
import Statusbar from './Statusbar'
import './Layout.css'

export default function Layout() {
  return (
    <div className="layout">
      <Sidebar />
      <div className="layout-main">
        <Topbar />
        <div className="layout-content">
          <Outlet />
        </div>
        <Statusbar />
      </div>
    </div>
  )
}
