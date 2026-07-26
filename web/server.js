import express from 'express'
import rateLimit from 'express-rate-limit'

const app = express()
app.use(express.json())

// Internal token shared with the Vite proxy. Any request lacking this
// header could not have come through the proxy and is rejected.
const INTERNAL_TOKEN = process.env.INTERNAL_API_TOKEN || 'vedra-internal'

app.use((req, res, next) => {
  if (req.headers['x-internal-token'] !== INTERNAL_TOKEN) {
    return res.status(403).json({ error: 'Forbidden' })
  }
  next()
})

// Rate-limit: max 30 requests per minute per IP
const limiter = rateLimit({
  windowMs: 60 * 1000,
  max: 30,
  standardHeaders: true,
  legacyHeaders: false,
  message: { error: 'Too many requests — please slow down.' },
})

const GEMINI_API_KEY = process.env.GEMINI_API_KEY
const GEMINI_URL =
  'https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent'

app.post('/api/chat', limiter, async (req, res) => {
  if (!GEMINI_API_KEY) {
    return res.status(500).json({ error: 'GEMINI_API_KEY not configured on server.' })
  }

  const { messages } = req.body
  if (!Array.isArray(messages) || messages.length === 0) {
    return res.status(400).json({ error: 'Invalid messages array.' })
  }

  try {
    const contents = messages.map(m => ({
      role: m.sender === 'USER' ? 'user' : 'model',
      parts: [{ text: String(m.text) }],
    }))

    const response = await fetch(`${GEMINI_URL}?key=${GEMINI_API_KEY}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents,
        systemInstruction: {
          parts: [{ text: 'You are VEDRA (VED), a personal AI assistant. Be helpful, concise, and friendly.' }],
        },
        generationConfig: { maxOutputTokens: 800 },
      }),
    })

    if (!response.ok) {
      const err = await response.json().catch(() => ({}))
      return res.status(response.status).json({ error: err?.error?.message || `Gemini error ${response.status}` })
    }

    const data = await response.json()
    const text = data.candidates?.[0]?.content?.parts?.[0]?.text || 'No response.'
    res.json({ text })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// Bind to loopback only — not reachable externally
const PORT = 3001
app.listen(PORT, '127.0.0.1', () =>
  console.log(`VEDRA API server on 127.0.0.1:${PORT} (internal only)`)
)
