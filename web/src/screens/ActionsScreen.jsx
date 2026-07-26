import { useState } from 'react'
import Card from '../components/Card'
import styles from './ActionsScreen.module.css'

const DEFAULT_ACTIONS = [
  { title: 'Set an Alarm', subtitle: 'Schedule a wake-up call', icon: '⏰', color: '#8B5CF6', command: 'Set an alarm for 7 AM tomorrow' },
  { title: 'Start Timer', subtitle: 'Countdown timer', icon: '⏱️', color: '#06B6D4', command: 'Start a 5 minute timer' },
  { title: 'Send Message', subtitle: 'Quick text compose', icon: '💬', color: '#10B981', command: 'Help me write a message' },
  { title: 'Play Music', subtitle: 'Open music player', icon: '🎵', color: '#EC4899', command: 'Play some music' },
  { title: 'Take Photo', subtitle: 'Open camera', icon: '📷', color: '#3B82F6', command: 'Open camera' },
  { title: 'Make a Call', subtitle: 'Phone dialer', icon: '📞', color: '#A855F7', command: 'Help me make a call' },
  { title: 'Open Files', subtitle: 'File manager', icon: '📁', color: '#F59E0B', command: 'Open files' },
  { title: 'Scan QR', subtitle: 'QR code reader', icon: '🔍', color: '#06B6D4', command: 'Scan a QR code' },
]

export default function ActionsScreen({ onSendToVed }) {
  const [search, setSearch] = useState('')
  const [customs, setCustoms] = useState([])
  const [newTitle, setNewTitle] = useState('')
  const [newCmd, setNewCmd] = useState('')
  const [showAdd, setShowAdd] = useState(false)

  const filtered = [...DEFAULT_ACTIONS, ...customs].filter(
    a => a.title.toLowerCase().includes(search.toLowerCase()) ||
         a.command.toLowerCase().includes(search.toLowerCase())
  )

  const addCustom = () => {
    if (!newTitle.trim() || !newCmd.trim()) return
    setCustoms(prev => [...prev, {
      title: newTitle.trim(),
      subtitle: 'Custom action',
      icon: '⚡',
      color: '#8B5CF6',
      command: newCmd.trim(),
    }])
    setNewTitle('')
    setNewCmd('')
    setShowAdd(false)
  }

  return (
    <div className={styles.container}>
      <div className={styles.searchRow}>
        <input
          className={styles.search}
          placeholder="🔍 Search actions..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <button className={styles.addBtn} onClick={() => setShowAdd(v => !v)}>＋</button>
      </div>

      {showAdd && (
        <Card className={styles.addCard}>
          <div className={styles.addTitle}>Add Custom Action</div>
          <input
            className={styles.addInput}
            placeholder="Action name"
            value={newTitle}
            onChange={e => setNewTitle(e.target.value)}
          />
          <input
            className={styles.addInput}
            placeholder="Command / description"
            value={newCmd}
            onChange={e => setNewCmd(e.target.value)}
          />
          <div className={styles.addBtns}>
            <button className={styles.cancelBtn} onClick={() => setShowAdd(false)}>Cancel</button>
            <button className={styles.saveBtn} onClick={addCustom}>Save</button>
          </div>
        </Card>
      )}

      <div className={styles.list}>
        {filtered.map((a, i) => (
          <Card key={i} className={styles.actionCard}>
            <button className={styles.action} onClick={() => onSendToVed(a.command)}>
              <div className={styles.iconWrap} style={{ background: a.color + '22', borderColor: a.color + '44' }}>
                <span className={styles.icon}>{a.icon}</span>
              </div>
              <div className={styles.info}>
                <div className={styles.title}>{a.title}</div>
                <div className={styles.sub}>{a.subtitle}</div>
              </div>
              <span style={{ color: a.color, fontSize: 18 }}>›</span>
            </button>
          </Card>
        ))}
      </div>
    </div>
  )
}
