package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "donors")
data class Donor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val bloodGroup: String, // A+, A-, B+, B-, AB+, AB-, O+, O-
    val division: String,   // Barishal, Chattogram, Dhaka, Khulna, Rajshahi, Rangpur, Mymensingh, Sylhet or Jashore Upazila
    val area: String,       // Specific street/sub-category (e.g., Mirpur, Dhanmondi, Zindabazar)
    val phone: String,
    val email: String = "", // Links to simulated Google account
    val avatarId: Int = 1,  // ID representing avatar illustrations
    val isAvailable: Boolean = true,
    val lastDonationDate: String = "কখনো নয়", // Last donation info
    val isCurrentUser: Boolean = false,
    val customAvatarUrl: String = "" // Custom uploaded profile image url support
) : Serializable

@Entity(tableName = "blood_requests")
data class BloodRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientName: String,
    val bloodGroup: String,
    val hospitalName: String,
    val division: String,
    val area: String,
    val phone: String,
    val neededDate: String,
    val neededTime: String,
    val details: String = "",
    val requestedBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
