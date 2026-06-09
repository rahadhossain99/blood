package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.model.BloodRequest
import com.example.data.model.Donor
import com.example.data.repository.BloodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Simple container for Google accounts
data class GoogleAccount(
    val email: String,
    val name: String,
    val imageUrlPlaceholder: Int // avatar number or identifier
)

class BloodViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "blood_donation_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = BloodRepository(db.donorDao(), db.bloodRequestDao())

    // Simulated Google Accounts
    val availableGoogleAccounts = listOf(
        GoogleAccount("rahadhossain991@gmail.com", "Rahad Hossain", 1),
        GoogleAccount("donor.shikha@gmail.com", "Shikha Rahman", 4),
        GoogleAccount("blood.hero@gmail.com", "Anika Ahmed", 7)
    )

    // Logged in email state
    private val _loggedInEmail = MutableStateFlow<String?>(null)
    val loggedInEmail = _loggedInEmail.asStateFlow()

    // Flag to denote if Google accounts modal/picker is shown
    private val _showGooglePicker = MutableStateFlow(false)
    val showGooglePicker = _showGooglePicker.asStateFlow()

    // Search & Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedBloodGroup = MutableStateFlow("সব")
    val selectedBloodGroup = _selectedBloodGroup.asStateFlow()

    private val _selectedDivision = MutableStateFlow("সব")
    val selectedDivision = _selectedDivision.asStateFlow()

    // Collect profile data reactively
    val currentUserProfile: StateFlow<Donor?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Flow for public blood requests
    val bloodRequests: StateFlow<List<BloodRequest>> = repository.bloodRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combine donors list with search filters Reactively
    val filteredDonors: StateFlow<List<Donor>> = combine(
        repository.otherDonors,
        _searchQuery,
        _selectedBloodGroup,
        _selectedDivision
    ) { donors, query, groupFilter, divisionFilter ->
        donors.filter { donor ->
            // 1. Blood Group filter
            val matchGroup = groupFilter == "সব" || donor.bloodGroup.equals(groupFilter, ignoreCase = true)
            // 2. Division filter
            val matchDivision = divisionFilter == "সব" || donor.division.equals(divisionFilter, ignoreCase = true)
            // 3. Text Search query (matches name, area, or phone)
            val matchQuery = query.isEmpty() ||
                    donor.name.contains(query, ignoreCase = true) ||
                    donor.area.contains(query, ignoreCase = true) ||
                    donor.phone.contains(query, ignoreCase = true)

            matchGroup && matchDivision && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            // Populate mock donors on fresh DB launch
            repository.setupMockDonorsIfEmpty()
        }
    }

    fun openGooglePicker() {
        _showGooglePicker.value = true
    }

    fun closeGooglePicker() {
        _showGooglePicker.value = false
    }

    fun selectGoogleAccount(account: GoogleAccount) {
        _loggedInEmail.value = account.email
        _showGooglePicker.value = false

        // Check if user already has a profile registered under this email
        viewModelScope.launch {
            val existingProfile = repository.getDonorByEmail(account.email)
            if (existingProfile != null) {
                // Restore profile as current user
                repository.saveCurrentUser(existingProfile.copy(isCurrentUser = true))
            } else {
                // Initialize clean slate profile with defaults representing the Google account info
                repository.saveCurrentUser(
                    Donor(
                        name = account.name,
                        bloodGroup = "O+",
                        division = "Dhaka",
                        area = "",
                        phone = "",
                        email = account.email,
                        avatarId = account.imageUrlPlaceholder,
                        isAvailable = true,
                        lastDonationDate = "কখনো নয়",
                        isCurrentUser = true
                    )
                )
            }
        }
    }

    fun saveProfile(
        name: String,
        bloodGroup: String,
        division: String,
        area: String,
        phone: String,
        avatarId: Int,
        isAvailable: Boolean,
        lastDonationDate: String,
        customAvatarUrl: String = ""
    ) {
        viewModelScope.launch {
            val currentMail = _loggedInEmail.value ?: ""
            val updatedProfile = Donor(
                name = name,
                bloodGroup = bloodGroup,
                division = division,
                area = area,
                phone = phone,
                email = currentMail,
                avatarId = avatarId,
                isAvailable = isAvailable,
                lastDonationDate = lastDonationDate,
                isCurrentUser = true,
                customAvatarUrl = customAvatarUrl
            )
            repository.saveCurrentUser(updatedProfile)
        }
    }

    fun postBloodRequest(
        patientName: String,
        bloodGroup: String,
        hospitalName: String,
        division: String,
        area: String,
        phone: String,
        neededDate: String,
        neededTime: String,
        details: String
    ) {
        viewModelScope.launch {
            val applicantName = currentUserProfile.value?.name ?: "অপরিচিত ব্যবহারকারী"
            val request = BloodRequest(
                patientName = patientName,
                bloodGroup = bloodGroup,
                hospitalName = hospitalName,
                division = division,
                area = area,
                phone = phone,
                neededDate = neededDate,
                neededTime = neededTime,
                details = details,
                requestedBy = applicantName
            )
            repository.addBloodRequest(request)
        }
    }

    fun removeBloodRequest(request: BloodRequest) {
        viewModelScope.launch {
            repository.deleteBloodRequest(request)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _loggedInEmail.value = null
            // We can delete the currentUser row or simply log out
            // Let's clear current user from current_user flag or keep it, deleting ensures clean slate log out
            val current = currentUserProfile.value
            if (current != null) {
                // Save it back without isCurrentUser = true, so they can log back in later
                repository.saveCurrentUser(current.copy(isCurrentUser = false))
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBloodGroupFilter(group: String) {
        _selectedBloodGroup.value = group
    }

    fun setDivisionFilter(division: String) {
        _selectedDivision.value = division
    }

    override fun onCleared() {
        super.onCleared()
        db.close()
    }
}
