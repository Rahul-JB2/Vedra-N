import { useState } from 'react'
import HomeScreen from './screens/HomeScreen'
import VedScreen from './screens/VedScreen'
import ActionsScreen from './screens/ActionsScreen'
import SettingsScreen from './screens/SettingsScreen'
import MemoryScreen from './screens/MemoryScreen'
import styles from './App.module.css'

const TABS = [
  { id: 'home', label: 'Home', icon: '🏠' },
  { id: 'ved', label: 'VED', icon: '🤖' },
  { id: 'actions', label: 'Actions', icon: '⚡' },
  { id: 'memory', label: 'Memory', icon: '🧠' },
  { id: 'settings', label: 'Settings', icon: '⚙️' },
]

export default function App() {
  const [activeTab, setActiveTab] = useState('home')
  const [pendingVedCommand, setPendingVedCommand] = useState(null)

  const handleActionToVed = (cmd) => {
    setPendingVedCommand(cmd)
    setActiveTab('ved')
  }

  return (
    <div className={styles.app}>
      {/* Header */}
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <div className={styles.logo}>V</div>
          <div>
            <div className={styles.appName}>VEDRA</div>
            <div className={styles.appSub}>Personal AI Assistant</div>
          </div>
        </div>
        <div className={styles.onlineBadge}>
          <span className={styles.dot} />
          Online
        </div>
      </div>

      {/* Screens — keep all mounted so VED retains chat history */}
      <div className={styles.screen}>
        <div style={{ display: activeTab === 'home' ? 'contents' : 'none' }}>
          <HomeScreen onNavigate={setActiveTab} />
        </div>
        <div style={{ display: activeTab === 'ved' ? 'contents' : 'none' }}>
          <VedScreen
            pendingCommand={pendingVedCommand}
            onPendingCommandConsumed={() => setPendingVedCommand(null)}
          />
        </div>
        <div style={{ display: activeTab === 'actions' ? 'contents' : 'none' }}>
          <ActionsScreen onSendToVed={handleActionToVed} />
        </div>
        <div style={{ display: activeTab === 'memory' ? 'contents' : 'none' }}>
          <MemoryScreen />
        </div>
        <div style={{ display: activeTab === 'settings' ? 'contents' : 'none' }}>
          <SettingsScreen />
        </div>
      </div>

      {/* Bottom nav */}
      <div className={styles.nav}>
        {TABS.map(tab => (
          <button
            key={tab.id}
            className={`${styles.navBtn} ${activeTab === tab.id ? styles.navBtnActive : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            <span className={styles.navIcon}>{tab.icon}</span>
            <span className={styles.navLabel}>{tab.label}</span>
          </button>
        ))}
      </div>
    </div>
  )
}
