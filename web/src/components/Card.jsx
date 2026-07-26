import styles from './Card.module.css'

export default function Card({ children, style, className = '', glow }) {
  return (
    <div className={`${styles.card} ${glow ? styles.glow : ''} ${className}`} style={style}>
      {children}
    </div>
  )
}
