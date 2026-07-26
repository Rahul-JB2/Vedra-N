import { useState } from 'react'
import Card from '../components/Card'
import styles from './MemoryScreen.module.css'

const INITIAL_MEMORIES = [
  { id: '1', title: 'Preferred greeting', content: 'User prefers "Good morning" style greetings', tag: 'Preference', color: '#8B5CF6' },
  { id: '2', title: 'AI assistant name', content: 'The AI assistant is called VEDRA or VED', tag: 'Info', color: '#06B6D4' },
]

export default function MemoryScreen() {
  const [memories, setMemories] = useState(INITIAL_MEMORIES)
  const [newTitle, setNewTitle] = useState('')
  const [newContent, setNewContent] = useState('')
  const [showAdd, setShowAdd] = useState(false)
  const [search, setSearch] = useState('')

  const add = () => {
    if (!newTitle.trim() || !newContent.trim()) return
    setMemories(prev => [...prev, {
      id: Date.now().toString(),
      title: newTitle.trim(),
      content: newContent.trim(),
      tag: 'Custom',
      color: '#10B981',
    }])
    setNewTitle('')
    setNewContent('')
    setShowAdd(false)
  }

  const del = (id) => setMemories(prev => prev.filter(m => m.id !== id))

  const filtered = memories.filter(m =>
    m.title.toLowerCase().includes(search.toLowerCase()) ||
    m.content.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className={styles.container}>
      <div className={styles.top}>
        <div className={styles.headerRow}>
          <div>
            <div className={styles.title}>Memory Bank</div>
            <div className={styles.sub}>{memories.length} stored memories</div>
          </div>
          <button className={styles.addBtn} onClick={() => setShowAdd(v => !v)}>＋ Add</button>
        </div>
        <input
          className={styles.search}
          placeholder="🔍 Search memories..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      {showAdd && (
        <Card className={styles.addCard}>
          <div className={styles.addLabel}>New Memory</div>
          <input className={styles.input} placeholder="Title" value={newTitle} onChange={e => setNewTitle(e.target.value)} />
          <textarea className={styles.textarea} placeholder="Content / details" value={newContent} onChange={e => setNewContent(e.target.value)} rows={3} />
          <div className={styles.btns}>
            <button className={styles.cancel} onClick={() => setShowAdd(false)}>Cancel</button>
            <button className={styles.save} onClick={add}>Save</button>
          </div>
        </Card>
      )}

      <div className={styles.list}>
        {filtered.length === 0 ? (
          <div className={styles.empty}>
            <div className={styles.emptyIcon}>🧠</div>
            <div className={styles.emptyText}>No memories found</div>
          </div>
        ) : filtered.map(m => (
          <Card key={m.id} className={styles.memCard}>
            <div className={styles.memHeader}>
              <div className={styles.tag} style={{ color: m.color, borderColor: m.color + '44', background: m.color + '11' }}>{m.tag}</div>
              <button className={styles.del} onClick={() => del(m.id)}>✕</button>
            </div>
            <div className={styles.memTitle}>{m.title}</div>
            <div className={styles.memContent}>{m.content}</div>
          </Card>
        ))}
      </div>
    </div>
  )
}
