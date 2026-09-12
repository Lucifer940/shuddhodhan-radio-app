package com.radioshuddhodhan.app.data.seed

import com.radioshuddhodhan.app.core.nepalidate.BsCalendar
import com.radioshuddhodhan.app.data.db.AppDatabase
import com.radioshuddhodhan.app.data.db.AnnouncementEntity
import com.radioshuddhodhan.app.data.db.EventEntity
import com.radioshuddhodhan.app.data.db.NewsEntity
import com.radioshuddhodhan.app.data.db.PostEntity
import com.radioshuddhodhan.app.data.db.SocialLinkEntity
import com.radioshuddhodhan.app.data.db.StationEntity
import java.time.LocalDate

/**
 * First-launch demo content so Radio Shuddhodhan is immediately usable.
 * Everything seeded here is fully editable/removable from the Admin console.
 *
 * Stream note: public demo stream URLs (SomaFM, public internet radio) are
 * used for the sample stations. The Radio Shuddhodhan primary stream URL is
 * intentionally left empty — the administrator sets the real stream from the
 * Admin console (no hard-coded permanent stream in the APK).
 */
object SeedData {

    suspend fun seedIfNeeded(db: AppDatabase, isSeeded: Boolean) {
        if (isSeeded) return
        val now = System.currentTimeMillis()
        val today = BsCalendar.todayInNepal()
        val day = 86_400_000L

        // ---------------- Stations ----------------
        db.stationDao().upsertAll(
            listOf(
                StationEntity(
                    id = "st-shuddhodhan",
                    name = "Radio Shuddhodhan 95.1 MHz",
                    nameNe = "रेडियो शुद्धोधन 95.1 मेगाहर्ज",
                    description = "तपाईंको समुदायको आवाज — समाचार, संगीत र जानकारी। The voice of your community.",
                    streamUrl = "", // Admin configures the real stream URL
                    logoUrl = null,
                    isEnabled = true,
                    isFeatured = true,
                    sortOrder = 0,
                    source = "local"
                ),
                StationEntity(
                    id = "st-demo-1",
                    name = "Groove Salad (Demo)",
                    nameNe = "ग्रुभ सलाड (डेमो)",
                    description = "अन्तर्राष्ट्रिय नमूना स्टेशन — शान्त संगीत। Sample ambient station for testing.",
                    streamUrl = "https://ice1.somafm.com/groovesalad-128-mp3",
                    logoUrl = null,
                    isEnabled = true,
                    isFeatured = false,
                    sortOrder = 1,
                    source = "local"
                ),
                StationEntity(
                    id = "st-demo-2",
                    name = "Indie Pop (Demo)",
                    nameNe = "इन्डी पप (डेमो)",
                    description = "नमूना स्टेशन — इन्डी संगीत। Sample indie station for testing.",
                    streamUrl = "https://ice1.somafm.com/indiepop-128-mp3",
                    logoUrl = null,
                    isEnabled = true,
                    isFeatured = false,
                    sortOrder = 2,
                    source = "local"
                ),
                StationEntity(
                    id = "st-demo-3",
                    name = "Secret Agent (Demo)",
                    nameNe = "सिक्रेट एजेन्ट (डेमो)",
                    description = "नमूना स्टेशन — लाउन्ज संगीत। Sample lounge station for testing.",
                    streamUrl = "https://ice2.somafm.com/secretagent-128-mp3",
                    logoUrl = null,
                    isEnabled = true,
                    isFeatured = false,
                    sortOrder = 3,
                    source = "local"
                )
            )
        )

        // ---------------- News ----------------
        db.newsDao().upsertAll(
            listOf(
                NewsEntity(
                    id = "seed-news-1",
                    title = "Radio Shuddhodhan एप आधिकारिक रूपमा सार्वजनिक",
                    summary = "समुदायको आवाज अब तपाईंको नामा। लाइभ रेडियो, समाचार, नेपाली पात्रो र हेल्पडेस्क एउटै एपमा।",
                    content = "Radio Shuddhodhan मोबाइल एप आधिकारिक रूपमा सार्वजनिक भएको छ। एपमार्फत लाइभ रेडियो प्रसारण सुन्न, ताजा समाचार पढ्न, विक्रम सम्बत् पात्रो हेर्न र हेल्पडेस्कमा सम्पर्क गर्न सकिन्छ।\n\nएप v1.0.1 निर्माता उमेश थारू हुन्। एपमा नेपाली र अंग्रेजी दुवै भाषामा नेपाली मिति र समय हेर्न मिल्छ। लाइभ प्रसारण इन्टरनेट जडान भएको अवस्थामा जहाँबाट पनि सुन्न सकिन्छ।",
                    category = "समाचार",
                    imageUrl = null,
                    author = "Radio Shuddhodhan",
                    publishedAt = now - day,
                    updatedAt = now - day,
                    isPublished = true,
                    isFeatured = true,
                    isBreaking = false,
                    source = "local"
                ),
                NewsEntity(
                    id = "seed-news-2",
                    title = "ब्रेकिङ: स्थानीय समाचार प्रणाली सञ्चालनमा",
                    summary = "एपमार्फत तत्कालै ब्रेकिङ समाचार सूचना पठाउने प्रणाली सञ्चालनमा आएको छ।",
                    content = "एडमिनले ब्रेकिङ समाचार पठाउँदा प्रयोगकर्ताको फोनमा तुरुन्तै सूचना देखिने व्यवस्था मिलाइएको छ। सूचना सेटिङबाट प्राथमिकता छान्न सकिन्छ।",
                    category = "समाचार",
                    imageUrl = null,
                    author = "Radio Shuddhodhan",
                    publishedAt = now - day / 2,
                    updatedAt = now - day / 2,
                    isPublished = true,
                    isFeatured = false,
                    isBreaking = true,
                    source = "local"
                ),
                NewsEntity(
                    id = "seed-news-3",
                    title = "कार्यक्रम तालिका: साँझको विशेष कुराकानी",
                    summary = "हरेक साँझ ७ बजे समुदायका विषयमा विशेष कुराकानी कार्यक्रम प्रसारण हुन्छ।",
                    content = "हरेक साँझ ७ बजे 'समुदायको मञ्च' कार्यक्रममा स्थानीय विषयवस्तुमा छलफल हुन्छ। सुझाव र विचार हेल्पडेस्कमार्फत पठाउन सक्नुहुन्छ।",
                    category = "कार्यक्रम",
                    imageUrl = null,
                    author = "Radio Shuddhodhan",
                    publishedAt = now - 2 * day,
                    updatedAt = now - 2 * day,
                    isPublished = true,
                    isFeatured = false,
                    isBreaking = false,
                    source = "local"
                ),
                NewsEntity(
                    id = "seed-news-4",
                    title = "मौसम अपडेट र दैनिक जानकारी",
                    summary = "बिहानको प्रसारणमा मौसम विवरण र दैनिक जानकारी सुन्न पाउनुहुन्छ।",
                    content = "प्रत्येक बिहान ७ बजेको प्रसारणमा मौसम विवरण, मूल्य विवरण र दैनिक जानकारी प्रसारण गरिन्छ। सुन्न लाइभ रेडियो बटन थिच्नुहोस्।",
                    category = "जानकारी",
                    imageUrl = null,
                    author = "Radio Shuddhodhan",
                    publishedAt = now - 3 * day,
                    updatedAt = now - 3 * day,
                    isPublished = true,
                    isFeatured = false,
                    isBreaking = false,
                    source = "local"
                )
            )
        )

        // ---------------- Posts ----------------
        db.postDao().upsertAll(
            listOf(
                PostEntity(
                    id = "seed-post-1",
                    title = "रेडियोको इतिहास: समुदायसँगको यात्रा",
                    summary = "Radio Shuddhodhan कसरी समुदायको विश्वसनीय स्रोत बन्यो — हाम्रो यात्राको कथा।",
                    content = "सुरुवातमा सानो समुदाय रेडियोका रूपमा यात्रा सुरु गरेको Radio Shuddhodhan आज समुदायको घर-घरमा पुगेको छ। यो पोस्टमा हाम्रो यात्रा, सिकाइ र भविष्यको योजना बारे लेखिएको छ।",
                    imageUrl = null,
                    author = "Radio Shuddhodhan",
                    publishedAt = now - 4 * day,
                    updatedAt = now - 4 * day,
                    isPublished = true,
                    isFeatured = true,
                    source = "local"
                ),
                PostEntity(
                    id = "seed-post-2",
                    title = "स्वयंसेवक खोजिँदै",
                    summary = "रेडियो कार्यक्रममा सहयोग गर्न इच्छुक स्वयंसेवकहरूका लागि अवसर।",
                    content = "कार्यक्रम निर्माण, प्रस्तुति र प्राविधिक काममा सहयोग गर्न इच्छुक स्वयंसेवकहरूले हेल्पडेस्कमार्फत सम्पर्क गर्नुहोस्। तालिम र सिकाइको अवसर पनि उपलब्ध छ।",
                    imageUrl = null,
                    author = "Radio Shuddhodhan",
                    publishedAt = now - 6 * day,
                    updatedAt = now - 6 * day,
                    isPublished = true,
                    isFeatured = false,
                    source = "local"
                )
            )
        )

        // ---------------- Events ----------------
        val eventDates = listOf(
            today.plusDays(3), today.plusDays(10), today.plusDays(21), today.plusDays(35)
        )
        val eventTitles = listOf(
            "समुदाय संगीत रात / Community Music Night",
            "स्वास्थ्य जागरण कार्यक्रम / Health Awareness Program",
            "विद्यालय छलफल / School Discussion",
            "मासिक खुला मञ्च / Monthly Open Mic"
        )
        db.eventDao().upsertAll(
            eventDates.mapIndexed { index, date ->
                EventEntity(
                    id = "seed-event-${index + 1}",
                    title = eventTitles[index],
                    titleNe = eventTitles[index].substringBefore(" /"),
                    description = "रेडियो स्टुडियोबाट प्रत्यक्ष प्रसारण। Broadcast live from the radio studio.",
                    adDate = date.toString(),
                    timeLabel = "19:00",
                    location = "Radio Shuddhodhan Studio",
                    isFeatured = index == 0,
                    source = "local"
                )
            }
        )

        // ---------------- Announcements ----------------
        db.announcementDao().upsertAll(
            listOf(
                AnnouncementEntity(
                    id = "seed-ann-1",
                    title = "नयाँ एप सार्वजनिक",
                    message = "Radio Shuddhodhan एप अब डाउनलोडका लागि उपलब्ध छ। सबैलाई स्वागत छ!",
                    createdAt = now,
                    activeUntil = now + 30 * day,
                    isActive = true,
                    source = "local"
                )
            )
        )

        // ---------------- Social links (placeholders for the admin) ----------------
        db.socialLinkDao().upsertAll(
            listOf(
                SocialLinkEntity("soc-fb", "facebook", "Facebook", "https://www.facebook.com/share/1BjjUPuPdx/", 0, true, "local"),
                SocialLinkEntity("soc-fblive", "facebook_live", "Facebook Live", "https://www.facebook.com/share/1BjjUPuPdx/", 1, true, "local"),
                SocialLinkEntity("soc-yt", "youtube", "YouTube", "https://www.youtube.com/", 2, true, "local"),
                SocialLinkEntity("soc-web", "website", "Website", "https://www.facebook.com/share/1BjjUPuPdx/", 3, true, "local")
            )
        )
    }
}
