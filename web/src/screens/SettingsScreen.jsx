import { useState } from 'react'
import Card from '../components/Card'
import styles from './SettingsScreen.module.css'

export default function SettingsScreen() {
  const [voice, setVoice] = useState(true)
  const [notifications, setNotifications] = useState(true)
  const [offline, setOffline] = useState(false)
  const [theme, setTheme] = useState('dark')
  const [model, setModel] = useState('gemini-2.0-flash')
  const [apiKey, setApiKey] = useState('')

  const Toggle = ({ value, onChange }) => (
    <button
      className={`${styles.toggle} ${value ? styles.toggleOn : ''}`}
      onClick={() => onChange(!value)}
    >
      <span className={styles.thumb} />
    </button>
  )

  return (
    <div className={styles.container}>
      {/* Profile */}
      <Card className={styles.profileCard}>
        <div className={styles.avatar}>V</div>
        <div>
          <div className={styles.profileName}>VEDRA</div>
          <div className={styles.profileSub}>Personal AI Assistant · v1.0</div>
        </div>
      </Card>

      {/* AI Settings */}
      <div className={styles.section}>
        <div className={styles.sectionLabel}>AI Configuration</div>
        <Card className={styles.settingCard}>
          <div className={styles.settingRow}>
            <div className={styles.settingInfo}>
              <div className={styles.settingTitle}>AI Model</div>
              <div className={styles.settingDesc}>Gemini model for responses</div>
            </div>
            <select
              className={styles.select}
              value={model}
              onChange={e => setModel(e.target.value)}
            >
              <option value="gemini-2.0-flash">Gemini 2.0 Flash</option>
              <option value="gemini-1.5-flash">Gemini 1.5 Flash</option>
              <option value="gemini-1.5-pro">Gemini 1.5 Pro</option>
            </select>
          </div>
        </Card>
        <Card className={styles.settingCard}>
          <div className={styles.settingInfo}>
            <div className={styles.settingTitle}>Custom API Key</div>
            <div className={styles.settingDesc}>Override default key (optional)</div>
          </div>
          <input
            type="password"
            className={styles.keyInput}
            placeholder="AIza..."
            value={apiKey}
            onChange={e => setApiKey(e.target.value)}
          />
        </Card>
      </div>

      {/* Features */}
      <div className={styles.section}>
        <div className={styles.sectionLabel}>Features</div>
        {[
          { label: 'Voice Mode', desc: 'Enable speech recognition', val: voice, set: setVoice },
          { label: 'Notifications', desc: 'Background alerts', val: notifications, set: setNotifications },
          { label: 'Offline Mode', desc: 'Basic offline responses', val: offline, set: setOffline },
        ].map(s => (
          <Card key={s.label} className={styles.settingCard}>
            <div className={styles.settingRow}>
              <div className={styles.settingInfo}>
                <div className={styles.settingTitle}>{s.label}</div>
                <div className={styles.settingDesc}>{s.desc}</div>
              </div>
              <Toggle value={s.val} onChange={s.set} />
            </div>
          </Card>
        ))}
      </div>

      {/* About */}
      <div className={styles.section}>
        <div className={styles.sectionLabel}>About</div>
        <Card className={styles.aboutCard}>
          <div className={styles.aboutRow}><span>Version</span><span>1.0.0</span></div>
          <div className={styles.divider} />
          <div className={styles.aboutRow}><span>Powered by</span><span style={{ color: '#06B6D4' }}>Gemini AI</span></div>
          <div className={styles.divider} />
          <div className={styles.aboutRow}><span>Build</span><span>Web Preview</span></div>
        </Card>
      </div>
    </div>
  )
}
