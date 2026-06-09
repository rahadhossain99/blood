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
                    name = "আরিফ বিল্লাহ",
                    bloodGroup = "O+",
                    division = "Sadar",
                    area = "মণিহার মোড়, যশোর সদর",
                    phone = "01712345678",
                    email = "",
                    avatarId = 1,
                    isAvailable = true,
                    lastDonationDate = "২ মাস আগে"
                ),
                Donor(
                    name = "নাহিদ হাসান",
                    bloodGroup = "A+",
                    division = "Jhikargachha",
                    area = "ঝিকরগাছা বাজার রোড",
                    phone = "01823456789",
                    email = "",
                    avatarId = 2,
                    isAvailable = true,
                    lastDonationDate = "৩ মাস আগে"
                ),
                Donor(
                    name = "রুপক লাল সাহা",
                    bloodGroup = "B+",
                    division = "Abhaynagar",
                    area = "নওয়াপাড়া ঘাট সড়ক",
                    phone = "01934567890",
                    email = "",
                    avatarId = 3,
                    isAvailable = true,
                    lastDonationDate = "১ মাস আগে"
                ),
                Donor(
                    name = "তাসনিমা কবির আঁখি",
                    bloodGroup = "AB+",
                    division = "Manirampur",
                    area = "কোপাল্ট রোড, মণিরামপুর",
                    phone = "01545678901",
                    email = "",
                    avatarId = 4,
                    isAvailable = false,
                    lastDonationDate = "২ সপ্তাহ আগে"
                ),
                Donor(
                    name = "ফারহানা আক্তার রিয়া",
                    bloodGroup = "O-",
                    division = "Keshabpur",
                    area = "ভুপালী গ্রাম, সাগরদাঁড়ি",
                    phone = "01656789012",
                    email = "",
                    avatarId = 5,
                    isAvailable = true,
                    lastDonationDate = "কখনো নয়"
                ),
                Donor(
                    name = "সালমান আদিব",
                    bloodGroup = "B-",
                    division = "Sharsha",
                    area = "বেনাপোল কাস্টমস চেকপোস্ট",
                    phone = "01367890123",
                    email = "",
                    avatarId = 6,
                    isAvailable = true,
                    lastDonationDate = "৪ মাস আগে"
                ),
                Donor(
                    name = "তাহসিনা সুলতানা",
                    bloodGroup = "A-",
                    division = "Chougachha",
                    area = "উপজেলা মোড়, চৌগাছা",
                    phone = "01732145698",
                    email = "",
                    avatarId = 7,
                    isAvailable = true,
                    lastDonationDate = "৫ মাস আগে"
                ),
                Donor(
                    name = "ইমরান হোসেন",
                    bloodGroup = "AB-",
                    division = "Bagherpara",
                    area = "চাড়া ভিটি, বাঘারপাড়া",
                    phone = "01845123987",
                    email = "",
                    avatarId = 8,
                    isAvailable = false,
                    lastDonationDate = "১ মাস আগে"
                ),
                Donor(
                    name = "সাদমান রহমান",
                    bloodGroup = "O+",
                    division = "Sadar",
                    area = "পালবাড়ি মোড়, যশোর সদর",
                    phone = "01967234812",
                    email = "",
                    avatarId = 9,
                    isAvailable = true,
                    lastDonationDate = "২ মাস আগে"
                ),
                Donor(
                    name = "সানজিদা ইয়াসমিন মিম",
                    bloodGroup = "A+",
                    division = "Sharsha",
                    area = "নাভারন ওয়ান স্টপ পয়েন্ট",
                    phone = "01552341987",
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
