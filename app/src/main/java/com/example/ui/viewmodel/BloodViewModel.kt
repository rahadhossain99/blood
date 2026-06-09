package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.model.BloodRequest
import com.example.data.model.Donor
import com.example.data.repository.BloodRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AuthStep {
    EMAIL_INPUT,
    OTP_VERIFICATION,
    PASSWORD_SETUP, // Set name and password for fresh registration
    PASSWORD_LOGIN  // Enter password to login
}

class BloodViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "blood_donation_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = BloodRepository(db.donorDao(), db.bloodRequestDao())

    // Logged in email state
    private val _loggedInEmail = MutableStateFlow<String?>(null)
    val loggedInEmail = _loggedInEmail.asStateFlow()

    // Search & Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedBloodGroup = MutableStateFlow("সব")
    val selectedBloodGroup = _selectedBloodGroup.asStateFlow()

    private val _selectedDivision = MutableStateFlow("সব")
    val selectedDivision = _selectedDivision.asStateFlow()

    // Email-Password OTP Custom Auth State
    private val _authStep = MutableStateFlow(AuthStep.EMAIL_INPUT)
    val authStep = _authStep.asStateFlow()

    private val _authEmail = MutableStateFlow("")
    val authEmail = _authEmail.asStateFlow()

    private val _generatedOtp = MutableStateFlow("")
    val generatedOtp = _generatedOtp.asStateFlow()

    private val _authIsLoading = MutableStateFlow(false)
    val authIsLoading = _authIsLoading.asStateFlow()

    private val _authErrorMsg = MutableStateFlow<String?>(null)
    val authErrorMsg = _authErrorMsg.asStateFlow()

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
            // Restore session if Firebase Auth already has a logged-in user
            try {
                val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
                if (currentFirebaseUser != null && !currentFirebaseUser.email.isNullOrBlank()) {
                    _loggedInEmail.value = currentFirebaseUser.email
                    restoreLocalOrCreateDefault(currentFirebaseUser.email!!)
                }
            } catch (e: Exception) {
                // Ignore if not initialized
            }

            // Populate mock donors on fresh DB launch
            repository.setupMockDonorsIfEmpty()

            // Setup real-time Firebase Firestore Synchronizer
            setupFirestoreRealtimeSync()
        }
    }

    // Connect real-time listeners to Firestore database collections
    private fun setupFirestoreRealtimeSync() {
        try {
            val firestore = FirebaseFirestore.getInstance()

            // 1. Real-time Donors Synchronization
            firestore.collection("donors")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("BloodViewModel", "Firestore sync error", e)
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        viewModelScope.launch {
                            for (doc in snapshots.documents) {
                                val email = doc.getString("email") ?: continue
                                if (email == _loggedInEmail.value) continue // Do not overwrite logged in user profile mid-session

                                val name = doc.getString("name") ?: ""
                                val bloodGroup = doc.getString("bloodGroup") ?: "O+"
                                val division = doc.getString("division") ?: "Dhaka"
                                val area = doc.getString("area") ?: ""
                                val phone = doc.getString("phone") ?: ""
                                val avatarId = doc.getLong("avatarId")?.toInt() ?: 1
                                val isAvailable = doc.getBoolean("isAvailable") ?: true
                                val lastDonationDate = doc.getString("lastDonationDate") ?: "কখনো নয়"
                                val customAvatarUrl = doc.getString("customAvatarUrl") ?: ""

                                val donorObj = Donor(
                                    name = name,
                                    bloodGroup = bloodGroup,
                                    division = division,
                                    area = area,
                                    phone = phone,
                                    email = email,
                                    avatarId = avatarId,
                                    isAvailable = isAvailable,
                                    lastDonationDate = lastDonationDate,
                                    isCurrentUser = false,
                                    customAvatarUrl = customAvatarUrl
                                )

                                val existing = repository.getDonorByEmail(email)
                                if (existing != null) {
                                    db.donorDao().insertDonor(donorObj.copy(id = existing.id))
                                } else {
                                    db.donorDao().insertDonor(donorObj)
                                }
                            }
                        }
                    }
                }

            // 2. Real-time Blood Requests Synchronization
            firestore.collection("blood_requests")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("BloodViewModel", "Firestore requests sync error", e)
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        viewModelScope.launch {
                            for (doc in snapshots.documents) {
                                val patientName = doc.getString("patientName") ?: continue
                                val bloodGroup = doc.getString("bloodGroup") ?: "O+"
                                val hospitalName = doc.getString("hospitalName") ?: ""
                                val division = doc.getString("division") ?: "Dhaka"
                                val area = doc.getString("area") ?: ""
                                val phone = doc.getString("phone") ?: ""
                                val neededDate = doc.getString("neededDate") ?: ""
                                val neededTime = doc.getString("neededTime") ?: ""
                                val details = doc.getString("details") ?: ""
                                val requestedBy = doc.getString("requestedBy") ?: ""
                                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                                val reqObj = BloodRequest(
                                    patientName = patientName,
                                    bloodGroup = bloodGroup,
                                    hospitalName = hospitalName,
                                    division = division,
                                    area = area,
                                    phone = phone,
                                    neededDate = neededDate,
                                    neededTime = neededTime,
                                    details = details,
                                    requestedBy = requestedBy,
                                    timestamp = timestamp
                                )

                                val currentRequests = repository.bloodRequests.first()
                                val isDuplicate = currentRequests.any { 
                                    it.phone == phone && 
                                    it.patientName == patientName && 
                                    it.neededDate == neededDate 
                                }
                                if (!isDuplicate) {
                                    repository.addBloodRequest(reqObj)
                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w("BloodViewModel", "Firestore not configured or unavailable: ${e.message}")
        }
    }

    // New Custom Email authentication functions
    fun resetAuthFlow() {
        _authStep.value = AuthStep.EMAIL_INPUT
        _authEmail.value = ""
        _generatedOtp.value = ""
        _authIsLoading.value = false
        _authErrorMsg.value = null
    }

    fun setAuthStep(step: AuthStep) {
        _authStep.value = step
    }

    fun setAuthError(err: String?) {
        _authErrorMsg.value = err
    }

    fun checkAndSendVerificationCode(email: String, onResult: (isRegistered: Boolean) -> Unit) {
        _authIsLoading.value = true
        _authErrorMsg.value = null
        _authEmail.value = email

        // Generate custom 6 digit OTP Code
        val randomOtp = (100000..999999).random().toString()
        _generatedOtp.value = randomOtp

        viewModelScope.launch {
            var exists = false
            try {
                // Check local Room cache first
                val existing = repository.getDonorByEmail(email)
                if (existing != null) {
                    exists = true
                }
            } catch (e: Exception) {
                // Ignore local DB error
            }

            try {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("donors").document(email).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            exists = true
                        }
                        _authIsLoading.value = false
                        onResult(exists)
                    }
                    .addOnFailureListener {
                        _authIsLoading.value = false
                        onResult(exists)
                    }
            } catch (e: Exception) {
                _authIsLoading.value = false
                onResult(exists)
            }
        }
    }

    fun signUpWithEmailPassword(
        email: String,
        name: String,
        password: String,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        _authIsLoading.value = true
        _authErrorMsg.value = null

        try {
            val auth = FirebaseAuth.getInstance()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            val newDonor = Donor(
                                name = name,
                                bloodGroup = "O+",
                                division = "ঢাকা",
                                area = "যশোর সদর",
                                phone = "",
                                email = email,
                                avatarId = (1..10).random(),
                                isAvailable = true,
                                lastDonationDate = "কখনো নয়",
                                isCurrentUser = true
                            )
                            repository.saveCurrentUser(newDonor)
                            uploadUserProfileToFirestore(newDonor)
                            _loggedInEmail.value = email
                            _authIsLoading.value = false
                            onComplete()
                        }
                    } else {
                        val err = task.exception?.localizedMessage ?: "অ্যাকাউন্ট তৈরি করা ব্যর্থ হয়েছে"
                        _authErrorMsg.value = err
                        _authIsLoading.value = false
                        onError(err)
                    }
                }
        } catch (e: Exception) {
            // Sandboxed emulator backup flow
            viewModelScope.launch {
                val newDonor = Donor(
                    name = name,
                    bloodGroup = "O+",
                    division = "ঢাকা",
                    area = "যশোর সদর",
                    phone = "",
                    email = email,
                    avatarId = (1..10).random(),
                    isAvailable = true,
                    lastDonationDate = "কখনো নয়",
                    isCurrentUser = true
                )
                repository.saveCurrentUser(newDonor)
                _loggedInEmail.value = email
                _authIsLoading.value = false
                onComplete()
            }
        }
    }

    fun signInWithEmailPassword(
        email: String,
        password: String,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        _authIsLoading.value = true
        _authErrorMsg.value = null

        try {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            _loggedInEmail.value = email
                            try {
                                val firestore = FirebaseFirestore.getInstance()
                                firestore.collection("donors").document(email).get()
                                    .addOnSuccessListener { doc ->
                                        viewModelScope.launch {
                                            if (doc.exists()) {
                                                val donor = Donor(
                                                    name = doc.getString("name") ?: "দাতা",
                                                    bloodGroup = doc.getString("bloodGroup") ?: "O+",
                                                    division = doc.getString("division") ?: "ঢাকা",
                                                    area = doc.getString("area") ?: "",
                                                    phone = doc.getString("phone") ?: "",
                                                    email = email,
                                                    avatarId = doc.getLong("avatarId")?.toInt() ?: 1,
                                                    isAvailable = doc.getBoolean("isAvailable") ?: true,
                                                    lastDonationDate = doc.getString("lastDonationDate") ?: "কখনো নয়",
                                                    isCurrentUser = true,
                                                    customAvatarUrl = doc.getString("customAvatarUrl") ?: ""
                                                )
                                                repository.saveCurrentUser(donor)
                                            } else {
                                                restoreLocalOrCreateDefault(email)
                                            }
                                            _authIsLoading.value = false
                                            onComplete()
                                        }
                                    }
                                    .addOnFailureListener {
                                        viewModelScope.launch {
                                            restoreLocalOrCreateDefault(email)
                                            _authIsLoading.value = false
                                            onComplete()
                                        }
                                    }
                            } catch (e: Exception) {
                                viewModelScope.launch {
                                    restoreLocalOrCreateDefault(email)
                                    _authIsLoading.value = false
                                    onComplete()
                                }
                            }
                        }
                    } else {
                        val err = task.exception?.localizedMessage ?: "পাসওয়ার্ড সঠিক নয়"
                        _authErrorMsg.value = err
                        _authIsLoading.value = false
                        onError(err)
                    }
                }
        } catch (e: Exception) {
            // Sandboxed emulator backup flow
            viewModelScope.launch {
                _loggedInEmail.value = email
                restoreLocalOrCreateDefault(email)
                _authIsLoading.value = false
                onComplete()
            }
        }
    }

    private suspend fun restoreLocalOrCreateDefault(email: String) {
        val existing = repository.getDonorByEmail(email)
        if (existing != null) {
            repository.saveCurrentUser(existing.copy(isCurrentUser = true))
        } else {
            repository.saveCurrentUser(
                Donor(
                    name = email.substringBefore("@"),
                    bloodGroup = "O+",
                    division = "ঢাকা",
                    area = "যশোর সদর",
                    phone = "",
                    email = email,
                    avatarId = (1..10).random(),
                    isAvailable = true,
                    lastDonationDate = "কখনো নয়",
                    isCurrentUser = true
                )
            )
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

            // Real-time Firestore write-sync!
            uploadUserProfileToFirestore(updatedProfile)
        }
    }

    private fun uploadUserProfileToFirestore(donor: Donor) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val donorMap = mapOf(
                "name" to donor.name,
                "bloodGroup" to donor.bloodGroup,
                "division" to donor.division,
                "area" to donor.area,
                "phone" to donor.phone,
                "email" to donor.email,
                "avatarId" to donor.avatarId,
                "isAvailable" to donor.isAvailable,
                "lastDonationDate" to donor.lastDonationDate,
                "customAvatarUrl" to donor.customAvatarUrl,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("donors").document(donor.email).set(donorMap)
        } catch (e: Exception) {
            Log.e("BloodViewModel", "Upload profile failed: ${e.message}")
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

            // Real-time Firestore write-sync!
            uploadBloodRequestToFirestore(request)
        }
    }

    private fun uploadBloodRequestToFirestore(request: BloodRequest) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val reqMap = mapOf(
                "patientName" to request.patientName,
                "bloodGroup" to request.bloodGroup,
                "hospitalName" to request.hospitalName,
                "division" to request.division,
                "area" to request.area,
                "phone" to request.phone,
                "neededDate" to request.neededDate,
                "neededTime" to request.neededTime,
                "details" to request.details,
                "requestedBy" to request.requestedBy,
                "timestamp" to request.timestamp
            )
            firestore.collection("blood_requests").add(reqMap)
        } catch (e: Exception) {
            Log.e("BloodViewModel", "Upload request failed: ${e.message}")
        }
    }

    fun removeBloodRequest(request: BloodRequest) {
        viewModelScope.launch {
            repository.deleteBloodRequest(request)
            // Optionally remove from firestore if ID is matched
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().signOut()
            } catch (e: Exception) {
                // Keep sign out robust
            }

            _loggedInEmail.value = null
            resetAuthFlow()

            val current = currentUserProfile.value
            if (current != null) {
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
