import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    proxy: {
      '/patient-api': {
        target: 'http://localhost:9010',
        secure: false,
        rewrite: (path) => path.replace(/^\/patient-api/, '')
      },
      '/appointment-api': {
        target: 'http://localhost:9020',
        secure: false,
        rewrite: (path) => path.replace(/^\/appointment-api/, '')
      },
      '/provider-api': {
        target: 'http://localhost:9040',
        secure: false,
        rewrite: (path) => path.replace(/^\/provider-api/, '')
      },
      '/dispatcher-api': {
        target: 'http://localhost:9030',
        secure: false,
        rewrite: (path) => path.replace(/^\/dispatcher-api/, '')
      }
    }
  }
});
