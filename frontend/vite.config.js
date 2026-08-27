import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // sockjs-client Node'daki "global" degiskenini bekliyor;
  // tarayicida karsiligi globalThis — tanimlanmazsa "global is not defined" hatasi verir
  define: { global: 'globalThis' },
})
