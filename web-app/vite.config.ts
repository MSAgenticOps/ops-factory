import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), '')

    // Check required environment variables
    const requiredEnvVars = ['GOOSED_BASE_URL', 'GOOSED_SECRET_KEY']
    const missingEnvVars = requiredEnvVars.filter(key => !env[key])

    if (missingEnvVars.length > 0) {
        console.error('\n❌ Missing required environment variables:\n')
        missingEnvVars.forEach(key => {
            console.error(`   - ${key}`)
        })
        console.error('\n📝 Please create a .env file in web-app/ with:\n')
        console.error('   GOOSED_BASE_URL=http://127.0.0.1:3000')
        console.error('   GOOSED_SECRET_KEY=your-secret-key')
        console.error('\n   Or export them in your shell before running npm run dev\n')
        process.exit(1)
    }

    return {
        plugins: [react()],
        define: {
            'process.env.GOOSED_BASE_URL': JSON.stringify(env.GOOSED_BASE_URL),
            'process.env.GOOSED_SECRET_KEY': JSON.stringify(env.GOOSED_SECRET_KEY),
            'process.env.GOOSE_WORKING_DIR': JSON.stringify(env.GOOSE_WORKING_DIR || '~'),
        },
        server: {
            port: 5173,
            proxy: {
                // Proxy API requests to goosed server to avoid CORS issues
                '/api': {
                    target: 'http://127.0.0.1:3000',
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, ''),
                },
            },
        },
    }
})
