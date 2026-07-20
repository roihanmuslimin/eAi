# eAi Browser

A lightweight Android browser with a custom **extension system** (ala userscript / Tampermonkey).
Upload a JS/CSS file + a simple manifest (URL match pattern like `*.tokopedia.com/*`) and the
script is automatically injected into matching pages.

Use it for: simple ad-blocking, forced dark mode, auto-fill, restyling websites, and more.

## Features
- Minimal, elegant WebView browser
- Custom extension manager: upload `.js` / `.css` + manifest
- Match patterns: `*.example.com/*`, `https://*.example.com/*`, `*://*/*`, etc.
- Scripts injected automatically on page load
- Built via GitHub Actions → APK downloadable from the Actions tab

## Build APK
The APK is built automatically by GitHub Actions on every push.
Download it from the **Actions** tab of the repository → pick the latest run → download the `app-debug` artifact.

## Extension manifest format
Create a manifest JSON alongside your script:

```json
{
  "name": "Tokopedia Dark Mode",
  "version": "1.0",
  "match": "*.tokopedia.com/*",
  "run_at": "document_end",
  "type": "css"
}
```

| Field     | Description                                                   |
|-----------|---------------------------------------------------------------|
| `name`    | Display name of the extension                                 |
| `version` | Version string                                                |
| `match`   | URL match pattern (supports `*` wildcards)                    |
| `run_at`  | `document_start` or `document_end` (default)                  |
| `type`    | `js` or `css`                                                 |

## License
MIT
