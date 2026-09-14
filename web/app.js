/* Radio Shuddhodhan — web companion (same-to-same with the Android app).
 * No frameworks, no build step. Uses the same logo, colours, calendar data
 * and live stream as the native app. */
(function () {
  'use strict';

  /* ============================ Constants ============================ */
  var STREAM_URL = 'http://stream.hamropatro.com/8483';
  var STATION_NAME = 'Radio Shuddhodhan 95.1 MHz';
  var APP_NAME = 'Radio Shuddhodhan 95.1 MHz';
  var VERSION = '1.0.1';
  var FREQUENCY = '95.1 MHz';
  var ADDRESS = 'Shuddhodhan-4, Pharsatikar, Rupandehi, Nepal';
  var EMAIL = 'Radiosuddhodhan95.1@gmail.com';
  var PHONE = '+977 984-7036945';
  var WHATSAPP = '+977 984-7036945';
  var TAGLINE_NE = 'हरेक नेपालीको मन रेडियो शुद्धोधन 95.1 मेगाहर्ज.';
  var TAGLINE_EN = "In every Nepali's heart — Radio Shuddhodhan 95.1 MHz.";
  var CREATOR = 'Umesh Tharu';

  /* ============================ i18n ============================ */
  function S(ne, en) { return App.lang === 'ne' ? ne : en; }
  var STR = {
    appName: APP_NAME,
    tagline: function () { return App.lang === 'ne' ? TAGLINE_NE : TAGLINE_EN; },
    home: function () { return S('गृह', 'Home'); },
    liveRadio: function () { return S('लाइभ रेडियो', 'Live Radio'); },
    news: function () { return S('समाचार', 'News'); },
    posts: function () { return S('पोस्टहरू', 'Posts'); },
    calendar: function () { return S('पात्रो', 'Calendar'); },
    stations: function () { return S('रेडियो स्टेशनहरू', 'Stations'); },
    profile: function () { return S('प्रोफाइल', 'Profile'); },
    settings: function () { return S('सेटिङ', 'Settings'); },
    about: function () { return S('हाम्रोबारे', 'About'); },
    helpdesk: function () { return S('हेल्पडेस्क', 'Helpdesk'); },
    notifications: function () { return S('सूचनाहरू', 'Notifications'); },
    social: function () { return S('सामाजिक', 'Social'); },
    admin: function () { return S('एडमिन', 'Admin'); },
    login: function () { return S('लगइन', 'Login'); },
    register: function () { return S('दर्ता गर्नुहोस्', 'Register'); },
    logout: function () { return S('लगआउट', 'Logout'); },
    guest: function () { return S('अतिथि', 'Guest'); },
    back: function () { return S('पछाडि', 'Back'); },
    listenLive: function () { return S('लाइभ सुन्नुहोस्', 'Listen Live'); },
    pause: function () { return S('रोक्नुहोस्', 'Pause'); },
    play: function () { return S('बजाउनुहोस्', 'Play'); },
    stop: function () { return S('बन्द गर्नुहोस्', 'Stop'); },
    volume: function () { return S('भोल्युम', 'Volume'); },
    buffering: function () { return S('बफरिङ…', 'Buffering…'); },
    connecting: function () { return S('जडान गरिँदै…', 'Connecting…'); },
    connected: function () { return S('जडान भयो', 'Connected'); },
    reconnecting: function () { return S('पुनः जडान गरिँदै…', 'Reconnecting…'); },
    connectionFailed: function () { return S('जडान असफल', 'Connection failed'); },
    nowPlaying: function () { return S('अहिले प्रसारणमा', 'Now playing'); },
    noProgram: function () { return S('कार्यक्रम तालिका उपलब्ध छैन', 'No program scheduled'); },
    currentProgram: function () { return S('चलिरहेको कार्यक्रम', 'Current program'); },
    latestNews: function () { return S('ताजा समाचार', 'Latest News'); },
    latestPosts: function () { return S('पछिल्ला पोस्टहरू', 'Latest Posts'); },
    upcomingEvents: function () { return S('आगामी कार्यक्रमहरू', 'Upcoming Events'); },
    announcements: function () { return S('घोषणाहरू', 'Announcements'); },
    seeAll: function () { return S('सबै हेर्नुहोस्', 'See all'); },
    search: function () { return S('खोज्नुहोस्…', 'Search…'); },
    bookmarks: function () { return S('बुकमार्कहरू', 'Bookmarks'); },
    bookmark: function () { return S('बुकमार्क', 'Bookmark'); },
    favorites: function () { return S('मनपर्नेहरू', 'Favourites'); },
    favorite: function () { return S('मनपर्ने', 'Favourite'); },
    share: function () { return S('सेयर गर्नुहोस्', 'Share'); },
    today: function () { return S('आज', 'Today'); },
    dayDetails: function () { return S('दिनको विवरण', 'Day details'); },
    noEvents: function () { return S('यो दिनका लागि कार्यक्रम छैनन्', 'No events for this day'); },
    publicHoliday: function () { return S('सार्वजनिक बिदा', 'Public holiday'); },
    events: function () { return S('कार्यक्रमहरू', 'Events'); },
    noNews: function () { return S('कुनै समाचार भेटिएन', 'No news found'); },
    noStations: function () { return S('कुनै स्टेशन भेटिएन', 'No stations found'); },
    noFavorites: function () { return S('मनपर्ने सूची खाली छ', 'No favourites yet'); },
    live: function () { return S('लाइभ', 'LIVE'); },
    offline: function () { return S('अफलाइन', 'Offline'); },
    checking: function () { return S('जाँच हुँदै…', 'Checking…'); },
    welcomeBack: function () { return S('फेरि स्वागत छ', 'Welcome back'); },
    createAccount: function () { return S('नयाँ खाता बनाउनुहोस्', 'Create an account'); },
    emailOrPhone: function () { return S('इमेल वा फोन', 'Email or phone'); },
    email: function () { return S('इमेल', 'Email'); },
    phone: function () { return S('फोन', 'Phone'); },
    password: function () { return S('पासवर्ड', 'Password'); },
    confirmPassword: function () { return S('पासवर्ड पुष्टि गर्नुहोस्', 'Confirm password'); },
    name: function () { return S('नाम', 'Name'); },
    continueAsGuest: function () { return S('अतिथिका रूपमा जारी राख्नुहोस्', 'Continue as guest'); },
    loginFailed: function () { return S('लगइन असफल — जाँच गरेर फेरि प्रयास गर्नुहोस्', 'Login failed — check and try again'); },
    fieldsRequired: function () { return S('कृपया सबै आवश्यक विवरण भर्नुहोस्', 'Please fill in all required fields'); },
    passwordTooShort: function () { return S('पासवर्ड कम्तीमा ६ अक्षरको हुनुपर्छ', 'Password must be at least 6 characters'); },
    passwordsDontMatch: function () { return S('पासवर्डहरू मेल खाँदैनन्', 'Passwords do not match'); },
    accountExists: function () { return S('यो इमेल पहिले नै दर्ता भइसकेको छ', 'This email is already registered'); },
    registerFailed: function () { return S('दर्ता असफल — फेरि प्रयास गर्नुहोस्', 'Registration failed — please try again'); },
    guestUser: function () { return S('अतिथि प्रयोगकर्ता', 'Guest user'); },
    memberSince: function () { return S('सदस्य भएको', 'Member since'); },
    appearance: function () { return S('रूपरंग', 'Appearance'); },
    darkTheme: function () { return S('डार्क मोड', 'Dark mode'); },
    language: function () { return S('भाषा', 'Language'); },
    nepali: function () { return S('नेपाली', 'Nepali'); },
    english: function () { return S('अंग्रेजी', 'English'); },
    adminConsole: function () { return S('एडमिन कन्सोल', 'Admin console'); },
    version: function () { return S('संस्करण', 'Version'); },
    createdBy: function () { return S('निर्माता', 'Created by'); },
    contactInfo: function () { return S('सम्पर्क विवरण', 'Contact information'); },
    followUs: function () { return S('हामीलाई फलो गर्नुहोस्', 'Follow us'); },
    contactUs: function () { return S('हामीलाई सम्पर्क गर्नुहोस्', 'Contact us'); },
    yourName: function () { return S('तपाईंको नाम', 'Your name'); },
    message: function () { return S('सन्देश', 'Message'); },
    sendMessage: function () { return S('सन्देश पठाउनुहोस्', 'Send message'); },
    messageSent: function () { return S('सन्देश पठाइयो! धन्यवाद।', 'Message sent! Thank you.'); },
    myRequests: function () { return S('मेरा अनुरोधहरू', 'My requests'); },
    noRequests: function () { return S('कुनै अनुरोध छैन', 'No requests yet'); },
    noNotifications: function () { return S('कुनै सूचना छैन', 'No notifications'); },
    markAllRead: function () { return S('सबै पढेको चिन्ह लगाउनुहोस्', 'Mark all read'); },
    clearAll: function () { return S('सबै मेट्नुहोस्', 'Clear all'); },
    streamHint: function () { return S('लाइभ स्ट्रिम जडान हुन सकेन — कृपया पछि फेरि प्रयास गर्नुहोस्।', 'Could not connect to the live stream — please try again later.'); },
    stationDetails: function () { return S('स्टेशन विवरण', 'Station details'); },
    ourTeam: function () { return S('हाम्रो टोली', 'Our team'); },
    stationManager: function () { return S('स्टेशन प्रमुख', 'Station Manager'); },
    technician: function () { return S('प्राविधिक', 'Technician'); },
    marketingManager: function () { return S('मार्केटिङ प्रमुख', 'Marketing Manager'); },
    openInBrowser: function () { return S('ब्राउजरमा खोल्नुहोस्', 'Open in browser'); },
    externalNotice: function () { return S('यो लिङ्क बाहिर खुल्छ।', 'This link opens externally.'); },
    noSocial: function () { return S('सामाजिक लिङ्कहरू कन्फिगर गरिएका छैनन्', 'Social links are not configured yet'); },
    adminNotice: function () { return S('डेमो एडमिन — यो वेब संस्करणमा परिवर्तनहरू यस ब्राउजरमा मात्र सुरक्षित हुन्छन्।', 'Demo admin — on this web build, changes are saved in this browser only.'); },
    sendNotification: function () { return S('सूचना पठाउनुहोस्', 'Send notification'); },
    notifTitle: function () { return S('सूचनाको शीर्षक', 'Notification title'); },
    notifBody: function () { return S('सूचनाको सन्देश', 'Notification message'); },
    sendNow: function () { return S('अहिले पठाउनुहोस्', 'Send now'); },
    listening: function (n) { return App.lang === 'ne' ? toNp(n) + ' जना सुन्दै' : n + ' listening'; },
    justNow: function () { return S('अहिले', 'just now'); },
    agoM: function (m) { return App.lang === 'ne' ? toNp(m) + ' मिनेट अघि' : m + 'm ago'; },
    agoH: function (h) { return App.lang === 'ne' ? toNp(h) + ' घण्टा अघि' : h + 'h ago'; },
    agoD: function (d) { return App.lang === 'ne' ? toNp(d) + ' दिन अघि' : d + 'd ago'; }
  };

  /* ============================ State ============================ */
  var store = {
    lang: 'ne',
    theme: 'light',
    user: null,           // {id, name, email, phone, isGuest, isAdmin, createdAt}
    accounts: [],         // local accounts
    bookmarks: [],        // news ids
    favorites: [],        // station ids
    notifications: [],
    tickets: [],
    route: 'home',
    playing: false,
    status: 'idle',       // idle | connecting | buffering | playing | paused | error
    streamError: false
  };

  function load() {
    try {
      var raw = localStorage.getItem('rs_state');
      if (raw) {
        var s = JSON.parse(raw);
        store.lang = s.lang || 'ne';
        store.theme = s.theme || 'light';
        store.user = s.user || null;
        store.accounts = s.accounts || [];
        store.bookmarks = s.bookmarks || [];
        store.favorites = s.favorites || [];
        store.notifications = s.notifications || [];
        store.tickets = s.tickets || [];
      }
    } catch (e) { /* ignore */ }
  }
  function save() {
    try {
      localStorage.setItem('rs_state', JSON.stringify({
        lang: store.lang, theme: store.theme, user: store.user, accounts: store.accounts,
        bookmarks: store.bookmarks, favorites: store.favorites,
        notifications: store.notifications, tickets: store.tickets
      }));
    } catch (e) { /* ignore */ }
  }

  /* ============================ BS Calendar engine ============================ */
  var D = window.RS_DATA;
  var ML = D.MONTH_LENGTHS;
  var MONTH_NE = D.MONTH_NE, MONTH_EN = D.MONTH_EN;
  var WD_NE = D.WD_NE, WD_EN = D.WD_EN, WD_S_NE = D.WD_SHORT_NE, WD_S_EN = D.WD_SHORT_EN;
  var HOLIDAYS = D.HOLIDAYS;
  var ANCHOR_EPOCH = Math.floor(Date.UTC(1943, 3, 14) / 86400000);
  var NP_DIGITS = ['०','१','२','३','४','५','६','७','८','९'];

  function toNp(n) { return String(n).replace(/\d/g, function (d) { return NP_DIGITS[d]; }); }
  function daysInYear(y) { var s = 0, m = ML[y]; for (var i = 0; i < 12; i++) s += m[i]; return s; }
  function daysInMonth(y, mo) { return ML[y][mo - 1]; }

  function fromAdYMD(y, mo, d) {
    var epoch = Math.floor(Date.UTC(y, mo - 1, d) / 86400000) - ANCHOR_EPOCH;
    if (epoch < 0) throw new Error('before range');
    var year = 2000, yl = daysInYear(year);
    while (epoch >= yl) { epoch -= yl; year++; if (year > 2090) throw new Error('after range'); yl = daysInYear(year); }
    var month = 1, remaining = epoch, ml = daysInMonth(year, month);
    while (remaining >= ml) { remaining -= ml; month++; ml = daysInMonth(year, month); }
    return { year: year, month: month, day: remaining + 1 };
  }
  function toAdYMD(bs) {
    var epoch = 0, y, m;
    for (y = 2000; y < bs.year; y++) epoch += daysInYear(y);
    for (m = 1; m < bs.month; m++) epoch += daysInMonth(bs.year, m);
    epoch += bs.day - 1;
    var d = new Date((ANCHOR_EPOCH + epoch) * 86400000);
    return { y: d.getUTCFullYear(), mo: d.getUTCMonth() + 1, d: d.getUTCDate() };
  }
  function weekdayOf(bs) { var a = toAdYMD(bs); return new Date(Date.UTC(a.y, a.mo - 1, a.d)).getUTCDay() + 1; } // 1=Sun
  function bsKey(bs) { return bs.year + '-' + pad(bs.month) + '-' + pad(bs.day); }
  function pad(n) { return (n < 10 ? '0' : '') + n; }

  function nepalNow() {
    try {
      var f = new Intl.DateTimeFormat('en-GB', { timeZone: 'Asia/Kathmandu', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false, weekday: 'short' });
      var p = {}; f.formatToParts(new Date()).forEach(function (x) { p[x.type] = x.value; });
      var wd = { Sun: 1, Mon: 2, Tue: 3, Wed: 4, Thu: 5, Fri: 6, Sat: 7 }[p.weekday] || 1;
      return { y: +p.year, mo: +p.month, d: +p.day, h: +p.hour, mi: +p.minute, s: +p.second, wd: wd };
    } catch (e) {
      var now = new Date(Date.now() + (5 * 60 + 45) * 60000);
      var u = new Date(now.toISOString());
      return { y: u.getUTCFullYear(), mo: u.getUTCMonth() + 1, d: u.getUTCDate(), h: u.getUTCHours(), mi: u.getUTCMinutes(), s: u.getUTCSeconds(), wd: u.getUTCDay() + 1 };
    }
  }
  function todayBs() { var n = nepalNow(); return fromAdYMD(n.y, n.mo, n.d); }

  /* ============================ Seed data (mirrors SeedData.kt) ============================ */
  var DAY = 86400000, now = Date.now();
  var news = [
    { id: 'n1', title: 'Radio Shuddhodhan एप आधिकारिक रूपमा सार्वजनिक', summary: 'समुदायको आवाज अब तपाईंको नामा। लाइभ रेडियो, समाचार, नेपाली पात्रो र हेल्पडेस्क एउटै एपमा।', content: 'Radio Shuddhodhan मोबाइल एप आधिकारिक रूपमा सार्वजनिक भएको छ। एपमार्फत लाइभ रेडियो प्रसारण सुन्न, ताजा समाचार पढ्न, विक्रम सम्बत् पात्रो हेर्न र हेल्पडेस्कमा सम्पर्क गर्न सकिन्छ। एपमा नेपाली र अंग्रेजी दुवै भाषामा नेपाली मिति र समय हेर्न मिल्छ।', category: 'समाचार', author: 'Radio Shuddhodhan', publishedAt: now - DAY, featured: true, breaking: false, image: null },
    { id: 'n2', title: 'ब्रेकिङ: स्थानीय समाचार प्रणाली सञ्चालनमा', summary: 'एपमार्फत तत्कालै ब्रेकिङ समाचार सूचना पठाउने प्रणाली सञ्चालनमा आएको छ।', content: 'एडमिनले ब्रेकिङ समाचार पठाउँदा प्रयोगकर्ताको फोनमा तुरुन्तै सूचना देखिने व्यवस्था मिलाइएको छ।', category: 'समाचार', author: 'Radio Shuddhodhan', publishedAt: now - DAY / 2, featured: false, breaking: true, image: null },
    { id: 'n3', title: 'कार्यक्रम तालिका: साँझको विशेष कुराकानी', summary: 'हरेक साँझ ७ बजे समुदायका विषयमा विशेष कुराकानी कार्यक्रम प्रसारण हुन्छ।', content: "हरेक साँझ ७ बजे 'समुदायको मञ्च' कार्यक्रममा स्थानीय विषयवस्तुमा छलफल हुन्छ।", category: 'कार्यक्रम', author: 'Radio Shuddhodhan', publishedAt: now - 2 * DAY, featured: false, breaking: false, image: null },
    { id: 'n4', title: 'मौसम अपडेट र दैनिक जानकारी', summary: 'बिहानको प्रसारणमा मौसम विवरण र दैनिक जानकारी सुन्न पाउनुहुन्छ।', content: 'प्रत्येक बिहान ७ बजेको प्रसारणमा मौसम विवरण र दैनिक जानकारी प्रसारण गरिन्छ।', category: 'जानकारी', author: 'Radio Shuddhodhan', publishedAt: now - 3 * DAY, featured: false, breaking: false, image: null }
  ];
  var posts = [
    { id: 'p1', title: 'रेडियोको इतिहास: समुदायसँगको यात्रा', summary: 'Radio Shuddhodhan कसरी समुदायको विश्वसनीय स्रोत बन्यो।', content: 'सुरुवातमा सानो समुदाय रेडियोका रूपमा यात्रा सुरु गरेको Radio Shuddhodhan आज समुदायको घर-घरमा पुगेको छ।', author: 'Radio Shuddhodhan', publishedAt: now - 4 * DAY, featured: true, image: null },
    { id: 'p2', title: 'स्वयंसेवक खोजिँदै', summary: 'रेडियो कार्यक्रममा सहयोग गर्न इच्छुक स्वयंसेवकहरूका लागि अवसर।', content: 'कार्यक्रम निर्माण, प्रस्तुति र प्राविधिक काममा सहयोग गर्न इच्छुक स्वयंसेवकहरूले हेल्पडेस्कमार्फत सम्पर्क गर्नुहोस्।', author: 'Radio Shuddhodhan', publishedAt: now - 6 * DAY, featured: false, image: null }
  ];
  var stations = [
    { id: 'st-shuddhodhan', name: 'Radio Shuddhodhan 95.1 MHz', nameNe: 'रेडियो शुद्धोधन 95.1 मेगाहर्ज', description: 'तपाईंको समुदायको आवाज — समाचार, संगीत र जानकारी।', streamUrl: STREAM_URL, logo: null, enabled: true, featured: true, order: 0 },
    { id: 'st-1', name: 'Groove Salad (Demo)', nameNe: 'ग्रुभ सलाड (डेमो)', description: 'शान्त संगीत। Sample ambient station.', streamUrl: 'https://ice1.somafm.com/groovesalad-128-mp3', logo: null, enabled: true, featured: false, order: 1 },
    { id: 'st-2', name: 'Indie Pop (Demo)', nameNe: 'इन्डी पप (डेमो)', description: 'इन्डी संगीत। Sample indie station.', streamUrl: 'https://ice1.somafm.com/indiepop-128-mp3', logo: null, enabled: true, featured: false, order: 2 },
    { id: 'st-3', name: 'Secret Agent (Demo)', nameNe: 'सिक्रेट एजेन्ट (डेमो)', description: 'लाउन्ज संगीत। Sample lounge station.', streamUrl: 'https://ice2.somafm.com/secretagent-128-mp3', logo: null, enabled: true, featured: false, order: 3 }
  ];
  var socialLinks = [
    { id: 'soc-fb', platform: 'facebook', label: 'Facebook', url: 'https://www.facebook.com/share/1BjjUPuPdx/' },
    { id: 'soc-fblive', platform: 'facebook_live', label: 'Facebook Live', url: 'https://www.facebook.com/share/1BjjUPuPdx/' },
    { id: 'soc-yt', platform: 'youtube', label: 'YouTube', url: 'https://www.youtube.com/' },
    { id: 'soc-web', platform: 'website', label: 'Website', url: 'https://www.facebook.com/share/1BjjUPuPdx/' }
  ];

  function seedEvents() {
    var t = nepalNow();
    function plusDays(dd) { var d = new Date(Date.UTC(t.y, t.mo - 1, t.d + dd)); return d.getUTCFullYear() + '-' + pad(d.getUTCMonth() + 1) + '-' + pad(d.getUTCDate()); }
    return [
      { id: 'e1', title: 'समुदाय संगीत रात', titleNe: 'समुदाय संगीत रात', adDate: plusDays(3), time: '19:00', location: 'Radio Shuddhodhan Studio', featured: true },
      { id: 'e2', title: 'स्वास्थ्य जागरण कार्यक्रम', titleNe: 'स्वास्थ्य जागरण कार्यक्रम', adDate: plusDays(10), time: '10:00', location: 'Radio Shuddhodhan Studio', featured: false },
      { id: 'e3', title: 'विद्यालय छलफल', titleNe: 'विद्यालय छलफल', adDate: plusDays(21), time: '14:00', location: 'Radio Shuddhodhan Studio', featured: false }
    ];
  }
  function seedAnnouncements() {
    return [{ id: 'a1', title: 'नयाँ एप सार्वजनिक', message: 'Radio Shuddhodhan एप र वेबसाइट अब उपलब्ध छ। सबैलाई स्वागत छ!', createdAt: now, activeUntil: now + 30 * DAY, active: true }];
  }

  /* ============================ Helpers ============================ */
  function $(sel) { return document.querySelector(sel); }
  function esc(s) { return String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;'); }
  function relTime(ms) {
    var diff = Date.now() - ms, m = Math.floor(diff / 60000), h = Math.floor(diff / 3600000), d = Math.floor(diff / 86400000);
    if (m < 1) return STR.justNow();
    if (m < 60) return STR.agoM(m);
    if (h < 24) return STR.agoH(h);
    if (d < 7) return STR.agoD(d);
    return new Date(ms).toLocaleDateString(App.lang === 'ne' ? 'ne-NP' : 'en-GB');
  }
  function toast(msg) {
    var t = $('#toast'); t.textContent = msg; t.classList.remove('hidden');
    clearTimeout(t._t); t._t = setTimeout(function () { t.classList.add('hidden'); }, 2600);
  }
  function share(text) {
    if (navigator.share) { navigator.share({ text: text }).catch(function () {}); }
    else { try { navigator.clipboard.writeText(text); toast(S('कपी भयो', 'Copied')); } catch (e) {} }
  }
  function greeting() {
    var h = nepalNow().h;
    if (h < 12) return S('शुभ प्रभात', 'Good morning');
    if (h < 17) return S('शुभ दिन', 'Good afternoon');
    return S('शुभ साँझ', 'Good evening');
  }

  /* ============================ Audio engine ============================ */
  var audio = null, currentStation = null;
  function getAudio() {
    if (!audio) {
      audio = $('#rs-audio');
      audio.addEventListener('playing', function () { store.playing = true; store.status = 'playing'; store.streamError = false; syncPlayer(); });
      audio.addEventListener('waiting', function () { store.status = 'buffering'; syncPlayer(); });
      audio.addEventListener('pause', function () { if (!audio.ended) { store.status = 'paused'; } store.playing = false; syncPlayer(); });
      audio.addEventListener('ended', function () { store.playing = false; store.status = 'idle'; syncPlayer(); });
      audio.addEventListener('error', function () {
        store.streamError = true; store.playing = false;
        store.status = 'error';
        if (currentStation && currentStation.id === 'st-shuddhodhan' && currentStation.streamUrl === STREAM_URL && !currentStation._triedHttps) {
          currentStation._triedHttps = true;
          currentStation.streamUrl = 'https://stream.hamropatro.com/8483';
          playStation(currentStation);
          return;
        }
        syncPlayer();
      });
    }
    return audio;
  }
  function playStation(st) {
    currentStation = st;
    store.status = 'connecting'; store.streamError = false; store.playing = false;
    syncPlayer();
    try {
      var a = getAudio();
      a.src = st.streamUrl;
      a.crossOrigin = 'anonymous';
      a.load();
      a.volume = store.volume || 1;
      var p = a.play();
      if (p && p.catch) p.catch(function () { /* autoplay blocked — user must tap */ store.status = 'idle'; syncPlayer(); });
    } catch (e) {
      store.status = 'error'; store.streamError = true; syncPlayer();
    }
  }
  function togglePlay() {
    var a = getAudio();
    if (!currentStation) { playStation(featuredStation()); return; }
    if (store.playing) { a.pause(); }
    else if (store.status === 'idle' || store.status === 'paused' || store.status === 'error') { playStation(currentStation); }
    else { a.play().catch(function () {}); }
  }
  function stopRadio() {
    var a = getAudio();
    try { a.pause(); } catch (e) {}
    try { a.src = ''; a.load(); } catch (e) {}
    currentStation = null; store.playing = false; store.status = 'idle'; store.streamError = false;
    syncPlayer();
  }
  function setVolume(v) { store.volume = v; var a = getAudio(); a.volume = v; save(); }
  function featuredStation() { return stations.find(function (s) { return s.featured && s.enabled; }) || stations[0]; }

  /* ============================ Rendering ============================ */
  function syncPlayer() { renderMiniBar(); if (store.route === 'player') render(); }

  function statusLabel() {
    if (store.streamError) return STR.connectionFailed();
    switch (store.status) {
      case 'connecting': return STR.connecting();
      case 'buffering': return STR.buffering();
      case 'playing': return STR.connected();
      case 'paused': return STR.nowPlaying();
      case 'error': return STR.connectionFailed();
      default: return STR.nowPlaying();
    }
  }

  function topbar(title) {
    return '<div class="topbar"><button class="back" onclick="App.back()">‹</button><div class="ttl">' + esc(title) + '</div></div>';
  }

  function renderMiniBar() {
    var el = $('#minibar');
    if (!currentStation || store.route === 'player') { el.classList.add('hidden'); return; }
    el.classList.remove('hidden');
    el.innerHTML =
      '<div class="eq"><span></span><span></span><span></span><span></span></div>' +
      '<div class="tt"><div class="t1">' + esc(currentStation.name) + '</div><div class="t2">' + esc(statusLabel()) + '</div></div>' +
      '<button class="mb-btn" onclick="App.toggle()">' + (store.playing ? '❚❚' : '▶') + '</button>';
  }

  function renderBottomNav() {
    var nav = $('#bottomnav');
    var routes = ['home', 'news', 'calendar', 'stations', 'profile'];
    var hide = ['splash', 'login', 'register', 'player'].indexOf(store.route) >= 0;
    if (hide) { nav.classList.add('hidden'); return; }
    nav.classList.remove('hidden');
    var items = [
      { r: 'home', ic: '🏠', lb: STR.home() },
      { r: 'news', ic: '📰', lb: STR.news() },
      { r: 'calendar', ic: '📅', lb: STR.calendar() },
      { r: 'stations', ic: '📻', lb: STR.stations() },
      { r: 'profile', ic: '👤', lb: STR.profile() }
    ];
    nav.innerHTML = items.map(function (i) {
      return '<button class="' + (store.route === i.r ? 'active' : '') + '" onclick="App.go(\'' + i.r + '\')"><span class="ic">' + i.ic + '</span>' + i.lb + '</button>';
    }).join('');
  }

  /* ----- screens ----- */
  function scrHome() {
    var ev = seedEvents(), ann = seedAnnouncements();
    var breaking = news.filter(function (n) { return n.breaking; });
    var latest = news.slice().sort(function (a, b) { return b.publishedAt - a.publishedAt; }).slice(0, 6);
    var featured = stations.filter(function (s) { return s.enabled; }).slice(0, 6);
    var upcoming = ev;
    var html = '';
    html += '<div class="header"><div class="brand-row">' +
      '<img class="logo" src="app_logo.png" alt="logo" />' +
      '<div class="grow"><div class="brand-title">' + esc(APP_NAME) + '</div><div class="brand-sub">' + esc(greeting()) + '</div></div>' +
      '</div><div class="clockbar" id="clockbar"></div></div>';

    html += '<div class="screen">';
    html += '<div class="banner">📢 ' + S('Radio Shuddhodhan मा स्वागत छ — तपाईंको समुदायको आवाज', 'Welcome to Radio Shuddhodhan — the voice of your community') + '</div>';

    // live radio hero
    html += '<div class="hero ' + (store.playing ? 'playing' : '') + '" onclick="App.openPlayer()">' +
      '<div class="pulse"></div>' +
      '<span class="live-pill">🔴 ' + STR.liveRadio() + '</span>' +
      '<h2>' + esc(STATION_NAME) + '</h2>' +
      '<div class="prog">' + STR.currentProgram() + ': ' + STR.noProgram() + '</div>' +
      '<button class="listen" onclick="event.stopPropagation();App.toggle()">' + (store.playing ? STR.pause() : '▶ ' + STR.listenLive()) + '</button>' +
      '</div>';

    if (breaking.length) {
      html += '<div class="breaking" onclick="App.go(\'newsDetail\',\'' + breaking[0].id + '\')"><span class="bt">⚡ ' + S('ब्रेकिङ', 'BREAKING') + '</span><span>' + esc(breaking[0].title) + '</span></div>';
    }
    if (ann.length) {
      html += '<div class="announce"><div class="t">📣 ' + esc(ann[0].title) + '</div><div class="m">' + esc(ann[0].message) + '</div></div>';
    }

    if (latest.length) {
      html += '<div class="section-title">' + STR.latestNews() + ' <span class="see" onclick="App.go(\'news\')">' + STR.seeAll() + ' ›</span></div>';
      html += '<div class="hscroll">' + latest.map(function (n) {
        return '<div class="hcard" onclick="App.go(\'newsDetail\',\'' + n.id + '\')">' +
          '<div class="img">📻</div><div class="body"><span class="cat">' + esc(n.category) + '</span>' +
          '<div class="t">' + esc(n.title) + '</div><div class="muted" style="margin-top:4px">' + relTime(n.publishedAt) + '</div></div></div>';
      }).join('') + '</div>';
    }

    html += '<div class="shortcut-row">' +
      shortcut('📅', STR.calendar(), 'calendar') +
      shortcut('📻', STR.stations(), 'stations') +
      shortcut('🎧', STR.helpdesk(), 'helpdesk') +
      shortcut('🌐', STR.social(), 'social') + '</div>';

    if (featured.length) {
      html += '<div class="section-title">' + STR.stations() + '</div>';
      html += featured.map(function (s) { return stationRow(s); }).join('');
    }

    if (posts.length) {
      html += '<div class="section-title">' + STR.latestPosts() + ' <span class="see" onclick="App.go(\'posts\')">' + STR.seeAll() + ' ›</span></div>';
      html += posts.map(function (p) { return articleRow(p, 'postDetail'); }).join('');
    }

    if (upcoming.length) {
      html += '<div class="section-title">' + STR.upcomingEvents() + '</div>';
      html += upcoming.map(eventRow).join('');
    }

    html += stationDetailsCard();
    html += '</div>';
    return html;
  }

  function shortcut(ic, lb, route) { return '<div class="shortcut" onclick="App.go(\'' + route + '\')"><div class="ic">' + ic + '</div><div class="lb">' + lb + '</div></div>'; }

  function stationDetailsCard() {
    var team = [
      { role: STR.stationManager(), name: 'Ravi Rana', contact: '' },
      { role: STR.technician(), name: '', contact: '' },
      { role: STR.marketingManager(), name: '', contact: '' }
    ].filter(function (t) { return t.name; });
    return '<div class="card station-details" style="margin-top:14px">' +
      '<div style="font-weight:800">' + (App.lang === 'ne' ? 'रेडियो शुद्धोधन ' : 'Radio Shuddhodhan ') + FREQUENCY + '</div>' +
      '<div class="muted" style="margin-top:4px">' + STR.tagline() + '</div>' +
      '<div class="muted" style="margin-top:8px">📍 ' + esc(ADDRESS) + '</div>' +
      '<div class="muted">📞 ' + esc(PHONE) + '</div>' +
      '<div class="muted">✉️ ' + esc(EMAIL) + '</div>' +
      (team.length ? '<div style="font-weight:800;margin-top:12px">' + STR.ourTeam() + '</div><div class="team">' +
        team.map(function (t) { return '<div class="team-row"><div class="avatar">' + esc(t.name.charAt(0)) + '</div><div><div style="font-weight:700;font-size:13.5px">' + esc(t.name) + '</div><div class="muted">' + esc(t.role) + '</div></div></div>'; }).join('') +
        '</div>' : '') +
      '</div>';
  }

  function articleRow(item, route) {
    return '<div class="article" onclick="App.go(\'' + route + '\',\'' + item.id + '\')">' +
      '<div class="thumb">' + (item.image ? '<img src="' + esc(item.image) + '"/>' : '📻') + '</div>' +
      '<div class="grow"><div class="t">' + esc(item.title) + '</div><div class="s">' + esc(item.summary) + '</div><div class="m">' + relTime(item.publishedAt) + '</div></div></div>';
  }
  function stationRow(s) {
    var isFav = store.favorites.indexOf(s.id) >= 0;
    var playing = currentStation && currentStation.id === s.id && store.playing;
    return '<div class="station-card" onclick="App.play(\'' + s.id + '\')">' +
      '<div class="logo">' + (s.logo ? '<img src="' + esc(s.logo) + '"/>' : esc((App.lang === 'ne' && s.nameNe) ? s.nameNe.charAt(0) : s.name.charAt(0))) + '</div>' +
      '<div class="grow"><div class="nm">' + esc(App.lang === 'ne' && s.nameNe ? s.nameNe : s.name) + (s.featured ? ' ⭐' : '') + '</div>' +
      '<div class="ds">' + esc(s.description) + '</div>' +
      '<span class="badge ' + (playing ? 'live' : '') + '" style="margin-top:6px">' + (playing ? '<i class="dot"></i>' + STR.live() : (s.streamUrl ? '▶ ' + STR.listenLive() : STR.offline())) + '</span></div>' +
      '<button class="iconbtn" onclick="event.stopPropagation();App.toggleFav(\'' + s.id + '\')">' + (isFav ? '❤️' : '🤍') + '</button></div>';
  }
  function eventRow(e) {
    var ad = null; try { var p = e.adDate.split('-'); ad = fromAdYMD(+p[0], +p[1], +p[2]); } catch (err) {}
    var day = ad ? ad.day : '--';
    var mon = ad ? (App.lang === 'ne' ? MONTH_NE[ad.month - 1] : MONTH_EN[ad.month - 1]).slice(0, 3) : '';
    return '<div class="event-row"><div class="date-box"><div class="d">' + (App.lang === 'ne' ? toNp(day) : day) + '</div><div class="mm">' + esc(mon) + '</div></div>' +
      '<div class="grow"><div style="font-weight:700;font-size:14px">' + esc(App.lang === 'ne' && e.titleNe ? e.titleNe : e.title) + '</div>' +
      '<div class="muted">' + esc([e.time, e.location].filter(Boolean).join(' • ')) + '</div></div><span style="font-size:18px">📅</span></div>';
  }

  function scrNews() {
    var q = store._newsQ || '';
    var bf = store._newsBookmark || false;
    var list = news.filter(function (n) {
      if (bf && store.bookmarks.indexOf(n.id) < 0) return false;
      if (!q) return true;
      return (n.title + n.summary + n.category).toLowerCase().indexOf(q.toLowerCase()) >= 0;
    }).sort(function (a, b) { return b.publishedAt - a.publishedAt; });
    var cats = {}; news.forEach(function (n) { cats[n.category] = 1; });
    var catKeys = Object.keys(cats);
    var html = topbar(STR.news());
    html += '<div class="screen">';
    html += '<div class="field"><input id="newsq" value="' + esc(q) + '" placeholder="' + STR.search() + '" oninput="App.onNewsSearch(this.value)" /></div>';
    html += '<div class="chips">' +
      '<div class="chip ' + (bf ? 'active' : '') + '" onclick="App.toggleBookmarkFilter()">🔖 ' + STR.bookmarks() + '</div>' +
      catKeys.map(function (c) { return '<div class="chip" onclick="App.filterNews(\'' + esc(c) + '\')">' + esc(c) + '</div>'; }).join('') + '</div>';
    if (!list.length) html += '<div class="empty"><div class="big">📰</div>' + STR.noNews() + '</div>';
    html += list.map(function (n) { return articleRow(n, 'newsDetail'); }).join('');
    html += '</div>';
    return html;
  }

  function scrNewsDetail(id) {
    var n = null; news.forEach(function (x) { if (x.id === id) n = x; });
    if (!n) { App.go('news'); return ''; }
    var bm = store.bookmarks.indexOf(id) >= 0;
    var html = topbar(S('समाचार', 'News'));
    html += '<div class="screen">';
    if (n.breaking) html += '<span class="badge live" style="margin-bottom:10px">⚡ ' + S('ब्रेकिङ न्युज', 'Breaking News') + '</span>';
    html += '<h1 style="font-size:22px;margin:8px 0">' + esc(n.title) + '</h1>';
    html += '<div class="row" style="margin:10px 0"><span class="badge" style="background:var(--primary-container);color:var(--on-primary-container)">' + esc(n.category) + '</span>' +
      '<span class="muted">' + relTime(n.publishedAt) + '</span><span class="grow"></span>' +
      '<button class="iconbtn" onclick="App.toggleBookmark(\'' + id + '\')">' + (bm ? '🔖' : '📑') + '</button>' +
      '<button class="iconbtn" onclick="App.shareNews(\'' + id + '\')">↗️</button></div>';
    html += '<div class="muted" style="font-size:15px;margin:10px 0">' + esc(n.summary) + '</div>';
    html += '<div style="margin:14px 0">' + esc(n.content) + '</div>';
    if (n.author) html += '<div style="color:var(--primary);font-weight:700">— ' + esc(n.author) + '</div>';
    html += '</div>';
    return html;
  }

  function scrPosts() {
    var html = topbar(STR.posts());
    html += '<div class="screen">';
    if (!posts.length) html += '<div class="empty"><div class="big">📝</div>' + S('कुनै पोस्ट भेटिएन', 'No posts found') + '</div>';
    html += posts.map(function (p) { return articleRow(p, 'postDetail'); }).join('');
    html += '</div>';
    return html;
  }
  function scrPostDetail(id) {
    var p = null; posts.forEach(function (x) { if (x.id === id) p = x; });
    if (!p) { App.go('posts'); return ''; }
    return topbar(STR.posts()) + '<div class="screen"><h1 style="font-size:21px">' + esc(p.title) + '</h1>' +
      '<div class="muted" style="margin:8px 0 14px">' + relTime(p.publishedAt) + '</div><div>' + esc(p.content) + '</div></div>';
  }

  function scrStations() {
    var q = store._stQ || '';
    var favOnly = store._stFav || false;
    var list = stations.filter(function (s) { return s.enabled; }).filter(function (s) {
      if (favOnly && store.favorites.indexOf(s.id) < 0) return false;
      if (!q) return true;
      return (s.name + ' ' + (s.nameNe || '') + ' ' + s.description).toLowerCase().indexOf(q.toLowerCase()) >= 0;
    }).sort(function (a, b) { return a.order - b.order; });
    var html = topbar(STR.stations());
    html += '<div class="screen">';
    html += '<div class="field"><input value="' + esc(q) + '" placeholder="' + STR.search() + '" oninput="App.onStationSearch(this.value)" /></div>';
    html += '<div class="chips"><div class="chip ' + (favOnly ? 'active' : '') + '" onclick="App.toggleFavFilter()">❤️ ' + STR.favorites() + '</div></div>';
    if (!list.length) html += '<div class="empty"><div class="big">📻</div>' + (favOnly ? STR.noFavorites() : STR.noStations()) + '</div>';
    html += list.map(stationRow).join('');
    html += '</div>';
    return html;
  }

  function scrCalendar() {
    var v = store.calView || { y: 0, m: 0 };
    if (!v.y) { var t = todayBs(); v = { y: t.year, m: t.month }; }
    var offset = weekdayOf({ year: v.y, month: v.m, day: 1 }) - 1;
    var dim = daysInMonth(v.y, v.m);
    var today = todayBs();
    var sel = store.calSel;
    var ev = seedEvents();
    var cells = [];
    for (var i = 0; i < offset; i++) cells.push(null);
    for (var d = 1; d <= dim; d++) cells.push({ y: v.y, m: v.m, d: d });
    while (cells.length % 7) cells.push(null);

    var wd = App.lang === 'ne' ? WD_S_NE : WD_S_EN;
    var html = topbar(STR.calendar());
    html += '<div class="screen">';
    html += '<div class="cal-head"><div class="cal-nav">' +
      '<button onclick="App.calShift(-1)">‹</button>' +
      '<div class="m">' + (App.lang === 'ne' ? MONTH_NE[v.m - 1] : MONTH_EN[v.m - 1]) + ' ' + (App.lang === 'ne' ? toNp(v.y) : v.y) + '</div>' +
      '<button onclick="App.calShift(1)">›</button></div>' +
      '<span class="cal-today" onclick="App.calToday()">' + STR.today() + '</span></div>';
    html += '<div class="cal-week">' + wd.map(function (w) { return '<div>' + w + '</div>'; }).join('') + '</div>';
    html += '<div class="cal-grid">' + cells.map(function (c) {
      if (!c) return '<div class="cal-cell empty"></div>';
      var key = bsKey(c);
      var hol = HOLIDAYS[key];
      var dayEv = ev.filter(function (e) { return e.adDate === (function () { var a = toAdYMD(c); return a.y + '-' + pad(a.mo) + '-' + pad(a.d); })(); });
      var cls = 'cal-cell';
      if (c.y === today.year && c.m === today.month && c.d === today.day) cls += ' today';
      if (sel && sel.y === c.y && sel.m === c.m && sel.d === c.d) cls += ' sel';
      if (weekdayOf(c) === 7 && !(c.y === today.year && c.m === today.month && c.d === today.day)) cls += ' sat';
      var dots = '';
      if (hol) dots += '<i style="background:' + (hol.pub ? 'var(--live)' : 'var(--gold)') + '"></i>';
      if (dayEv.length) dots += '<i style="background:var(--primary)"></i>';
      return '<div class="' + cls + '" onclick="App.calSelect(' + c.y + ',' + c.m + ',' + c.d + ')">' + (App.lang === 'ne' ? toNp(c.d) : c.d) + (dots ? '<div class="dott">' + dots + '</div>' : '') + '</div>';
    }).join('') + '</div>';

    if (sel) {
      var skey = bsKey(sel);
      var shol = HOLIDAYS[skey];
      var sa = toAdYMD(sel);
      var sEv = ev.filter(function (e) { return e.adDate === sa.y + '-' + pad(sa.mo) + '-' + pad(sa.d); });
      var wdName = App.lang === 'ne' ? WD_NE[weekdayOf(sel) - 1] : WD_EN[weekdayOf(sel) - 1];
      html += '<div class="card" style="margin-top:14px"><div style="font-weight:800;color:var(--primary)">' + STR.dayDetails() + '</div>' +
        '<div style="font-weight:700;margin-top:6px">' + wdName + ', ' + (App.lang === 'ne' ? MONTH_NE[sel.m - 1] : MONTH_EN[sel.m - 1]) + ' ' + (App.lang === 'ne' ? toNp(sel.d) : sel.d) + ', ' + (App.lang === 'ne' ? toNp(sel.y) : sel.y) + (App.lang === 'ne' ? ' वि.सं.' : ' BS') + '</div>' +
        '<div class="muted">AD: ' + sa.d + '/' + sa.mo + '/' + sa.y + '</div>';
      if (shol) html += '<div style="margin-top:10px"><span class="badge" style="background:var(--error-container);color:var(--on-error-container)">' + (shol.pub ? STR.publicHoliday() : S('चाडपर्व', 'Festival')) + '</span> <strong>' + esc(App.lang === 'ne' ? shol.ne : shol.en) + '</strong></div>';
      if (sEv.length) { html += '<div style="font-weight:700;margin-top:12px">' + STR.events() + ':</div>'; html += sEv.map(eventRow).join(''); }
      if (!shol && !sEv.length) html += '<div class="muted" style="margin-top:10px">' + STR.noEvents() + '</div>';
      html += '</div>';
    }
    html += '</div>';
    return html;
  }

  function scrPlayer() {
    var html = '<div class="topbar"><button class="back" onclick="App.back()">‹</button><div class="ttl">' + STR.liveRadio() + '</div></div>';
    html += '<div class="player"><div class="art ' + (store.playing ? 'playing' : '') + '"><img src="app_logo.png" alt="logo"/></div>';
    html += '<div class="name">' + esc(currentStation ? currentStation.name : STATION_NAME) + '</div>';
    html += '<div class="status">' + (store.connecting || store.buffering ? '<div class="spinner dark"></div>' : '') + esc(statusLabel()) + '</div>';
    if (store.streamError) html += '<div class="muted" style="margin-top:10px;text-align:center;max-width:320px">' + STR.streamHint() + '</div>';
    html += '<div class="controls">' +
      '<button class="ctl small" onclick="App.stopRadio()">⏹</button>' +
      '<button class="ctl play" onclick="App.toggle()">' + (store.playing ? '❚❚' : '▶') + '</button>' +
      '<button class="ctl small" onclick="App.retry()">↻</button></div>';
    html += '<div class="volume-row"><span>🔊</span><input type="range" min="0" max="1" step="0.05" value="' + (store.volume || 1) + '" oninput="App.volume(this.value)"/></div>';
    html += '<div class="muted" style="margin-top:18px;text-align:center">' + S('एप बन्द भए पनि प्रसारण लगातार चल्छ।', 'Live streaming continues in the background.') + '</div></div>';
    return html;
  }

  function scrProfile() {
    var u = store.user;
    var html = '<div class="screen">';
    html += '<div class="header" style="display:flex;gap:14px;align-items:center;padding:20px">' +
      '<img class="logo" style="width:64px;height:64px" src="app_logo.png" alt="logo"/>' +
      '<div class="grow"><div style="font-size:19px;font-weight:800">' + esc(u ? u.name : STR.guestUser()) + '</div>' +
      '<div class="muted">' + (u ? (u.isGuest ? STR.guest() : (u.email || u.phone || '')) : S('अतिथि मोडमा हुनुहुन्छ', 'You are browsing as guest')) + '</div>' +
      (u && !u.isGuest ? '<div class="muted">' + STR.memberSince() + ': ' + new Date(u.createdAt).toLocaleDateString() + '</div>' : '') + '</div></div>';

    html += '<div class="card" style="margin-top:12px">' +
      listRow('🔖', STR.bookmarks(), 'news') +
      listRow('❤️', STR.favorites(), 'stations') +
      listRow('🔔', STR.notifications(), 'notifications') +
      listRow('🎧', STR.helpdesk(), 'helpdesk') +
      listRow('🌐', STR.social(), 'social') +
      listRow('⚙️', STR.settings(), 'settings') +
      listRow('ℹ️', STR.about(), 'about') + '</div>';

    if (u) html += '<button class="btn outline" style="margin-top:14px" onclick="App.logout()">' + STR.logout() + '</button>';
    else html += '<button class="btn" style="margin-top:14px" onclick="App.go(\'login\')">' + STR.login() + '</button>';
    html += '</div>';
    return html;
  }
  function listRow(ic, lb, route) { return '<div class="list-row" onclick="App.go(\'' + route + '\')"><span class="ic">' + ic + '</span><span class="grow" style="font-weight:600">' + lb + '</span><span class="muted">›</span></div>'; }

  function scrLogin() {
    return '<div class="auth-bg"><div class="screen" style="max-width:440px">' +
      '<div class="auth-head"><img src="app_logo.png" alt="logo"/><h1>' + STR.welcomeBack() + '</h1><p>' + esc(APP_NAME) + '</p></div>' +
      '<div class="auth-card">' +
      '<div id="auth-err"></div>' +
      '<div class="field"><label>' + STR.emailOrPhone() + '</label><input id="li-id" autocomplete="username" /></div>' +
      '<div class="field"><label>' + STR.password() + '</label><input id="li-pw" type="password" autocomplete="current-password" /></div>' +
      '<button class="btn" id="li-btn" onclick="App.doLogin()">' + STR.login() + '</button></div>' +
      '<button class="btn outline" style="margin-top:14px" onclick="App.guest()">' + STR.continueAsGuest() + '</button>' +
      '<div class="auth-link">' + STR.register() + '? <a href="javascript:App.go(\'register\')" style="font-weight:800">' + STR.register() + '</a></div>' +
      '</div></div>';
  }
  function scrRegister() {
    return '<div class="auth-bg"><div class="screen" style="max-width:440px">' +
      '<div class="auth-head"><img src="app_logo.png" alt="logo"/><h1>' + STR.createAccount() + '</h1><p>' + esc(APP_NAME) + '</p></div>' +
      '<div class="auth-card">' +
      '<div id="auth-err"></div>' +
      '<div class="field"><label>' + STR.name() + '</label><input id="rg-name" autocomplete="name" /></div>' +
      '<div class="field"><label>' + STR.email() + '</label><input id="rg-email" autocomplete="email" /></div>' +
      '<div class="field"><label>' + STR.phone() + '</label><input id="rg-phone" autocomplete="tel" /></div>' +
      '<div class="field"><label>' + STR.password() + '</label><input id="rg-pw" type="password" autocomplete="new-password" /></div>' +
      '<div class="field"><label>' + STR.confirmPassword() + '</label><input id="rg-pw2" type="password" autocomplete="new-password" /></div>' +
      '<button class="btn" onclick="App.doRegister()">' + STR.register() + '</button></div>' +
      '<div class="auth-link">' + STR.login() + '? <a href="javascript:App.go(\'login\')" style="font-weight:800">' + STR.login() + '</a></div>' +
      '</div></div>';
  }

  function scrSettings() {
    var html = topbar(STR.settings());
    html += '<div class="screen">';
    html += '<div class="card"><div style="font-weight:800;margin-bottom:6px">' + STR.appearance() + '</div>' +
      '<div class="switch-row"><span>' + STR.darkTheme() + '</span><div class="switch ' + (store.theme === 'dark' ? 'on' : '') + '" onclick="App.toggleTheme()"></div></div>' +
      '<div class="switch-row"><span>' + STR.language() + '</span><div class="chips" style="padding:0">' +
      '<div class="chip ' + (store.lang === 'ne' ? 'active' : '') + '" onclick="App.setLang(\'ne\')">' + STR.nepali() + '</div>' +
      '<div class="chip ' + (store.lang === 'en' ? 'active' : '') + '" onclick="App.setLang(\'en\')">' + STR.english() + '</div></div></div></div>';

    if (store.user && !store.user.isGuest) {
      html += '<div class="card" style="margin-top:12px"><div style="font-weight:800;margin-bottom:4px">' + STR.adminConsole() + '</div>' +
        '<div class="muted">' + STR.adminNotice() + '</div>' +
        '<button class="btn outline" style="margin-top:12px" onclick="App.go(\'admin\')">' + STR.adminConsole() + '</button></div>';
    }
    html += '<div class="card" style="margin-top:12px">' + listRow('ℹ️', STR.about(), 'about') + '</div>';
    html += '<div class="center muted" style="margin-top:18px">' + STR.version() + ': ' + VERSION + ' (10001)<br/>' + STR.createdBy() + ': ' + CREATOR + '</div>';
    html += '</div>';
    return html;
  }

  function scrAbout() {
    var html = topbar(STR.about());
    html += '<div class="screen" style="text-align:center">';
    html += '<img src="app_logo.png" style="width:110px;height:110px;border-radius:50%;margin:10px auto"/>';
    html += '<h1 style="font-size:22px;margin:10px 0 2px">' + esc(APP_NAME) + '</h1>';
    html += '<div class="muted">' + STR.version() + ': v' + VERSION + ' • ' + FREQUENCY + '</div>';
    html += '<div class="muted">' + esc(ADDRESS) + '</div>';
    html += '<div style="color:var(--primary);font-weight:700;margin-top:8px">' + STR.tagline() + '</div>';
    html += '<div style="font-weight:700;margin-top:8px">' + STR.createdBy() + ': ' + CREATOR + '</div>';
    html += '<div class="card" style="margin-top:16px;text-align:left"><div style="font-weight:800;margin-bottom:6px">' + STR.contactInfo() + '</div>' +
      '<div class="muted">📞 ' + esc(PHONE) + '</div><div class="muted">✉️ ' + esc(EMAIL) + '</div>' +
      '<div class="muted">🌐 ' + esc('https://www.facebook.com/share/1BjjUPuPdx/') + '</div></div>';
    html += '<button class="btn outline" style="margin-top:14px" onclick="App.go(\'social\')">' + STR.followUs() + '</button>';
    html += '</div>';
    return html;
  }

  function scrHelpdesk() {
    var html = topbar(STR.helpdesk());
    html += '<div class="screen">';
    html += '<div class="card" style="background:var(--primary-container);color:var(--on-primary-container)">' + S('तपाईंको सन्देश एडमिनसम्म सिधै पुग्छ।', 'Your message goes straight to the administrator.') + '</div>';
    html += '<div class="card" style="margin-top:12px">' +
      '<div class="field"><label>' + STR.yourName() + '</label><input id="hd-name" /></div>' +
      '<div class="field"><label>' + STR.emailOrPhone() + '</label><input id="hd-contact" /></div>' +
      '<div class="field"><label>' + S('विषय', 'Category') + '</label><select id="hd-cat">' +
      ['सामान्य/General', 'प्राविधिक/Technical', 'कार्यक्रम सुझाव/Programme feedback', 'समाचार सुझाव/News tip', 'विज्ञापन/Advertisement', 'अन्य/Other'].map(function (c) { return '<option>' + c + '</option>'; }).join('') + '</select></div>' +
      '<div class="field"><label>' + STR.message() + '</label><textarea id="hd-msg" rows="4"></textarea></div>' +
      '<button class="btn" onclick="App.sendHelpdesk()">' + STR.sendMessage() + '</button></div>';

    var mine = store.tickets;
    if (mine.length) {
      html += '<div class="section-title">' + STR.myRequests() + '</div>';
      html += mine.map(function (t) {
        return '<div class="card" style="margin-top:8px"><div class="row"><span class="badge" style="background:var(--primary-container);color:var(--on-primary-container)">' + esc(t.category) + '</span><span class="grow"></span><span class="muted">' + relTime(t.createdAt) + '</span></div>' +
          '<div style="margin-top:8px">' + esc(t.message) + '</div>' +
          (t.reply ? '<div style="background:var(--secondary-container);color:var(--on-secondary-container);border-radius:10px;padding:10px;margin-top:8px">' + esc(t.reply) + '</div>' : '') + '</div>';
      }).join('');
    }
    html += '</div>';
    return html;
  }

  function scrSocial() {
    var html = topbar(STR.social());
    html += '<div class="screen">';
    var fbl = socialLinks.find(function (l) { return l.platform === 'facebook_live'; });
    if (fbl) {
      html += '<div class="hero" style="margin-top:0" onclick="App.openLink(\'' + esc(fbl.url) + '\')">' +
        '<span class="live-pill">📹 ' + S('फेसबुक लाइभ', 'Facebook Live') + '</span>' +
        '<div class="prog" style="margin-top:10px">' + STR.externalNotice() + '</div>' +
        '<button class="listen" onclick="event.stopPropagation();App.openLink(\'' + esc(fbl.url) + '\')">' + STR.openInBrowser() + '</button></div>';
    }
    var others = socialLinks.filter(function (l) { return l.platform !== 'facebook_live'; });
    if (!others.length) html += '<div class="empty"><div class="big">🌐</div>' + STR.noSocial() + '</div>';
    html += others.map(function (l) {
      var ic = { facebook: '📘', youtube: '▶️', website: '🌐', whatsapp: '💬', twitter: '🐦', instagram: '📸', telegram: '✈️' }[l.platform] || '🔗';
      return '<div class="station-card" style="margin-top:10px" onclick="App.openLink(\'' + esc(l.url) + '\')"><div class="logo" style="background:var(--primary-container);color:var(--primary)">' + ic + '</div>' +
        '<div class="grow"><div class="nm">' + esc(l.label) + '</div><div class="ds">' + esc(l.url.replace(/^https?:\/\//, '')) + '</div></div><span class="muted">↗</span></div>';
    }).join('');
    html += '</div>';
    return html;
  }

  function scrNotifications() {
    var html = topbar(STR.notifications());
    html += '<div class="screen">';
    html += '<div class="row" style="margin-bottom:8px"><span class="grow"></span>' +
      '<button class="btn ghost" style="width:auto;padding:8px 14px" onclick="App.markAllRead()">' + STR.markAllRead() + '</button>' +
      '<button class="btn ghost" style="width:auto;padding:8px 14px" onclick="App.clearNotifs()">' + STR.clearAll() + '</button></div>';
    if (!store.notifications.length) html += '<div class="empty"><div class="big">🔔</div>' + STR.noNotifications() + '</div>';
    html += store.notifications.map(function (n) {
      var ic = { breaking: '⚡', news: '📰', event: '📅', announcement: '📣', general: '🔔' }[n.type] || '🔔';
      return '<div class="notif ' + (n.read ? '' : 'unread') + '"><div class="t">' + ic + ' ' + esc(n.title) + '</div><div class="b">' + esc(n.body) + '</div><div class="tm">' + relTime(n.receivedAt) + '</div></div>';
    }).join('');
    html += '</div>';
    return html;
  }

  function scrAdmin() {
    var html = topbar(STR.adminConsole());
    html += '<div class="screen">';
    html += '<div class="card" style="background:var(--secondary-container);color:var(--on-secondary-container)">' + STR.adminNotice() + '</div>';
    html += '<div class="grid-tiles" style="margin-top:14px">' +
      tile('📰', S('समाचार व्यवस्थापन', 'Manage news')) +
      tile('📻', S('स्टेशन व्यवस्थापन', 'Manage stations')) +
      tile('📅', S('इभेन्ट व्यवस्थापन', 'Manage events')) +
      tile('🔔', STR.sendNotification()) + '</div>';

    html += '<div class="card" style="margin-top:14px"><div style="font-weight:800;margin-bottom:10px">' + STR.sendNotification() + '</div>' +
      '<div class="field"><label>' + STR.notifTitle() + '</label><input id="nt-title" /></div>' +
      '<div class="field"><label>' + STR.notifBody() + '</label><textarea id="nt-body" rows="3"></textarea></div>' +
      '<button class="btn" onclick="App.sendNotif()">' + STR.sendNow() + '</button></div>';
    html += '</div>';
    return html;
  }
  function tile(ic, lb) { return '<div class="tile"><div class="ic">' + ic + '</div><div class="lb">' + lb + '</div></div>'; }

  /* ============================ Router ============================ */
  var routes = ['home', 'news', 'newsDetail', 'posts', 'postDetail', 'stations', 'calendar', 'player', 'profile', 'login', 'register', 'settings', 'about', 'helpdesk', 'social', 'notifications', 'admin'];
  function render() {
    var app = $('#app');
    var r = store.route;
    var base = r.split('/')[0];
    if (routes.indexOf(base) < 0) base = 'home';
    var arg = r.split('/')[1];
    var html = '';
    switch (base) {
      case 'home': html = scrHome(); break;
      case 'news': html = scrNews(); break;
      case 'newsDetail': html = scrNewsDetail(arg); break;
      case 'posts': html = scrPosts(); break;
      case 'postDetail': html = scrPostDetail(arg); break;
      case 'stations': html = scrStations(); break;
      case 'calendar': html = scrCalendar(); break;
      case 'player': html = scrPlayer(); break;
      case 'profile': html = scrProfile(); break;
      case 'login': html = scrLogin(); break;
      case 'register': html = scrRegister(); break;
      case 'settings': html = scrSettings(); break;
      case 'about': html = scrAbout(); break;
      case 'helpdesk': html = scrHelpdesk(); break;
      case 'social': html = scrSocial(); break;
      case 'notifications': html = scrNotifications(); break;
      case 'admin': html = scrAdmin(); break;
      default: html = scrHome();
    }
    app.innerHTML = html;
    renderBottomNav();
    renderMiniBar();
    if (base === 'home') tickClock();
    window.scrollTo(0, 0);
  }

  /* ============================ Clock ============================ */
  function tickClock() {
    var el = $('#clockbar'); if (!el) return;
    var n = nepalNow();
    var bs = fromAdYMD(n.y, n.mo, n.d);
    var wd = App.lang === 'ne' ? WD_NE[n.wd - 1] : WD_EN[n.wd - 1];
    var mo = App.lang === 'ne' ? MONTH_NE[bs.month - 1] : MONTH_EN[bs.month - 1];
    var time = pad(n.h) + ':' + pad(n.mi) + ':' + pad(n.s);
    el.innerHTML =
      '<span class="flag">🇳🇵</span>' +
      '<div class="grow"><div class="d1">' + wd + ', ' + mo + ' ' + (App.lang === 'ne' ? toNp(bs.day) : bs.day) + ', ' + (App.lang === 'ne' ? toNp(bs.year) : bs.year) + (App.lang === 'ne' ? ' वि.सं.' : ' BS') + '</div>' +
      '<div class="d2">' + (App.lang === 'ne' ? 'ई.सं. ' : 'AD ') + (App.lang === 'ne' ? toNp(n.d + '/' + n.mo + '/' + n.y) : n.d + '/' + n.mo + '/' + n.y) + '</div></div>' +
      '<div class="time">' + (App.lang === 'ne' ? toNp(time) : time) + '<small>' + S('नेपालको समय', 'Nepal time') + '</small></div>';
  }

  /* ============================ App API (called from HTML) ============================ */
  window.App = {
    go: function (r, arg) {
      if (r === 'newsDetail' || r === 'postDetail') store.route = r + '/' + arg;
      else store.route = r;
      render();
    },
    back: function () { this.go('home'); },
    openPlayer: function () { this.go('player'); },
    play: function (id) {
      var s = null; stations.forEach(function (x) { if (x.id === id) s = x; });
      if (s) { playStation(s); this.go('player'); }
    },
    toggle: function () { togglePlay(); },
    stopRadio: function () { stopRadio(); },
    retry: function () { if (currentStation) { currentStation._triedHttps = true; playStation(currentStation); } else playStation(featuredStation()); },
    volume: function (v) { setVolume(parseFloat(v)); },
    onNewsSearch: function (v) { store._newsQ = v; this.go('news'); var i = $('#newsq'); if (i) { i.value = v; i.focus(); } },
    onStationSearch: function (v) { store._stQ = v; render(); },
    toggleBookmarkFilter: function () { store._newsBookmark = !store._newsBookmark; render(); },
    toggleFavFilter: function () { store._stFav = !store._stFav; render(); },
    filterNews: function (c) { store._newsQ = c; render(); },
    toggleBookmark: function (id) {
      var i = store.bookmarks.indexOf(id);
      if (i >= 0) store.bookmarks.splice(i, 1); else store.bookmarks.push(id);
      save(); render();
    },
    toggleFav: function (id) {
      var i = store.favorites.indexOf(id);
      if (i >= 0) store.favorites.splice(i, 1); else store.favorites.push(id);
      save(); render();
    },
    shareNews: function (id) {
      var n = null; news.forEach(function (x) { if (x.id === id) n = x; });
      if (n) share(n.title + '\n\n' + n.summary + '\n\n— Radio Shuddhodhan');
    },
    openLink: function (u) { try { window.open(u, '_blank', 'noopener'); } catch (e) {} },
    calShift: function (d) {
      var v = store.calView || { y: todayBs().year, m: todayBs().month };
      v.m += d; if (v.m < 1) { v.m = 12; v.y--; } if (v.m > 12) { v.m = 1; v.y++; }
      if (v.y < 2000) v.y = 2000; if (v.y > 2090) v.y = 2090;
      store.calView = v; store.calSel = null; render();
    },
    calToday: function () { var t = todayBs(); store.calView = { y: t.year, m: t.month }; store.calSel = { y: t.year, m: t.month, d: t.day }; render(); },
    calSelect: function (y, m, d) { store.calSel = { y: y, m: m, d: d }; render(); },
    toggleTheme: function () { store.theme = store.theme === 'dark' ? 'light' : 'dark'; save(); applyTheme(); render(); },
    setLang: function (l) { store.lang = l; save(); render(); },
    doLogin: function () {
      var id = ($('#li-id') ? $('#li-id').value : '').trim();
      var pw = $('#li-pw') ? $('#li-pw').value : '';
      var err = $('#auth-err'); if (!err) return;
      if (!id || !pw) { err.innerHTML = '<div class="error-text">' + STR.fieldsRequired() + '</div>'; return; }
      var acc = null; store.accounts.forEach(function (a) { if (a.email === id || a.phone === id) acc = a; });
      if (!acc || acc.pw !== hash(pw)) { err.innerHTML = '<div class="error-text">' + STR.loginFailed() + '</div>'; return; }
      store.user = { id: acc.id, name: acc.name, email: acc.email, phone: acc.phone, isGuest: false, isAdmin: false, createdAt: acc.createdAt };
      save(); this.go('home');
    },
    doRegister: function () {
      var name = ($('#rg-name') ? $('#rg-name').value : '').trim();
      var email = ($('#rg-email') ? $('#rg-email').value : '').trim();
      var phone = ($('#rg-phone') ? $('#rg-phone').value : '').trim();
      var pw = $('#rg-pw') ? $('#rg-pw').value : '';
      var pw2 = $('#rg-pw2') ? $('#rg-pw2').value : '';
      var err = $('#auth-err'); if (!err) return;
      if (!name || !pw || (!email && !phone)) { err.innerHTML = '<div class="error-text">' + STR.fieldsRequired() + '</div>'; return; }
      if (pw.length < 6) { err.innerHTML = '<div class="error-text">' + STR.passwordTooShort() + '</div>'; return; }
      if (pw !== pw2) { err.innerHTML = '<div class="error-text">' + STR.passwordsDontMatch() + '</div>'; return; }
      var exists = store.accounts.some(function (a) { return (email && a.email === email) || (phone && a.phone === phone); });
      if (exists) { err.innerHTML = '<div class="error-text">' + STR.accountExists() + '</div>'; return; }
      var acc = { id: 'u' + Date.now(), name: name, email: email || null, phone: phone || null, pw: hash(pw), createdAt: Date.now() };
      store.accounts.push(acc);
      store.user = { id: acc.id, name: name, email: acc.email, phone: acc.phone, isGuest: false, isAdmin: false, createdAt: acc.createdAt };
      save(); this.go('home');
    },
    guest: function () {
      store.user = { id: 'g' + Date.now(), name: 'Guest', email: null, phone: null, isGuest: true, isAdmin: false, createdAt: Date.now() };
      save(); this.go('home');
    },
    logout: function () { store.user = null; save(); this.go('profile'); },
    sendHelpdesk: function () {
      var name = ($('#hd-name') ? $('#hd-name').value : '').trim();
      var contact = ($('#hd-contact') ? $('#hd-contact').value : '').trim();
      var cat = $('#hd-cat') ? $('#hd-cat').value : '';
      var msg = ($('#hd-msg') ? $('#hd-msg').value : '').trim();
      if (!name || !contact || !msg) { toast(STR.fieldsRequired()); return; }
      store.tickets.unshift({ id: 't' + Date.now(), name: name, contact: contact, category: cat, message: msg, createdAt: Date.now(), reply: null });
      save(); toast(STR.messageSent()); this.go('helpdesk');
    },
    sendNotif: function () {
      var t = ($('#nt-title') ? $('#nt-title').value : '').trim();
      var b = ($('#nt-body') ? $('#nt-body').value : '').trim();
      if (!t || !b) { toast(STR.fieldsRequired()); return; }
      store.notifications.unshift({ id: 'n' + Date.now(), type: 'general', title: t, body: b, receivedAt: Date.now(), read: false });
      save(); toast(STR.sendNow()); this.go('notifications');
    },
    markAllRead: function () { store.notifications.forEach(function (n) { n.read = true; }); save(); render(); },
    clearNotifs: function () { store.notifications = []; save(); render(); }
  };

  function hash(s) {
    var h = 5381; for (var i = 0; i < s.length; i++) { h = ((h << 5) + h + s.charCodeAt(i)) >>> 0; }
    return 'h' + h.toString(16);
  }

  function applyTheme() { document.body.setAttribute('data-theme', store.theme); }

  /* ============================ Init ============================ */
  function init() {
    load();
    applyTheme();
    // seed a couple of notifications on first run
    if (!store._seededNotifs) {
      store.notifications.push(
        { id: 'n1', type: 'announcement', title: S('नयाँ एप सार्वजनिक', 'New app released'), body: S('Radio Shuddhodhan एप र वेबसाइट अब उपलब्ध छ।', 'The app and website are now live.'), receivedAt: Date.now() - 3600000, read: false },
        { id: 'n2', type: 'news', title: 'स्वागत छ', body: S('स्वागत छ — लाइभ रेडियो सुन्न सुरु गर्नुहोस्।', 'Welcome — start listening to live radio.'), receivedAt: Date.now() - 7200000, read: false }
      );
      store._seededNotifs = true;
      save();
    }
    render();
    // splash out
    setTimeout(function () { var s = $('#splash'); if (s) s.classList.add('done'); }, 1600);
    // clock ticks every second while home is visible
    setInterval(function () { if (store.route === 'home') tickClock(); }, 1000);
  }

  document.addEventListener('DOMContentLoaded', init);
})();
