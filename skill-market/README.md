# skill-market

Independent skill package catalog service for Ops Factory.

Current scope:

- Create simple text-based skills
- Import complete skill ZIP packages
- Validate package structure and archive safety
- Store skill metadata and ZIP packages
- Expose skill metadata and package download APIs

The service does not install skills into agents. Gateway owns agent skill installation by pulling packages from `skill-market` and writing agent configuration.

Optional local catalog entries can be stored under `data/skills` as versioned skill packages. Each skill directory contains `metadata.yaml`, `package.zip`, and `unpacked/SKILL.md`; when present, the service reads this directory as the catalog source. An empty catalog is valid and does not affect core platform startup.
