package com.radioshuddhodhan.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radioshuddhodhan.app.data.remote.AppConfigDto
import com.radioshuddhodhan.app.data.remote.TeamMemberDto
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import com.radioshuddhodhan.app.ui.theme.LiveRed

/**
 * Remote configuration editor — the server-side source of truth for feature
 * flags, primary stream, home banner, current programme and contact info.
 * Saved values immediately control what user devices show.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConfigScreen() {
    val L = LocalAppStrings.current
    val viewModel: AdminDataViewModel = appViewModel { AdminDataViewModel(it) }
    val config by viewModel.config.collectAsStateWithLifecycle()
    val toast by viewModel.toast.collectAsStateWithLifecycle()

    // Local editable copy
    var draft by remember(config) { mutableStateOf(config) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(L.remoteConfig, fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (toast == "configSaved") {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) { Text(L.configSaved, modifier = Modifier.padding(12.dp)) }
            }

            // Feature flags
            SectionCard(L.featureFlags) {
                ConfigSwitch(L.liveRadioEnabled, draft.liveRadioEnabled) {
                    draft = draft.copy(liveRadioEnabled = it)
                }
                ConfigSwitch(L.newsEnabled, draft.newsEnabled) { draft = draft.copy(newsEnabled = it) }
                ConfigSwitch(L.calendarEnabled, draft.calendarEnabled) {
                    draft = draft.copy(calendarEnabled = it)
                }
                ConfigSwitch(L.helpdeskEnabled, draft.helpdeskEnabled) {
                    draft = draft.copy(helpdeskEnabled = it)
                }
                ConfigSwitch(L.postsEnabled, draft.postsEnabled) { draft = draft.copy(postsEnabled = it) }
                ConfigSwitch(L.stationsEnabled, draft.stationsEnabled) {
                    draft = draft.copy(stationsEnabled = it)
                }
                ConfigSwitch(L.socialEnabled, draft.socialEnabled) { draft = draft.copy(socialEnabled = it) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(L.maintenanceMode, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Switch(checked = draft.maintenanceMode, onCheckedChange = {
                        draft = draft.copy(maintenanceMode = it)
                    })
                }
                if (draft.maintenanceMode) {
                    Text(
                        text = L.maintenanceDesc,
                        style = MaterialTheme.typography.labelSmall,
                        color = LiveRed
                    )
                }
            }

            // Live radio
            SectionCard(L.liveRadio) {
                OutlinedTextField(
                    value = draft.primaryStreamUrl,
                    onValueChange = { draft = draft.copy(primaryStreamUrl = it) },
                    label = { Text(L.primaryStreamUrl) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.primaryStationName,
                    onValueChange = { draft = draft.copy(primaryStationName = it) },
                    label = { Text(L.primaryStationName) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.currentProgram,
                    onValueChange = { draft = draft.copy(currentProgram = it) },
                    label = { Text(L.currentProgramLabel + " (EN)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.currentProgramNe,
                    onValueChange = { draft = draft.copy(currentProgramNe = it) },
                    label = { Text(L.currentProgramLabel + " (नेपाली)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Home & branding
            SectionCard(L.manageHome) {
                OutlinedTextField(
                    value = draft.homeBannerText,
                    onValueChange = { draft = draft.copy(homeBannerText = it) },
                    label = { Text(L.homeBannerText + " (EN)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.homeBannerTextNe,
                    onValueChange = { draft = draft.copy(homeBannerTextNe = it) },
                    label = { Text(L.homeBannerText + " (नेपाली)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.appLogoUrl,
                    onValueChange = { draft = draft.copy(appLogoUrl = it) },
                    label = { Text(L.appLogoUrlLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.aboutText,
                    onValueChange = { draft = draft.copy(aboutText = it) },
                    label = { Text(L.aboutTextLabel + " (EN)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.aboutTextNe,
                    onValueChange = { draft = draft.copy(aboutTextNe = it) },
                    label = { Text(L.aboutTextLabel + " (नेपाली)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Contact info
            SectionCard(L.contactInfo) {
                OutlinedTextField(
                    value = draft.contactPhone,
                    onValueChange = { draft = draft.copy(contactPhone = it) },
                    label = { Text(L.contactPhoneLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.contactEmail,
                    onValueChange = { draft = draft.copy(contactEmail = it) },
                    label = { Text(L.contactEmailLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.contactWhatsapp,
                    onValueChange = { draft = draft.copy(contactWhatsapp = it) },
                    label = { Text(L.contactWhatsappLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.contactWebsite,
                    onValueChange = { draft = draft.copy(contactWebsite = it) },
                    label = { Text(L.contactWebsiteLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Station details & team (shown on Home > Station details)
            SectionCard(L.stationDetails) {
                OutlinedTextField(
                    value = draft.stationFrequency,
                    onValueChange = { draft = draft.copy(stationFrequency = it) },
                    label = { Text(L.frequency) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.stationAddress,
                    onValueChange = { draft = draft.copy(stationAddress = it) },
                    label = { Text(L.address + " (EN)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.stationAddressNe,
                    onValueChange = { draft = draft.copy(stationAddressNe = it) },
                    label = { Text(L.address + " (नेपाली)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.operatorText,
                    onValueChange = { draft = draft.copy(operatorText = it) },
                    label = { Text("Operator (EN)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.operatorTextNe,
                    onValueChange = { draft = draft.copy(operatorTextNe = it) },
                    label = { Text("Operator (नेपाली)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.taglineNe,
                    onValueChange = { draft = draft.copy(taglineNe = it) },
                    label = { Text("Tagline (नेपाली)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.taglineSubNe,
                    onValueChange = { draft = draft.copy(taglineSubNe = it) },
                    label = { Text("Tagline line 2 (नेपाली)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.taglineEn,
                    onValueChange = { draft = draft.copy(taglineEn = it) },
                    label = { Text("Tagline (English)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.taglineSubEn,
                    onValueChange = { draft = draft.copy(taglineSubEn = it) },
                    label = { Text("Tagline line 2 (English)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
                Text(L.ourTeam, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                teamMemberEditor(
                    roleKey = "manager",
                    roleLabel = L.stationManagerRole,
                    team = draft.teamMembers
                ) { updated -> draft = draft.copy(teamMembers = updated) }
                teamMemberEditor(
                    roleKey = "technician",
                    roleLabel = L.technicianRole,
                    team = draft.teamMembers
                ) { updated -> draft = draft.copy(teamMembers = updated) }
                teamMemberEditor(
                    roleKey = "marketing",
                    roleLabel = L.marketingManagerRole,
                    team = draft.teamMembers
                ) { updated -> draft = draft.copy(teamMembers = updated) }
            }

            // Login options
            SectionCard(L.login) {
                ConfigSwitch(L.googleLogin, draft.googleLoginEnabled) {
                    draft = draft.copy(googleLoginEnabled = it)
                }
                ConfigSwitch(L.facebookLogin, draft.facebookLoginEnabled) {
                    draft = draft.copy(facebookLoginEnabled = it)
                }
                ConfigSwitch(L.phoneLogin, draft.phoneLoginEnabled) {
                    draft = draft.copy(phoneLoginEnabled = it)
                }
            }

            Button(
                onClick = { viewModel.saveConfig(draft) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(L.save)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun ConfigSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}


/** Edits one team role inside the team list, creating it when missing. */
private fun updateMember(
    team: List<TeamMemberDto>,
    roleKey: String,
    fallbackRole: String,
    fallbackRoleNe: String,
    transform: (TeamMemberDto) -> TeamMemberDto
): List<TeamMemberDto> {
    val idx = team.indexOfFirst { it.roleKey == roleKey }
    return if (idx >= 0) {
        team.toMutableList().also { it[idx] = transform(it[idx]) }.toList()
    } else {
        team + transform(
            TeamMemberDto(
                roleKey = roleKey, role = fallbackRole, roleNe = fallbackRoleNe,
                name = "", contact = "", sortOrder = team.size
            )
        )
    }
}

@Composable
private fun teamMemberEditor(
    roleKey: String,
    roleLabel: String,
    team: List<TeamMemberDto>,
    onUpdate: (List<TeamMemberDto>) -> Unit
) {
    val member = team.firstOrNull { it.roleKey == roleKey }
        ?: TeamMemberDto(roleKey = roleKey, role = roleLabel, roleNe = roleLabel)
    Column {
        Text(roleLabel, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = member.name,
            onValueChange = { name ->
                onUpdate(
                    updateMember(team, roleKey, roleLabel, roleLabel) { it.copy(name = name) }
                )
            },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = member.contact,
            onValueChange = { contact ->
                onUpdate(
                    updateMember(team, roleKey, roleLabel, roleLabel) { it.copy(contact = contact) }
                )
            },
            label = { Text("Contact") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
