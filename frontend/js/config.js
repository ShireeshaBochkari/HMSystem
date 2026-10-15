// API base URL.
// - Deployed on Render (frontend served by the backend): same origin -> ''.
// - Local dev with `python3 -m http.server 3000` or file://: backend on :8080.
// - Separate static host: set window.API_BASE_OVERRIDE before this script, or edit REMOTE below.
const REMOTE = '';   // e.g. 'https://my-hostel-api.onrender.com' if frontend is hosted separately
const isLocalStatic = location.protocol === 'file:' || ((location.hostname === 'localhost' || location.hostname === '127.0.0.1') && location.port !== '8080' && location.port !== '');
window.API_BASE = window.API_BASE_OVERRIDE ?? (REMOTE || (isLocalStatic ? 'http://localhost:8080' : ''));
