import { useState, useEffect } from 'react'
import Card from '../components/Card'
import styles from './HomeScreen.module.css'

const QUICK_ACTIONS = [
  { title: 'Voice Mode', subtitle: 'Talk to VEDRA', icon: '🎤', color: '#8B5CF6', tab: 'ved' },
  { title: 'Quick Actions', subtitle: 'Run commands', icon: '⚡', color: '#06B6D4', tab: 'actions' },
  { title: 'Study Hub', subtitle: 'Focus & learn', icon: '📚', color: '#10B981', tab: 'memory' },
  { title: 'Notifications', subtitle: 'View alerts', icon: '🔔', color: '#EC4899', tab: 'ved' },
]

export default function HomeScreen({ onNavigate }) {
  const [time, setTime] = useState(new Date())
  const [greeting, setGreeting] = useState('')

  useEffect(() => {
    const h = new Date().getHours()
    if (h < 12) setGreeting('Good morning')
    else if (h < 18) setGreeting('Good afternoon')
    else setGreeting('Good evening')
    const t = setInterval(() => setTime(new Date()), 1000)
    return () => clearInterval(t)
  }, [])

  const fmt = (d) => d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  const fmtDate = (d) => d.toLocaleDateString([], { weekday: 'long', month: 'long', day: 'numeric' })

  return (
    <div className={styles.container}>
      {/* Hero */}
      <div className={styles.hero}>
        <div className={styles.pulse} />
        <div className={styles.clock}>{fmt(time)}</div>
        <div className={styles.date}>{fmtDate(time)}</div>
        <div className={styles.greeting}>{greeting} — I'm VEDRA</div>
      </div>

      {/* Status row */}
      <div className={styles.statusRow}>
        <Card className={styles.statusCard}>
          <div className={styles.statusIcon} style={{ color: '#10B981' }}>🔋</div>
          <div className={styles.statusLabel}>Battery</div>
          <div className={styles.statusVal}>Good</div>
        </Card>
        <Card className={styles.statusCard}>
          <div className={styles.statusIcon} style={{ color: '#06B6D4' }}>💾</div>
          <div className={styles.statusLabel}>Storage</div>
          <div className={styles.statusVal}>Available</div>
        </Card>
        <Card className={styles.statusCard}>
          <div className={styles.statusIcon} style={{ color: '#8B5CF6' }}>🌐</div>
          <div className={styles.statusLabel}>Network</div>
          <div className={styles.statusVal}>Online</div>
        </Card>
      </div>

      {/* Quick actions */}
      <div className={styles.sectionTitle}>Quick Access</div>
      <div className={styles.actions}>
        {QUICK_ACTIONS.map(a => (
          <Card key={a.title} className={styles.actionCard} style={{ cursor: 'pointer' }}>
            <button className={styles.actionBtn} onClick={() => onNavigate(a.tab)}>
              <div className={styles.actionIconWrap} style={{ background: a.color + '22', borderColor: a.color + '44' }}>
                <span className={styles.actionIcon}>{a.icon}</span>
              </div>
              <div className={styles.actionInfo}>
                <div className={styles.actionTitle} style={{ color: '#F3F4F6' }}>{a.title}</div>
                <div className={styles.actionSub}>{a.subtitle}</div>
              </div>
              <span style={{ color: a.color, fontSize: 14 }}>›</span>
            </button>
          </Card>
        ))}
      </div>

      {/* AI tip */}
      <Card glow className={styles.tipCard}>
        <div className={styles.tipHeader}>
          <span>💡</span>
          <span className={styles.tipTitle}>VEDRA Tip</span>
        </div>
        <div className={styles.tipText}>
          Try asking VED anything — I can answer questions, write content, summarize info, and much more using Gemini AI.
        </div>
        <button className={styles.tipBtn} onClick={() => onNavigate('ved')}>
          Open VED Chat →
        </button>
      </Card>
    </div>
  )
}
