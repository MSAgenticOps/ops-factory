# Onboarding

## Local Prerequisites
- Node.js 18+
- Java 21+
- Maven
- `goosed` on `PATH`
- Docker for optional services

## Recommended Startup
Use the root orchestrator for normal development:

```bash
./scripts/ctl.sh startup all
./scripts/ctl.sh status
```

For a lighter loop, start only the required services:

```bash
./scripts/ctl.sh startup gateway webapp
```

## First Checks
- Confirm `gateway/config.yaml` and `web-app/config.json` are valid for your environment.
- When editing service config, treat the checked-in `*.example` file as the shared contract: keep its key paths consistent with the code and keep local-only values in the untracked runtime file.
- Use `test/` for cross-service validation, not ad hoc one-off scripts.
- Read `AGENTS.md` and the docs in `architecture/` and `development/` before large changes.
