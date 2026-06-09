package com.example.data.repository

import com.example.data.local.BloodRequestDao
import com.example.data.local.DonorDao
import com.example.data.model.BloodRequest
import com.example.data.model.Donor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BloodRepository(
    private val donorDao: DonorDao,
    private val bloodRequestDao: BloodRequestDao
) {
    val otherDonors: Flow<List<Donor>> = donorDao.getOtherDonorsFlow()
    val allDonors: Flow<List<Donor>> = donorDao.getAllDonorsFlow()
    val currentUser: Flow<Donor?> = donorDao.getCurrentUserFlow()
    val bloodRequests: Flow<List<BloodRequest>> = bloodRequestDao.getAllRequestsFlow()

    suspend fun setupMockDonorsIfEmpty() {
        if (donorDao.getOtherDonorCount() == 0) {
            val mockDonors = listOf(
                Donor(
                    name = "আরিয়ান রহমান",
                    bloodGroup = "O+",
                    division = "Dhaka",
                    area = "Mirpur 10",
                    phone = "+8801712345678",
                    email = "",
                    avatarId = 1,
                    isAvailable = true,
                    lastDonationDate = "২ মাস আগে"
                ),
                Donor(
                    name = "ফাহিম হাসান",
                    bloodGroup = "A+",
                    division = "Dhaka",
                    area = "Dhanmondi",
                    phone = "+8801823456789",
                    email = "",
                    avatarId = 2,
                    isAvailable = true,
                    lastDonationDate = "৩ মাস আগে"
                ),
                Donor(
                    name = "তাসনিম আহমেদশাওন",
                    bloodGroup = "B+",
                    division = "Sylhet",
                    area = "Zindabazar",
                    phone = "+8801934567890",
                    email = "",
                    avatarId = 3,
                    isAvailable = true,
                    lastDonationDate = "১ মাস আগে"
                ),
                Donor(
                    name = "আফরোজা সুলতানা আঁখি",
                    bloodGroup = "AB+",
                    division = "Chittagong",
                    area = "GEC Circle",
                    phone = "+8801545678901",
                    email = "",
                    avatarId = 4,
                    isAvailable = false,
                    lastDonationDate = "২ সপ্তাহ আগে"
                ),
                Donor(
                    name = "ইশতিয়াক হাসান",
                    bloodGroup = "O-",
                    division = "Rajshahi",
                    area = "Kazihata",
                    phone = "+8801656789012",
                    email = "",
                    avatarId = 5,
                    isAvailable = true,
                    lastDonationDate = "কখনো নয়"
                ),
                Donor(
                    name = "সালমান চৌধুরী",
                    bloodGroup = "B-",
                    division = "Khulna",
                    area = "Sajibari",
                    phone = "+8801367890123",
                    email = "",
                    avatarId = 6,
                    isAvailable = true,
                    lastDonationDate = "৪ মাস আগে"
                ),
                Donor(
                    name = "তাহসিনা মজুমদার",
                    bloodGroup = "A-",
                    division = "Chittagong",
                    area = "Agrabad",
                    phone = "+8801732145698",
                    email = "",
                    avatarId = 7,
                    isAvailable = true,
                    lastDonationDate = "৫ মাস আগে"
                ),
                Donor(
                    name = "আসিফ জামান রনি",
                    bloodGroup = "AB-",
                    division = "Sylhet",
                    area = "Subidbazar",
                    phone = "+8801845123987",
                    email = "",
                    avatarId = 8,
                    isAvailable = false,
                    lastDonationDate = "১ মাস আগে"
                ),
                Donor(
                    name = "মেহেদী হাসান শুভ",
                    bloodGroup = "O+",
                    division = "Dhaka",
                    area = "Uttara Sector 7",
                    phone = "+8801967234812",
                    email = "",
                    avatarId = 9,
                    isAvailable = true,
                    lastDonationDate = "২ মাস আগে"
                ),
                Donor(
                    name = "নুসরাত জাহান মিম",
                    bloodGroup = "A+",
                    division = "Barishal",
                    area = "Sadar Road",
                    phone = "+8801552341987",
                    email = "",
                    avatarId = 10,
                    isAvailable = true,
                    lastDonationDate = "কখনো নয়"
                )
            )
            donorDao.insertDonors(mockDonors)
        }
    }

    suspend fun saveCurrentUser(donor: Donor) {
        // Enforce update if exists or insert
        val existing = donorDao.getCurrentUser()
        if (existing != null) {
            donorDao.insertDonor(donor.copy(id = existing.id, isCurrentUser = true))
        } else {
            donorDao.insertDonor(donor.copy(isCurrentUser = true))
        }
    }

    suspend fun getDonorByEmail(email: String): Donor? {
        return donorDao.getDonorByEmail(email)
    }

    suspend fun addBloodRequest(request: BloodRequest) {
        bloodRequestDao.insertRequest(request)
    }

    suspend fun deleteBloodRequest(request: BloodRequest) {
        bloodRequestDao.deleteRequest(request)
    }

    suspend fun deleteDonorById(id: Int) {
        donorDao.deleteDonorById(id)
    }
}
