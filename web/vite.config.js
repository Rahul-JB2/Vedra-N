import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Internal token — injected by the Vite proxy so Express can reject
// any request that did not come through the proxy.
const INTERNAL_TOKEN = process.env.INTERNAL_API_TOKEN || 'vedra-internal'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5000,
    allowedHosts: 'all',
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:3001',
        changeOrigin: true,
        headers: {
          'x-internal-token': INTERNAL_TOKEN,
        },
      },
    },
  },
})
