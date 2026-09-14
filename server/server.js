#!/usr/bin/env node
/*
 * Radio Shuddhodhan — live backend (zero-dependency Node server).
 *
 * One server does three jobs so that the app and the website stay in sync:
 *   1. Serves the web companion (static files from ../web).
 *   2. Implements the REST API the Android app already speaks
 *      (see docs/BACKEND_API.md and ApiService.kt).
 *   3. Broadcasts every admin change over Server-Sent Events
 *      (GET /api/v1/events) so the app and every open website update
 *      the moment the admin saves something.
 *
 * Data is persisted to ./data.json (auto-seeded on first run).
 */
'use strict';

const http = require('http');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const PORT = parseInt(process.env.PORT || '8080', 10);
const HOST = '0.0.0.0';
const WEB_ROOT = path.join(__dirname, '..', 'web');
const DATA_FILE = path.join(__dirname, 'data.json');
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'shuddhodhan951';

/* ------------------------------------------------------------------ */
/* Data store                                                          */
/* ------------------------------------------------------------------ */

const DAY = 86400000;

function defaultConfig() {
  return {
    liveRadioEnabled: true, newsEnabled: true, calendarEnabled: true,
    helpdeskEnabled: true, postsEnabled: true, stationsEnabled: true,
    socialEnabled: true, maintenanceMode: false,
    featuredStationId: 'st-shuddhodhan',
    primaryStreamUrl: 'http://stream.hamropatro.com/8483',
    primaryStationName: 'Radio Shuddhodhan 95.1 MHz',
    homeBannerText: '', homeBannerTextNe: '',
    currentProgram: '', currentProgramNe: '',
    contactPhone: '+977 984-7036945',
    contactEmail: 'Radiosuddhodhan95.1@gmail.com',
    contactWhatsapp: '+977 984-7036945',
    contactWebsite: 'https://www.facebook.com/share/1BjjUPuPdx/',
    aboutText: '', aboutTextNe: '', appLogoUrl: '',
    googleLoginEnabled: false, facebookLoginEnabled: false, phoneLoginEnabled: true,
    stationFrequency: '95.1 MHz',
    stationAddress: 'Shuddhodhan-4, Pharsatikar, Rupandehi, Nepal',
    stationAddressNe: 'शुद्धोधन-४, फर्साटिकर, रूपन्देही, नेपाल',
    operatorText: 'Operated by Shuddhodhan Multimedia',
    operatorTextNe: 'शुद्धोधन मल्टिमिडियाद्वारा सञ्चालित',
    taglineNe: 'हरेक नेपालीको मन',
    taglineSubNe: 'रेडियो शुद्धोधन 95.1 मेगाहर्ज',
    taglineEn: "In every Nepali's heart",
    taglineSubEn: 'Radio Shuddhodhan 95.1 MHz',
    teamMembers: [
      { roleKey: 'manager', role: 'Station Manager', roleNe: 'स्टेशन प्रमुख', name: 'Ravi Rana', contact: '', sortOrder: 0 },
      { roleKey: 'technician', role: 'Technician', roleNe: 'प्राविधिक', name: '', contact: '', sortOrder: 1 },
      { roleKey: 'marketing', role: 'Marketing Manager', roleNe: 'मार्केटिङ प्रमुख', name: '', contact: '', sortOrder: 2 }
    ],
    updatedAt: Date.now()
  };
}

function seed() {
  const now = Date.now();
  const stations = [
    { id: 'st-shuddhodhan', name: 'Radio Shuddhodhan 95.1 MHz', nameNe: 'रेडियो शुद्धोधन 95.1 मेगाहर्ज', description: 'तपाईंको समुदायको आवाज — समाचार, संगीत र जानकारी। The voice of your community.', streamUrl: 'http://stream.hamropatro.com/8483', logoUrl: null, isEnabled: true, isFeatured: true, sortOrder: 0 },
    { id: 'st-1', name: 'Groove Salad (Demo)', nameNe: 'ग्रुभ सलाड (डेमो)', description: 'शान्त संगीत। Sample ambient station.', streamUrl: 'https://ice1.somafm.com/groovesalad-128-mp3', logoUrl: null, isEnabled: true, isFeatured: false, sortOrder: 1 },
    { id: 'st-2', name: 'Indie Pop (Demo)', nameNe: 'इन्डी पप (डेमो)', description: 'इन्डी संगीत। Sample indie station.', streamUrl: 'https://ice1.somafm.com/indiepop-128-mp3', logoUrl: null, isEnabled: true, isFeatured: false, sortOrder: 2 },
    { id: 'st-3', name: 'Secret Agent (Demo)', nameNe: 'सिक्रेट एजेन्ट (डेमो)', description: 'लाउन्ज संगीत। Sample lounge station.', streamUrl: 'https://ice2.somafm.com/secretagent-128-mp3', logoUrl: null, isEnabled: true, isFeatured: false, sortOrder: 3 }
  ];
  const news = [
    { id: 'n1', title: 'Radio Shuddhodhan एप आधिकारिक रूपमा सार्वजनिक', summary: 'समुदायको आवाज अब तपाईंको नामा। लाइभ रेडियो, समाचार, नेपाली पात्रो र हेल्पडेस्क एउटै एपमा।', content: 'Radio Shuddhodhan मोबाइल एप आधिकारिक रूपमा सार्वजनिक भएको छ। एपमार्फत लाइभ रेडियो प्रसारण सुन्न, ताजा समाचार पढ्न, विक्रम सम्बत् पात्रो हेर्न र हेल्पडेस्कमा सम्पर्क गर्न सकिन्छ।', category: 'समाचार', imageUrl: null, author: 'Radio Shuddhodhan', publishedAt: now - DAY, updatedAt: now - DAY, isPublished: true, isFeatured: true, isBreaking: false },
    { id: 'n2', title: 'ब्रेकिङ: स्थानीय समाचार प्रणाली सञ्चालनमा', summary: 'एपमार्फत तत्कालै ब्रेकिङ समाचार सूचना पठाउने प्रणाली सञ्चालनमा आएको छ।', content: 'एडमिनले ब्रेकिङ समाचार पठाउँदा प्रयोगकर्ताको फोनमा तुरुन्तै सूचना देखिने व्यवस्था मिलाइएको छ।', category: 'समाचार', imageUrl: null, author: 'Radio Shuddhodhan', publishedAt: now - DAY / 2, updatedAt: now - DAY / 2, isPublished: true, isFeatured: false, isBreaking: true },
    { id: 'n3', title: 'कार्यक्रम तालिका: साँझको विशेष कुराकानी', summary: 'हरेक साँझ ७ बजे समुदायका विषयमा विशेष कुराकानी कार्यक्रम प्रसारण हुन्छ।', content: "हरेक साँझ ७ बजे 'समुदायको मञ्च' कार्यक्रममा स्थानीय विषयवस्तुमा छलफल हुन्छ।", category: 'कार्यक्रम', imageUrl: null, author: 'Radio Shuddhodhan', publishedAt: now - 2 * DAY, updatedAt: now - 2 * DAY, isPublished: true, isFeatured: false, isBreaking: false },
    { id: 'n4', title: 'मौसम अपडेट र दैनिक जानकारी', summary: 'बिहानको प्रसारणमा मौसम विवरण र दैनिक जानकारी सुन्न पाउनुहुन्छ।', content: 'प्रत्येक बिहान ७ बजेको प्रसारणमा मौसम विवरण र दैनिक जानकारी प्रसारण गरिन्छ।', category: 'जानकारी', imageUrl: null, author: 'Radio Shuddhodhan', publishedAt: now - 3 * DAY, updatedAt: now - 3 * DAY, isPublished: true, isFeatured: false, isBreaking: false }
  ];
  const posts = [
    { id: 'p1', title: 'रेडियोको इतिहास: समुदायसँगको यात्रा', summary: 'Radio Shuddhodhan कसरी समुदायको विश्वसनीय स्रोत बन्यो।', content: 'सुरुवातमा सानो समुदाय रेडियोका रूपमा यात्रा सुरु गरेको Radio Shuddhodhan आज समुदायको घर-घरमा पुगेको छ।', imageUrl: null, author: 'Radio Shuddhodhan', publishedAt: now - 4 * DAY, updatedAt: now - 4 * DAY, isPublished: true, isFeatured: true },
    { id: 'p2', title: 'स्वयंसेवक खोजिँदै', summary: 'रेडियो कार्यक्रममा सहयोग गर्न इच्छुक स्वयंसेवकहरूका लागि अवसर।', content: 'कार्यक्रम निर्माण, प्रस्तुति र प्राविधिक काममा सहयोग गर्न इच्छुक स्वयंसेवकहरूले हेल्पडेस्कमार्फत सम्पर्क गर्नुहोस्।', imageUrl: null, author: 'Radio Shuddhodhan', publishedAt: now - 6 * DAY, updatedAt: now - 6 * DAY, isPublished: true, isFeatured: false }
  ];
  function plusDays(n) { const d = new Date(); d.setDate(d.getDate() + n); return d.toISOString().slice(0, 10); }
  const events = [
    { id: 'e1', title: 'समुदाय संगीत रात', titleNe: 'समुदाय संगीत रात', description: 'रेडियो स्टुडियोबाट प्रत्यक्ष प्रसारण।', adDate: plusDays(3), timeLabel: '19:00', location: 'Radio Shuddhodhan Studio', isFeatured: true },
    { id: 'e2', title: 'स्वास्थ्य जागरण कार्यक्रम', titleNe: 'स्वास्थ्य जागरण कार्यक्रम', description: 'स्वास्थ्य सम्बन्धी जानकारी।', adDate: plusDays(10), timeLabel: '10:00', location: 'Radio Shuddhodhan Studio', isFeatured: false },
    { id: 'e3', title: 'विद्यालय छलफल', titleNe: 'विद्यालय छलफल', description: 'स्थानीय विद्यालयसँग छलफल।', adDate: plusDays(21), timeLabel: '14:00', location: 'Radio Shuddhodhan Studio', isFeatured: false }
  ];
  const announcements = [
    { id: 'a1', title: 'नयाँ एप र वेबसाइट सार्वजनिक', message: 'Radio Shuddhodhan एप र वेबसाइट अब उपलब्ध छ। सबैलाई स्वागत छ!', createdAt: now, activeUntil: now + 30 * DAY, isActive: true }
  ];
  const socialLinks = [
    { id: 'soc-fb', platform: 'facebook', label: 'Facebook', url: 'https://www.facebook.com/share/1BjjUPuPdx/', sortOrder: 0, isEnabled: true },
    { id: 'soc-fblive', platform: 'facebook_live', label: 'Facebook Live', url: 'https://www.facebook.com/share/1BjjUPuPdx/', sortOrder: 1, isEnabled: true },
    { id: 'soc-yt', platform: 'youtube', label: 'YouTube', url: 'https://www.youtube.com/', sortOrder: 2, isEnabled: true },
    { id: 'soc-web', platform: 'website', label: 'Website', url: 'https://www.facebook.com/share/1BjjUPuPdx/', sortOrder: 3, isEnabled: true }
  ];
  return { config: defaultConfig(), news, posts, stations, events, announcements, socialLinks, notifications: [], helpdesk: [], users: [], adminToken: crypto.randomBytes(16).toString('hex') };
}

let db;
function loadDb() {
  try {
    db = JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
  } catch (e) {
    db = seed();
    persist();
  }
}
function persist() {
  try { fs.writeFileSync(DATA_FILE, JSON.stringify(db, null, 2)); } catch (e) { /* ignore */ }
}

/* ------------------------------------------------------------------ */
/* Live broadcast (SSE)                                                */
/* ------------------------------------------------------------------ */

const clients = new Set();
function broadcast(type, id) {
  const payload = 'event: change\ndata: ' + JSON.stringify({ type: type, id: id || null, ts: Date.now() }) + '\n\n';
  for (const res of clients) {
    try { res.write(payload); } catch (e) { /* ignore */ }
  }
}

/* ------------------------------------------------------------------ */
/* HTTP helpers                                                        */
/* ------------------------------------------------------------------ */

function sendJson(res, status, obj) {
  const body = JSON.stringify(obj);
  res.writeHead(status, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Cache-Control': 'no-store'
  });
  res.end(body);
}

function readBody(req) {
  return new Promise((resolve) => {
    let data = '';
    req.on('data', (c) => { data += c; if (data.length > 1e7) req.destroy(); });
    req.on('end', () => {
      try { resolve(data ? JSON.parse(data) : {}); } catch (e) { resolve({}); }
    });
    req.on('error', () => resolve({}));
  });
}

function isAdmin(req) {
  const h = req.headers['authorization'] || '';
  return h === 'Bearer ' + db.adminToken;
}

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.json': 'application/json',
  '.ico': 'image/x-icon'
};

function serveStatic(req, res, pathname) {
  let p = pathname === '/' ? '/index.html' : pathname;
  const file = path.normalize(path.join(WEB_ROOT, p));
  if (!file.startsWith(WEB_ROOT)) { sendJson(res, 403, { ok: false, message: 'forbidden' }); return; }
  fs.readFile(file, (err, data) => {
    if (err) { sendJson(res, 404, { ok: false, message: 'not found' }); return; }
    res.writeHead(200, { 'Content-Type': MIME[path.extname(file).toLowerCase()] || 'application/octet-stream', 'Cache-Control': 'no-cache' });
    res.end(data);
  });
}

function upsert(list, item) {
  const i = list.findIndex((x) => x.id === item.id);
  if (i >= 0) list[i] = Object.assign({}, list[i], item);
  else list.push(item);
}

/* ------------------------------------------------------------------ */
/* Router                                                              */
/* ------------------------------------------------------------------ */

const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, 'http://localhost');
  const p = url.pathname;
  const seg = p.split('/').filter(Boolean); // e.g. ['api','v1','news']

  if (req.method === 'OPTIONS') { sendJson(res, 200, {}); return; }

  // ---- SSE ----
  if (p === '/api/v1/events') {
    res.writeHead(200, {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive',
      'Access-Control-Allow-Origin': '*'
    });
    res.write(': connected\n\n');
    clients.add(res);
    req.on('close', () => clients.delete(res));
    return;
  }

  // ---- API ----
  if (seg[0] === 'api') {
    try { await api(req, res, seg, url); } catch (e) { sendJson(res, 500, { ok: false, message: String(e && e.message || e) }); }
    return;
  }

  // ---- Static web app ----
  serveStatic(req, res, p);
});

async function api(req, res, seg, url) {
  // seg = ['api','v1', ...]
  const base = seg[2]; // config | news | posts | stations | events | announcements | social-links | notifications | helpdesk | auth | admin | devices
  const id = seg[3];

  if (req.method === 'GET' && base === 'config') return sendJson(res, 200, db.config);

  if (base === 'auth') {
    const body = await readBody(req);
    if (req.method === 'POST' && seg[3] === 'register') {
      const user = { id: 'u' + Date.now(), name: body.name || '', email: body.email || null, phone: body.phone || null, password: body.password || '', token: crypto.randomBytes(16).toString('hex'), createdAt: Date.now() };
      db.users.push(user); persist();
      return sendJson(res, 200, { token: user.token, userId: user.id, name: user.name, email: user.email, phone: user.phone });
    }
    if (req.method === 'POST' && seg[3] === 'login') {
      const u = db.users.find((x) => (body.email && x.email === body.email) || (body.phone && x.phone === body.phone));
      if (!u || u.password !== body.password) return sendJson(res, 401, { ok: false, message: 'invalid credentials' });
      return sendJson(res, 200, { token: u.token, userId: u.id, name: u.name, email: u.email, phone: u.phone });
    }
  }

  if (base === 'devices') {
    if (req.method === 'POST' && seg[3] === 'register') { return sendJson(res, 200, { ok: true }); }
  }

  // ---- Public content reads ----
  if (req.method === 'GET') {
    if (base === 'news') {
      if (id && id !== 'listeners') { const n = db.news.find((x) => x.id === id); return n ? sendJson(res, 200, n) : sendJson(res, 404, { ok: false }); }
      return sendJson(res, 200, db.news.filter((x) => x.isPublished).sort((a, b) => b.publishedAt - a.publishedAt));
    }
    if (base === 'posts') { return sendJson(res, 200, db.posts.filter((x) => x.isPublished).sort((a, b) => b.publishedAt - a.publishedAt)); }
    if (base === 'stations') {
      if (seg[4] === 'listeners' && id) {
        const s = db.stations.find((x) => x.id === id);
        return sendJson(res, 200, { stationId: id, count: s ? (Math.floor(Math.random() * 40) + 12) : 0, updatedAt: Date.now() });
      }
      return sendJson(res, 200, db.stations.filter((x) => x.isEnabled).sort((a, b) => a.sortOrder - b.sortOrder));
    }
    if (base === 'events') { return sendJson(res, 200, db.events.sort((a, b) => a.adDate.localeCompare(b.adDate))); }
    if (base === 'announcements') { return sendJson(res, 200, db.announcements.filter((x) => x.isActive && x.activeUntil >= Date.now())); }
    if (base === 'social-links') { return sendJson(res, 200, db.socialLinks.filter((x) => x.isEnabled).sort((a, b) => a.sortOrder - b.sortOrder)); }
    if (base === 'notifications') { return sendJson(res, 200, db.notifications); }
  }

  if (base === 'helpdesk') {
    if (req.method === 'POST' && !id) {
      const body = await readBody(req);
      const ticket = { id: 't' + Date.now(), name: body.name, contact: body.contact, category: body.category, message: body.message, createdAt: Date.now(), status: 'open', reply: null, repliedAt: null };
      db.helpdesk.unshift(ticket); persist(); broadcast('helpdesk', ticket.id);
      return sendJson(res, 200, { ok: true, message: 'received' });
    }
  }

  // ---- Admin ----
  if (base === 'admin') {
    const action = seg[3];
    if (req.method === 'POST' && action === 'login') {
      const body = await readBody(req);
      if (body.password === ADMIN_PASSWORD) return sendJson(res, 200, { token: db.adminToken, isAdmin: true });
      return sendJson(res, 401, { ok: false, message: 'invalid admin password' });
    }
    if (!isAdmin(req)) return sendJson(res, 401, { ok: false, message: 'unauthorized' });

    // config
    if (action === 'config' && req.method === 'PUT') {
      const body = await readBody(req);
      db.config = Object.assign({}, db.config, body, { updatedAt: Date.now() });
      persist(); broadcast('config'); return sendJson(res, 200, db.config);
    }
    // notifications
    if (action === 'notifications' && req.method === 'POST') {
      const body = await readBody(req);
      const n = { id: 'nt' + Date.now(), title: body.title, body: body.body, type: body.type || 'general', receivedAt: Date.now(), isRead: false, dataId: null };
      db.notifications.unshift(n); persist(); broadcast('notification', n.id);
      return sendJson(res, 200, { ok: true });
    }
    // ai generate (stub — no real OpenAI call in this self-hosted server)
    if (action === 'ai' && seg[4] === 'generate' && req.method === 'POST') {
      const body = await readBody(req);
      const output = '(AI) ' + (body.action || 'draft') + ': ' + (body.input || '').slice(0, 120);
      return sendJson(res, 200, { output });
    }
    // helpdesk inbox
    if (action === 'helpdesk') {
      if (req.method === 'GET') return sendJson(res, 200, db.helpdesk);
      if (req.method === 'PUT' && seg[4]) {
        const body = await readBody(req);
        const t = db.helpdesk.find((x) => x.id === seg[4]);
        if (t) { t.status = body.status || t.status; t.reply = body.reply != null ? body.reply : t.reply; t.repliedAt = body.repliedAt || Date.now(); persist(); broadcast('helpdesk', t.id); }
        return sendJson(res, 200, t || { ok: false });
      }
    }

    // generic content CRUD: admin/news, admin/posts, admin/stations, admin/events, admin/announcements, admin/social-links
    const table = { news: 'news', posts: 'posts', stations: 'stations', events: 'events', announcements: 'announcements', 'social-links': 'socialLinks' }[action];
    if (table) {
      const list = db[table];
      if (req.method === 'GET') return sendJson(res, 200, list);
      if (req.method === 'DELETE' && seg[4]) {
        const i = list.findIndex((x) => x.id === seg[4]);
        if (i >= 0) list.splice(i, 1);
        persist(); broadcast(action, seg[4]);
        return sendJson(res, 200, { ok: true });
      }
      if (req.method === 'POST' || req.method === 'PUT') {
        const body = await readBody(req);
        const item = Object.assign({}, body);
        if (!item.id) item.id = crypto.randomBytes(6).toString('hex');
        if (table === 'news' || table === 'posts') item.updatedAt = Date.now();
        upsert(list, item);
        persist(); broadcast(action, item.id);
        return sendJson(res, 200, item);
      }
    }
  }

  sendJson(res, 404, { ok: false, message: 'not found' });
}

loadDb();
server.listen(PORT, HOST, () => {
  console.log('Radio Shuddhodhan backend listening on http://' + HOST + ':' + PORT);
  console.log('Web app:  http://localhost:' + PORT + '/');
  console.log('Admin password: "' + ADMIN_PASSWORD + '"  (admin login at /admin)');
});
