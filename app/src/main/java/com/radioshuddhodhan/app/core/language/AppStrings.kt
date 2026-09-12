package com.radioshuddhodhan.app.core.language

/**
 * Bilingual (Nepali / English) UI strings for Radio Shuddhodhan.
 *
 * Usage: `val L = LocalAppStrings.current` then `L.home`, `L.liveRadio` etc.
 * A missing translation simply falls back to the other language because both
 * values live side by side.
 */
class AppStrings(val nepali: Boolean) {
    private fun t(ne: String, en: String): String = if (nepali) ne else en

    // ---------- General ----------
    val appName get() = "Radio Shuddhodhan 95.1 MHz"
    val tagline get() = t(
        "हरेक नेपालीको मन रेडियो शुद्धोधन 95.1 मेगाहर्ज.",
        "In every Nepali's heart — Radio Shuddhodhan 95.1 MHz."
    )
    val ok get() = t("ठीक छ", "OK")
    val cancel get() = t("रद्द गर्नुहोस्", "Cancel")
    val save get() = t("सुरक्षित गर्नुहोस्", "Save")
    val delete get() = t("मेट्नुहोस्", "Delete")
    val edit get() = t("सम्पादन गर्नुहोस्", "Edit")
    val add get() = t("थप्नुहोस्", "Add")
    val retry get() = t("फेरि प्रयास गर्नुहोस्", "Retry")
    val next get() = t("अर्को", "Next")
    val previous get() = t("अघिल्लो", "Previous")
    val skip get() = t("छाड्नुहोस्", "Skip")
    val getStarted get() = t("सुरु गर्नुहोस्", "Get Started")
    val search get() = t("खोज्नुहोस्", "Search")
    val share get() = t("सेयर गर्नुहोस्", "Share")
    val loading get() = t("लोड हुँदै…", "Loading…")
    val error get() = t("त्रुटि", "Error")
    val seeAll get() = t("सबै हेर्नुहोस्", "See all")
    val back get() = t("पछाडि", "Back")
    val done get() = t("भयो", "Done")
    val close get() = t("बन्द गर्नुहोस्", "Close")
    val confirm get() = t("पक्का गर्नुहोस्", "Confirm")
    val refresh get() = t("रिफ्रेस गर्नुहोस्", "Refresh")
    val online get() = t("अनलाइन", "Online")
    val offline get() = t("अफलाइन", "Offline")
    val demoMode get() = t("डेमो मोड", "Demo mode")
    val openInBrowser get() = t("ब्राउजरमा खोल्नुहोस्", "Open in browser")
    val enabled get() = t("सक्रिय", "Enabled")
    val disabled get() = t("निष्क्रिय", "Disabled")
    val featured get() = t("विशेष", "Featured")
    val breaking get() = t("ब्रेकिङ", "Breaking")
    val category get() = t("विषय", "Category")
    val all get() = t("सबै", "All")
    val language get() = t("भाषा", "Language")
    val nepaliLang get() = t("नेपाली", "Nepali")
    val englishLang get() = t("अंग्रेजी", "English")
    val notAvailable get() = t("उपलब्ध छैन", "Not available")
    val copy get() = t("कपी गर्नुहोस्", "Copy")
    val copied get() = t("कपी भयो", "Copied")
    val required get() = t("आवश्यक छ", "Required")
    val optional get() = t("वैकल्पिक", "optional")
    val demoNotice get() = t("डेमो डेटा — एडमिनबाट परिवर्तन गर्न सकिन्छ", "Demo data — editable from the Admin console")

    // ---------- Offline ----------
    val noInternet get() = t("इन्टरनेट जडान छैन", "No Internet Connection")
    val noInternetDesc get() = t(
        "कृपया इन्टरनेट जडान जाँच गर्नुहोस्। क्यास गरिएको सामग्री हेर्न सकिन्छ।",
        "Please check your connection. Cached content is still available."
    )
    val radioNeedsInternet get() = t("लाइव रेडियोका लागि इन्टरनेट आवश्यक छ", "Live radio requires an internet connection")
    val connectedAgain get() = t("इन्टरनेट फेरि जडान भयो — समक्रमण गरिँदै", "Back online — synchronising")
    val lastSync get() = t("अन्तिम सिन्क", "Last sync")
    val never get() = t("कहिल्यै छैन", "Never")

    // ---------- Navigation ----------
    val home get() = t("गृह", "Home")
    val liveRadio get() = t("लाइभ रेडियो", "Live Radio")
    val news get() = t("समाचार", "News")
    val posts get() = t("पोस्टहरू", "Posts")
    val calendar get() = t("पात्रो", "Calendar")
    val stations get() = t("रेडियो स्टेशनहरू", "Stations")
    val profile get() = t("प्रोफाइल", "Profile")
    val settings get() = t("सेटिङ", "Settings")
    val about get() = t("हाम्रोबारे", "About")
    val helpdesk get() = t("हेल्पडेस्क", "Helpdesk")
    val notifications get() = t("सूचनाहरू", "Notifications")
    val admin get() = t("एडमिन", "Admin")
    val social get() = t("सामाजिक", "Social")
    val login get() = t("लगइन", "Login")
    val register get() = t("दर्ता गर्नुहोस्", "Register")
    val logout get() = t("लगआउट", "Logout")
    val guest get() = t("अतिथि", "Guest")

    // ---------- Splash & guide ----------
    val welcomeTo get() = t("मा स्वागत छ", "Welcome to")
    val howToUse get() = t("Radio Shuddhodhan कसरी प्रयोग गर्ने", "How to Use Radio Shuddhodhan")
    val guideStep1Title get() = t("लगइन गर्नुहोस्", "1. Login")
    val guideStep1Desc get() = t(
        "आफ्नो खाता बनाउनुहोस् वा अतिथिका रूपमा ब्राउज गर्नुहोस्। गुगल र फोन लगइन ब्याकएन्ड कन्फिगरेसनसँगै उपलब्ध हुन्छ।",
        "Create an account or browse as a guest. Google and phone login become available once the backend is configured."
    )
    val guideStep2Title get() = t("गृहपृष्ठ खोल्नुहोस्", "2. Open Home")
    val guideStep2Desc get() = t(
        "गृहपृष्ठमा नेपाली मिति, नेपालको समय, ताजा समाचार, घोषणा र विशेष सामग्रीहरू एकैठाउँमा पाउनुहुन्छ।",
        "The home screen brings the Nepali date, Nepal time, latest news, announcements and featured content together."
    )
    val guideStep3Title get() = t("लाइभ सुन्नुहोस्", "3. Listen Live")
    val guideStep3Desc get() = t(
        "लाइभ रेडियो बटन थिच्नुहोस्। स्क्रिन बन्द भए पनि पृष्ठभूमिमा प्रसारण चलिरहन्छ।",
        "Tap Live Radio. Playback continues in the background with lock-screen controls even when the screen is off."
    )
    val guideStep4Title get() = t("समाचार पढ्नुहोस्", "4. Read News")
    val guideStep4Desc get() = t(
        "ब्रेकिङ समाचारदेखि विषयगत समाचारसम्म — खोज्नुहोस्, फिल्टर गर्नुहोस्, बुकमार्क र सेयर गर्नुहोस्।",
        "From breaking news to categories — search, filter, bookmark and share stories."
    )
    val guideStep5Title get() = t("पात्रो हेर्नुहोस्", "5. View Calendar")
    val guideStep5Desc get() = t(
        "विक्रम सम्बत् पात्रोमा चाडपर्व, बिदा, कार्यक्रम र रेडियो इभेन्टहरू हेर्नुहोस्।",
        "See festivals, public holidays, events and radio programmes in the Bikram Sambat calendar."
    )
    val guideStep6Title get() = t("स्टेशन ब्राउज गर्नुहोस्", "6. Browse Stations")
    val guideStep6Desc get() = t(
        "विभिन्न रेडियो स्टेशनहरू खोज्नुहोस्, मनपर्नेलाई फेभरिट बनाउनुहोस् र सुन्न सुरु गर्नुहोस्।",
        "Search radio stations, mark favourites and start listening."
    )
    val guideStep7Title get() = t("हेल्पडेस्कमा सम्पर्क गर्नुहोस्", "7. Contact Helpdesk")
    val guideStep7Desc get() = t(
        "कुनै प्रश्न वा सुझाव भए हेल्पडेस्कमा सन्देश पठाउनुहोस् — एडमिनले सिधै जवाफ दिन्छ।",
        "Questions or suggestions? Send a message to the helpdesk — the administrator replies directly."
    )

    // ---------- Home ----------
    val goodMorning get() = t("शुभ प्रभात", "Good morning")
    val goodAfternoon get() = t("शुभ दिन", "Good afternoon")
    val goodEvening get() = t("शुभ साँझ", "Good evening")
    val nepaliDate get() = t("नेपाली मिति", "Nepali date")
    val englishDate get() = t("अंग्रेजी मिति", "English date")
    val nepalTime get() = t("नेपालको समय", "Nepal time")
    val listenLive get() = t("लाइभ सुन्नुहोस्", "Listen Live")
    val currentProgram get() = t("चलिरहेको कार्यक्रम", "Current program")
    val noProgramScheduled get() = t("कार्यक्रम तालिका उपलब्ध छैन", "No program scheduled")
    val latestNews get() = t("ताजा समाचार", "Latest News")
    val latestPosts get() = t("पछिल्ला पोस्टहरू", "Latest Posts")
    val featuredContent get() = t("विशेष सामग्री", "Featured")
    val radioStations get() = t("रेडियो स्टेशनहरू", "Radio Stations")
    val announcements get() = t("घोषणाहरू", "Announcements")
    val upcomingEvents get() = t("आगामी कार्यक्रमहरू", "Upcoming Events")
    val facebookLive get() = t("फेसबुक लाइभ", "Facebook Live")
    val homeBannerDefault get() = t(
        "Radio Shuddhodhan मा स्वागत छ — तपाईंको समुदायको आवाज",
        "Welcome to Radio Shuddhodhan — the voice of your community"
    )

    // ---------- Player ----------
    val nowPlaying get() = t("अहिले प्रसारणमा", "Now playing")
    val play get() = t("बजाउनुहोस्", "Play")
    val pause get() = t("रोक्नुहोस्", "Pause")
    val stop get() = t("बन्द गर्नुहोस्", "Stop")
    val volume get() = t("भोल्युम", "Volume")
    val buffering get() = t("बफरिङ…", "Buffering…")
    val connectingState get() = t("जडान गरिँदै…", "Connecting…")
    val reconnecting get() = t("पुनः जडान गरिँदै…", "Reconnecting…")
    val connected get() = t("जडान भयो", "Connected")
    val connectionFailed get() = t("जडान असफल", "Connection failed")
    val streamNotConfigured get() = t(
        "स्ट्रिम URL कन्फिगर गरिएको छैन",
        "Stream URL is not configured"
    )
    val streamNotConfiguredHint get() = t(
        "एडमिन कन्सोलबाट स्ट्रिम URL थप्न सकिन्छ।",
        "The administrator can set the stream URL from the Admin console."
    )
    val autoReconnect get() = t("स्वतः पुनः जडान", "Auto-reconnect")
    val retryConnection get() = t("फेरि जडान गर्नुहोस्", "Reconnect")
    val backgroundPlayHint get() = t(
        "एप बन्ध भए पनि प्रसारण लगातार चल्छ — लक-स्क्रिन कन्ट्रोल प्रयोग गर्नुहोस्।",
        "Playback continues with lock-screen controls even when the app is closed."
    )
    val live get() = t("लाइभ", "LIVE")
    val changeStation get() = t("स्टेशन परिवर्तन गर्नुहोस्", "Change station")

    // ---------- News ----------
    val breakingNews get() = t("ब्रेकिङ न्युज", "Breaking News")
    val searchNews get() = t("समाचार खोज्नुहोस्…", "Search news…")
    val bookmarks get() = t("बुकमार्कहरू", "Bookmarks")
    val bookmark get() = t("बुकमार्क", "Bookmark")
    val bookmarked get() = t("बुकमार्क गरियो", "Bookmarked")
    val noNews get() = t("कुनै समाचार भेटिएन", "No news found")
    val readMore get() = t("पूरा पढ्नुहोस्", "Read more")
    val noBookmarks get() = t("बुकमार्क खाली छ", "No bookmarks yet")
    val newsCategories get() = t("समाचार विषयहरू", "News categories")

    // ---------- Posts ----------
    val noPosts get() = t("कुनै पोस्ट भेटिएन", "No posts found")
    val postsDesc get() = t("समुदायका लेख र अपडेटहरू", "Community articles and updates")

    // ---------- Calendar ----------
    val today get() = t("आज", "Today")
    val holidays get() = t("बिदाहरू", "Holidays")
    val festivals get() = t("चाडपर्वहरू", "Festivals")
    val events get() = t("कार्यक्रमहरू", "Events")
    val publicHoliday get() = t("सार्वजनिक बिदा", "Public holiday")
    val noEventsForDay get() = t("यो दिनका लागि कार्यक्रम छैनन्", "No events for this day")
    val tapDayDetails get() = t("विवरण हेर्न दिन छुनुहोस्", "Tap a day for details")
    val bsEra get() = t("वि.सं.", "BS")
    val adEra get() = t("ई.सं.", "AD")
    val dayDetails get() = t("दिनको विवरण", "Day details")
    val radioPrograms get() = t("रेडियो कार्यक्रम", "Radio programmes")

    // ---------- Stations ----------
    val searchStations get() = t("स्टेशन खोज्नुहोस्…", "Search stations…")
    val favorites get() = t("मनपर्नेहरू", "Favourites")
    val favorite get() = t("मनपर्ने", "Favourite")
    val unfavorite get() = t("मनपर्नेबाट हटाउनुहोस्", "Remove favourite")
    val noStations get() = t("कुनै स्टेशन भेटिएन", "No stations found")
    val noFavorites get() = t("मनपर्ने सूची खाली छ", "No favourites yet")

    // ---------- Social ----------
    val socialFeed get() = t("सामाजिक समाचार फिड", "Social news feed")
    val followUs get() = t("हामीलाई फलो गर्नुहोस्", "Follow us")
    val facebook get() = t("फेसबुक", "Facebook")
    val youtube get() = t("युट्युब", "YouTube")
    val website get() = t("वेबसाइट", "Website")
    val whatsapp get() = t("व्हाट्सएप", "WhatsApp")
    val twitter get() = t("ट्विटर", "Twitter")
    val instagram get() = t("इन्स्टाग्राम", "Instagram")
    val telegram get() = t("टेलिग्राम", "Telegram")
    val noSocialConfigured get() = t(
        "सामाजिक लिङ्कहरू अझै कन्फिगर गरिएका छैनन्",
        "Social links are not configured yet"
    )
    val externalLinkNotice get() = t(
        "यो लिङ्क बाह्य एपमा खुल्छ।",
        "This link opens in an external app."
    )

    // ---------- Helpdesk ----------
    val contactUs get() = t("हामीलाई सम्पर्क गर्नुहोस्", "Contact us")
    val yourName get() = t("तपाईंको नाम", "Your name")
    val emailOrPhone get() = t("इमेल वा फोन", "Email or phone")
    val message get() = t("सन्देश", "Message")
    val sendMessage get() = t("सन्देश पठाउनुहोस्", "Send message")
    val send get() = t("पठाउनुहोस्", "Send")
    val sending get() = t("पठाइँदै…", "Sending…")
    val messageSent get() = t("सन्देश पठाइयो! धन्यवाद।", "Message sent! Thank you.")
    val messageSendFailed get() = t("सन्देश पठाउन असफल — फेरि प्रयास गर्नुहोस्", "Could not send — please retry")
    val myRequests get() = t("मेरा अनुरोधहरू", "My requests")
    val noRequests get() = t("कुनै अनुरोध छैन", "No requests yet")
    val helpCategoryGeneral get() = t("सामान्य", "General")
    val helpCategoryTechnical get() = t("प्राविधिक", "Technical")
    val helpCategoryFeedback get() = t("कार्यक्रम सुझाव", "Programme feedback")
    val helpCategoryNewsTip get() = t("समाचार सुझाव", "News tip")
    val helpCategoryAds get() = t("विज्ञापन", "Advertisement")
    val helpCategoryOther get() = t("अन्य", "Other")
    val directContact get() = t("सिधै सम्पर्क", "Direct contact")
    val helpdeskHint get() = t(
        "तपाईंको सन्देश एडमिनसम्म सिधै पुग्छ। सामान्यतया २४ घण्टाभित्र जवाफ आउँछ।",
        "Your message goes straight to the administrator. Typical response time is 24 hours."
    )
    val statusOpen get() = t("खुला", "Open")
    val statusPending get() = t("प्रतीक्षामा", "Pending")
    val statusResolved get() = t("समाधान भयो", "Resolved")

    // ---------- Auth ----------
    val welcomeBack get() = t("फेरि स्वागत छ", "Welcome back")
    val createAccountTitle get() = t("नयाँ खाता बनाउनुहोस्", "Create an account")
    val email get() = t("इमेल", "Email")
    val phone get() = t("फोन", "Phone")
    val password get() = t("पासवर्ड", "Password")
    val confirmPassword get() = t("पासवर्ड पुष्टि गर्नुहोस्", "Confirm password")
    val name get() = t("नाम", "Name")
    val forgotPassword get() = t("पासवर्ड बिर्सनुभयो?", "Forgot password?")
    val orContinueWith get() = t("वा यसबाट जारी राख्नुहोस्", "or continue with")
    val continueAsGuest get() = t("अतिथिका रूपमा जारी राख्नुहोस्", "Continue as guest")
    val googleLogin get() = t("गुगलबाट लगइन", "Continue with Google")
    val facebookLogin get() = t("फेसबुकबाट लगइन", "Continue with Facebook")
    val phoneLogin get() = t("फोनबाट लगइन", "Continue with phone")
    val loginFailed get() = t("लगइन असफल — जाँच गरेर फेरि प्रयास गर्नुहोस्", "Login failed — check and try again")
    val registerFailed get() = t("दर्ता असफल — फेरि प्रयास गर्नुहोस्", "Registration failed — please try again")
    val fieldsRequired get() = t("कृपया सबै आवश्यक विवरण भर्नुहोस्", "Please fill in all required fields")
    val passwordTooShort get() = t("पासवर्ड कम्तीमा ६ अक्षरको हुनुपर्छ", "Password must be at least 6 characters")
    val passwordsDontMatch get() = t("पासवर्डहरू मेल खाँदैनन्", "Passwords do not match")
    val accountExists get() = t("यो इमेल पहिले नै दर्ता भइसकेको छ", "This email is already registered")
    val registerSuccess get() = t("खाता बन्यो! स्वागत छ।", "Account created! Welcome.")
    val guestUser get() = t("अतिथि प्रयोगकर्ता", "Guest user")
    val loggedInAs get() = t("लगइन गरिएको: %s", "Logged in as %s")
    val logoutConfirm get() = t("लगआउट गर्ने हो?", "Log out?")
    val socialLoginNeedsBackend get() = t(
        "%s लगइन ब्याकएन्ड सर्भर कन्फिगर गरिएपछि मात्र सक्रिय हुन्छ। हाल इमेल/फोन वा अतिथि मोड प्रयोग गर्नुहोस्।",
        "%s sign-in activates once the backend server is configured. Meanwhile use email/phone or guest mode."
    )
    val demoAccountNotice get() = t(
        "यो खाता यो उपकरणमा मात्र सुरक्षित हुन्छ (डेमो मोड)। ब्याकएन्ड जडान भएपछि खाता क्लाउडमा सिन्क हुन्छ।",
        "This account is stored on this device only (demo mode). Accounts sync to the cloud once a backend is connected."
    )

    // ---------- Profile ----------
    val memberSince get() = t("सदस्य भएको", "Member since")
    val loginToSyncHint get() = t(
        "बुकमार्क र मनपर्नेहरू सुरक्षित राख्न लगइन गर्नुहोस्",
        "Log in to keep your bookmarks and favourites"
    )
    val adminConsole get() = t("एडमिन कन्सोल", "Admin console")
    val guestModeLabel get() = t("अतिथि मोडमा हुनुहुन्छ", "You are browsing as guest")

    // ---------- Settings ----------
    val appearance get() = t("रूपरंग", "Appearance")
    val themeMode get() = t("थिम", "Theme")
    val lightTheme get() = t("लाइट", "Light")
    val darkTheme get() = t("डार्क", "Dark")
    val systemTheme get() = t("सिस्टम", "System")
    val languageSetting get() = t("भाषा सेटिङ", "Language")
    val notificationsSettings get() = t("सूचना सेटिङ", "Notifications")
    val notifBreaking get() = t("ब्रेकिङ समाचार", "Breaking news")
    val notifNews get() = t("नयाँ समाचार र पोस्ट", "News & posts")
    val notifEvents get() = t("पात्रो इभेन्टहरू", "Calendar events")
    val notifAnnouncements get() = t("रेडियो घोषणा", "Radio announcements")
    val notifHint get() = t(
        "सूचना प्राथमिकता ब्याकएन्ड पुश सेवासँगै पूर्ण रूपमा लागू हुन्छ।",
        "Preferences fully apply once backend push delivery is configured."
    )
    val audioSettings get() = t("अडियो सेटिङ", "Audio")
    val accountSection get() = t("खाता", "Account")
    val privacyPolicy get() = t("गोपनीयता नीति", "Privacy policy")
    val termsOfService get() = t("सेवा सर्तहरू", "Terms of service")
    val help get() = t("सहायता", "Help")
    val backendServer get() = t("ब्याकएन्ड सर्भर", "Backend server")
    val backendUrl get() = t("सर्भर URL", "Server URL")
    val backendHint get() = t(
        "खाली छाड्दा एप डेमो मोडमा चल्छ। उदाहरण: https://api.example.com",
        "Leave empty to run in demo mode. Example: https://api.example.com"
    )
    val saveUrl get() = t("URL सुरक्षित गर्नुहोस्", "Save URL")
    val invalidUrl get() = t("अमान्य URL", "Invalid URL")
    val serverConnected get() = t("सर्भर जडान भयो", "Server connected")
    val serverNotSet get() = t("सर्भर सेट छैन — डेमो मोड", "No server set — demo mode")
    val advanced get() = t("उन्नत", "Advanced")
    val versionInfo get() = t("संस्करण", "Version")
    val aboutApp get() = t("एपबारे", "About the app")
    val openSourceNotice get() = t(
        "क्यालेन्डर डेटा खुला स्रोत परियोजनाहरूबाट (MIT/Apache-2.0) प्रमाणित गरिएको छ।",
        "Calendar data is verified from open-source projects (MIT/Apache-2.0)."
    )

    // ---------- About ----------
    val createdBy get() = t("निर्माता", "Created by")
    val aboutTextDefault get() = t(
        "Radio Shuddhodhan समुदायको आवाज — लाइभ प्रसारण, समाचार, पात्रो र सूचना एउटै ठाउँबाट।",
        "Radio Shuddhodhan is the voice of the community — live broadcast, news, calendar and information in one place."
    )
    val contactInfo get() = t("सम्पर्क विवरण", "Contact information")
    val visitWebsite get() = t("वेबसाइट हेर्नुहोस्", "Visit website")

    // ---------- Notifications ----------
    val markAllRead get() = t("सबै पढेको चिन्ह लगाउनुहोस्", "Mark all read")
    val clearAll get() = t("सबै मेट्नुहोस्", "Clear all")
    val noNotifications get() = t("कुनै सूचना छैन", "No notifications")
    val notifTypeBreaking get() = t("ब्रेकिङ", "Breaking")
    val notifTypeNews get() = t("समाचार", "News")
    val notifTypeEvent get() = t("इभेन्ट", "Event")
    val notifTypeAnnouncement get() = t("घोषणा", "Announcement")
    val notifTypeGeneral get() = t("सामान्य", "General")

    // ---------- Admin ----------
    val demoAdminNotice get() = t(
        "डेमो एडमिन — परिवर्तनहरू यो उपकरणमा मात्र सुरक्षित हुन्छन्। उत्पादन प्रयोगका लागि ब्याकएन्ड सर्भर जडान गर्नुहोस् (सेटिङ → ब्याकएन्ड सर्भर)।",
        "Demo admin — changes are saved on this device only. Connect the backend server for production (Settings → Backend server)."
    )
    val adminDashboard get() = t("एडमिन ड्यासबोर्ड", "Admin dashboard")
    val manageNews get() = t("समाचार व्यवस्थापन", "Manage news")
    val managePosts get() = t("पोस्ट व्यवस्थापन", "Manage posts")
    val manageStations get() = t("स्टेशन व्यवस्थापन", "Manage stations")
    val manageEvents get() = t("इभेन्ट व्यवस्थापन", "Manage events")
    val manageAnnouncements get() = t("घोषणा व्यवस्थापन", "Manage announcements")
    val manageSocial get() = t("सामाजिक लिङ्क", "Social links")
    val helpdeskInbox get() = t("हेल्पडेस्क इनबक्स", "Helpdesk inbox")
    val sendNotification get() = t("सूचना पठाउनुहोस्", "Send notification")
    val remoteConfig get() = t("रिमोट कन्फिगरेसन", "Remote configuration")
    val aiAssistant get() = t("AI समाचार सहायक", "AI news assistant")
    val publish get() = t("प्रकाशित गर्नुहोस्", "Publish")
    val unpublish get() = t("अप्रकाशित गर्नुहोस्", "Unpublish")
    val markBreaking get() = t("ब्रेकिङ बनाउनुहोस्", "Mark breaking")
    val unmarkBreaking get() = t("ब्रेकिङ हटाउनुहोस्", "Unmark breaking")
    val markFeatured get() = t("विशेष बनाउनुहोस्", "Mark featured")
    val unmarkFeatured get() = t("विशेष हटाउनुहोस्", "Unmark featured")
    val headline get() = t("शीर्षक", "Headline")
    val summary get() = t("सारांश", "Summary")
    val content get() = t("विषयवस्तु", "Content")
    val imageUrl get() = t("तस्बिर URL (वैकल्पिक)", "Image URL (optional)")
    val addNews get() = t("समाचार थप्नुहोस्", "Add news")
    val editNews get() = t("समाचार सम्पादन", "Edit news")
    val deleteNewsConfirm get() = t("यो समाचार मेट्ने हो?", "Delete this news item?")
    val stationName get() = t("स्टेशनको नाम", "Station name")
    val stationDesc get() = t("स्टेशनको विवरण", "Description")
    val streamUrlLabel get() = t("स्ट्रिम URL", "Stream URL")
    val logoUrlLabel get() = t("लोगो URL (वैकल्पिक)", "Logo URL (optional)")
    val stationOrder get() = t("क्रम", "Order")
    val addStation get() = t("स्टेशन थप्नुहोस्", "Add station")
    val deleteStationConfirm get() = t("यो स्टेशन मेट्ने हो?", "Delete this station?")
    val eventTitle get() = t("इभेन्टको शीर्षक", "Event title")
    val eventDesc get() = t("इभेन्टको विवरण", "Event description")
    val eventDate get() = t("मिति", "Date")
    val addEvent get() = t("इभेन्ट थप्नुहोस्", "Add event")
    val announcementTitle get() = t("घोषणाको शीर्षक", "Announcement title")
    val announcementText get() = t("घोषणाको विवरण", "Announcement message")
    val addAnnouncement get() = t("घोषणा थप्नुहोस्", "Add announcement")
    val reply get() = t("जवाफ", "Reply")
    val replyHint get() = t("जवाफ लेख्नुहोस्…", "Write a reply…")
    val markResolved get() = t("समाधान भयो भन्नुहोस्", "Mark resolved")
    val notifTitleLabel get() = t("सूचनाको शीर्षक", "Notification title")
    val notifBodyLabel get() = t("सूचनाको सन्देश", "Notification message")
    val notifTypeLabel get() = t("सूचनाको प्रकार", "Notification type")
    val sendNow get() = t("अहिले पठाउनुहोस्", "Send now")
    val notifSentLocal get() = t("सूचना यो उपकरणमा पठाइयो (डेमो)। ब्याकएन्ड जडान भएपछि सबै प्रयोगकर्तामा पुग्छ।", "Notification delivered on this device (demo). It reaches all users once the backend push service is connected.")
    val featureFlags get() = t("फिचर फ्ल्यागहरू", "Feature flags")
    val liveRadioEnabled get() = t("लाइभ रेडियो", "Live radio")
    val newsEnabled get() = t("समाचार", "News")
    val calendarEnabled get() = t("पात्रो", "Calendar")
    val helpdeskEnabled get() = t("हेल्पडेस्क", "Helpdesk")
    val postsEnabled get() = t("पोस्टहरू", "Posts")
    val stationsEnabled get() = t("स्टेशनहरू", "Stations")
    val socialEnabled get() = t("सामाजिक फिड", "Social feed")
    val maintenanceMode get() = t("मर्मत मोड", "Maintenance mode")
    val primaryStreamUrl get() = t("प्राथमिक स्ट्रिम URL", "Primary stream URL")
    val primaryStationName get() = t("प्राथमिक स्टेशनको नाम", "Primary station name")
    val homeBannerText get() = t("गृह ब्यानर", "Home banner")
    val currentProgramLabel get() = t("चलिरहेको कार्यक्रम", "Current programme")
    val contactPhoneLabel get() = t("सम्पर्क फोन", "Contact phone")
    val contactEmailLabel get() = t("सम्पर्क इमेल", "Contact email")
    val contactWhatsappLabel get() = t("व्हाट्सएप नम्बर", "WhatsApp number")
    val contactWebsiteLabel get() = t("वेबसाइट", "Website")
    val aboutTextLabel get() = t("हाम्रोबारे पाठ", "About text")
    val appLogoUrlLabel get() = t("एप लोगो URL", "App logo URL")
    val configSaved get() = t("कन्फिगरेसन सुरक्षित भयो", "Configuration saved")
    val aiNeedsBackend get() = t(
        "AI सहायकलाई सुरक्षित ब्याकएन्ड सर्भर चाहिन्छ — OpenAI कुञ्जी कहिल्यै एप भित्र राखिँदैन। ब्याकएन्ड जडान गरेपछि यहाँबाट शीर्षक, सारांश, अनुवाद, रेडियो स्क्रिप्ट र ड्राफ्ट बनाउन सकिन्छ।",
        "The AI assistant requires the secure backend server — the OpenAI key is never stored inside the app. Once the backend is connected you can generate headlines, summaries, translations, radio scripts and drafts here."
    )
    val aiActionHeadline get() = t("शीर्षक बनाउनुहोस्", "Generate headline")
    val aiActionSummarize get() = t("समाचार छोटो बनाउनुहोस्", "Summarise news")
    val aiActionRewrite get() = t("पुनः लेख्नुहोस्", "Rewrite news")
    val aiActionTranslate get() = t("अनुवाद गर्नुहोस्", "Translate news")
    val aiActionScript get() = t("रेडियो स्क्रिप्ट बनाउनुहोस्", "Generate radio script")
    val aiActionDraft get() = t("समाचार ड्राफ्ट बनाउनुहोस्", "Create news draft")
    val aiInputPlaceholder get() = t("सामग्री वा निर्देशन लेख्नुहोस्…", "Paste content or instructions…")
    val aiGenerate get() = t("बनाउनुहोस्", "Generate")
    val aiResult get() = t("नतिजा", "Result")
    val aiApprovePublish get() = t("स्वीकृत गरी प्रकाशित गर्नुहोस्", "Approve & publish")
    val aiRequiresApproval get() = t(
        "AI ले बनाएको सामग्री प्रकाशन अघि एडमिन स्वीकृति अनिवार्य छ।",
        "AI-generated content always requires administrator approval before publication."
    )
    val aiSaveAsDraft get() = t("ड्राफ्टका रूपमा सुरक्षित गर्नुहोस्", "Save as draft")
    val aiDraftSaved get() = t("ड्राफ्ट सुरक्षित भयो — समाचार व्यवस्थापनबाट सम्पादन गर्नुहोस्", "Draft saved — edit it in Manage news")
    val manageHome get() = t("गृह व्यवस्थापन", "Home management")
    val platform get() = t("प्लेटफर्म", "Platform")
    val urlLabel get() = t("URL", "URL")
    val label get() = t("लेबल", "Label")
    val addLink get() = t("लिङ्क थप्नुहोस्", "Add link")
    val saved get() = t("सुरक्षित भयो", "Saved")
    val deleteConfirm get() = t("मेट्ने हो?", "Delete?")
    val noTickets get() = t("कुनै अनुरोध आएको छैन", "No requests yet")
    val ticket get() = t("अनुरोध", "Request")
    val adminAuthorized get() = t("एडमिन पहुँच प्राप्त", "Administrator access granted")

    // ---------- Maintenance ----------
    val maintenanceTitle get() = t("सर्भर मर्मतमा छ", "Under maintenance")
    val maintenanceDesc get() = t(
        "एडमिनले सर्भर मर्मत मोडमा राख्नुभएको छ। केही समयपछि फेरि प्रयास गर्नुहोस्।",
        "The administrator has enabled maintenance mode. Please check back soon."
    )
    val tryAgainLater get() = t("फेरि प्रयास गर्नुहोस्", "Try again")

    // ---------- Station details / team ----------
    val stationDetails get() = t("स्टेशन विवरण", "Station details")
    val ourTeam get() = t("हाम्रो टोली", "Our team")
    val stationManagerRole get() = t("स्टेशन प्रमुख", "Station Manager")
    val technicianRole get() = t("प्राविधिक", "Technician")
    val marketingManagerRole get() = t("मार्केटिङ प्रमुख", "Marketing Manager")
    val address get() = t("ठेगाना", "Address")
    val frequency get() = t("फ्रिक्वेन्सी", "Frequency")

    // ---------- Stream status ----------
    val pleaseWait get() = t("कृपया प्रतीक्षा गर्नुहोस्…", "Please wait…")
    val checkingStream get() = t("जाँच हुँदै…", "Checking…")
    val offlineStation get() = t("अफलाइन — स्टेशन अहिले उपलब्ध छैन", "Offline — station not available right now")

    /** "x listening" badge text. */
    fun listeningCount(n: Int): String = if (nepali) {
        com.radioshuddhodhan.app.core.nepalidate.BsCalendar.toNepaliDigits(n.toString()) + " जना सुन्दै"
    } else {
        "$n listening"
    }

    // ---------- Hidden owner section (Settings) ----------
    val adminConsoleDesc get() = t(
        "स्टेशन मालिकको खाता — सबै व्यवस्थापन उपकरणहरू यहाँ छन्।",
        "Station owner account — all management tools live here."
    )
    val openAdminDashboard get() = t("ड्यासबोर्ड खोल्नुहोस्", "Open dashboard")
}
