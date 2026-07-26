import { useState, useRef, useEffect } from 'react'
import styles from './VedScreen.module.css'

const SUGGESTIONS = [
  'What can you do?',
  'Tell me a fun fact',
  'Write a short poem',
  'Explain quantum computing simply',
]

async function askGemini(messages) {
  const res = await fetch('/api/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ messages }),
  })

  const data = await res.json()
  if (!res.ok) throw new Error(data.error || `HTTP ${res.status}`)
  return data.text
}

export default function VedScreen({ pendingCommand, onPendingCommandConsumed }) {
  const [messages, setMessages] = useState([
    { id: '0', sender: 'VEDRA', text: "Hello! I'm VEDRA, your personal AI assistant powered by Gemini. How can I help you today?" },
  ])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const bottomRef = useRef(null)

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, loading])

  // Auto-send command arriving from Actions screen
  useEffect(() => {
    if (pendingCommand) {
      onPendingCommandConsumed?.()
      send(pendingCommand)
    }
  }, [pendingCommand])

  const send = async (text) => {
    const t = (text ?? input).trim()
    if (!t || loading) return
    setInput('')

    const userMsg = { id: Date.now().toString(), sender: 'USER', text: t }
    const updated = (prev) => [...prev, userMsg]
    setMessages(updated)
    setLoading(true)

    let snapshot
    setMessages(prev => {
      snapshot = [...prev, userMsg]
      return snapshot
    })

    try {
      const reply = await askGemini(snapshot ?? [userMsg])
      setMessages(prev => [...prev, { id: Date.now() + '_ai', sender: 'VEDRA', text: reply }])
    } catch (e) {
      setMessages(prev => [...prev, { id: Date.now() + '_err', sender: 'VEDRA', text: `⚠️ ${e.message}` }])
    } finally {
      setLoading(false)
    }
  }

  const copy = (text) => navigator.clipboard.writeText(text).catch(() => {})

  return (
    <div className={styles.container}>
      <div className={styles.messages}>
        {messages.map(msg => (
          <div key={msg.id} className={`${styles.msgRow} ${msg.sender === 'USER' ? styles.userRow : styles.aiRow}`}>
            {msg.sender === 'VEDRA' && <div className={styles.avatar}>V</div>}
            <div className={`${styles.bubble} ${msg.sender === 'USER' ? styles.userBubble : styles.aiBubble}`}>
              <div className={styles.msgText}>{msg.text}</div>
              {msg.sender === 'VEDRA' && (
                <button className={styles.copyBtn} onClick={() => copy(msg.text)} title="Copy">📋</button>
              )}
            </div>
          </div>
        ))}

        {loading && (
          <div className={`${styles.msgRow} ${styles.aiRow}`}>
            <div className={styles.avatar}>V</div>
            <div className={`${styles.bubble} ${styles.aiBubble}`}>
              <div className={styles.typing}><span /><span /><span /></div>
            </div>
          </div>
        )}
        <div ref={bottomRef} />
      </div>

      {messages.length <= 1 && (
        <div className={styles.suggestions}>
          {SUGGESTIONS.map(s => (
            <button key={s} className={styles.suggestion} onClick={() => send(s)}>{s}</button>
          ))}
        </div>
      )}

      <div className={styles.inputArea}>
        <input
          className={styles.input}
          placeholder="Ask VEDRA anything..."
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && !e.shiftKey && send()}
          disabled={loading}
        />
        <button
          className={styles.sendBtn}
          onClick={() => send()}
          disabled={!input.trim() || loading}
        >➤</button>
      </div>
    </div>
  )
}
