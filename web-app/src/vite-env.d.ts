/// <reference types="vite/client" />

declare const process: {
    env: {
        GOOSED_BASE_URL: string
        GOOSED_SECRET_KEY: string
        GOOSE_WORKING_DIR: string
    }
}
