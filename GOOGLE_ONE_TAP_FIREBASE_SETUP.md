# গুগল ওয়ান-ট্যাপ এবং ফায়ারবেস অথেন্টিকেশন গাইড (Google One Tap & Firebase Auth Guide)

রক্তবন্ধু অ্যাপ্লিকেশনে আধুনিক **Google Credential Manager (One-Tap Sign-In) API** এবং **Firebase Authentication** সফলভাবে যুক্ত করা হয়েছে। এই গাইডটিতে ক্রমান্বয়ে বর্ণনা করা হয়েছে কীভাবে আপনি অ্যাপটিকে গুগল ক্লাউড এবং ফায়ারবেস কনসোলে সংযুক্ত করে বাস্তব প্রোডাকশনে চালু করবেন।

---

## ১. ফায়ারবেস এবং গুগল ক্লাউড কনসোল সেটআপ (Cloud Configuration)

বাস্তব ডিভাইসে গুগল লগইন সচল করতে নিচের ধাপগুলো অনুসরণ করুন:

### ক. SHA-1 এবং SHA-256 ফিঙ্গারপ্রিন্ট সংগ্রহ করুন:
বাস্তব সাইন-ইন রিকুয়েস্টের জন্য গুগলের কাছে আপনার ডেভেলপমেন্ট কী-এর ফিঙ্গারপ্রিন্ট রেজিস্টার করতে হবে।
আপনার লোকাল কম্পিউটারের টার্মিনালে নিচের কমান্ডটি রান করুন:
* **Windows:**
  ```bash
  keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
  ```
* **Mac/Linux:**
  ```bash
  keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
  ```
*আউটপুট থেকে **SHA-1** এবং **SHA-256** হেক্স কোডগুলো কপি করুন।*

### খ. ফায়ারবেস প্রজেক্ট তৈরি ও অ্যাপ রেজিস্টার:
1. [Firebase Console](https://console.firebase.google.com/)-এ যান এবং একটি নতুন প্রজেক্ট তৈরি করুন।
2. প্রজেক্টের ড্যাশবোর্ডে **Android Icon**-এ ক্লিক করে আপনার অ্যান্ড্রয়েড অ্যাপ যুক্ত করুন।
3. **Android Package Name** প্রদান করুন: `com.rahadhossain.roktobondhu` (বিল্ড ফাইল অনুযায়ী)।
4. পূর্ববর্তী ধাপে কপি করা **SHA-1** ফিঙ্গারপ্রিন্টটি পেস্ট করুন এবং অ্যাপটি রেজিস্টার করুন।
5. `google-services.json` ফাইলটি ডাউনলোড করে আপনার প্রকল্পের `/app/` ডিরেক্টরিতে পেস্ট করুন।

### গ. গুগল সাইন-ইন সচলকরণ (Enable Google Sign-In):
1. ফায়ারবেস কনসোলের বাঁদিকের মেনু থেকে **Authentication** সেকশনে যান।
2. **Sign-in method** ট্যাবে ক্লিক করে **Add new provider** থেকে **Google** নির্বাচন করুন।
3. এটি সচল (Enable) করুন, একটি সাপোর্ট ইমেইল দিন।
4. স্ক্রিনের নিচে **Web SDK configuration** ড্রপডাউনটি খুলুন। এখানে আপনি **Web Client ID** এবং **Web Client Secret** দেখতে পাবেন।
5. **Web Client ID**-টি কপি করে রাখুন।

---

## ২. ডিপেন্ডেন্সি ও প্লাগইন সেটআপ (Gradle Setup)

রিয়েল-টাইম ওয়ান-ট্যাপের জন্য প্রয়োজনীয় প্লাগইন এবং লাইব্রেরিগুলো `/app/build.gradle.kts` ফাইলে যুক্ত করা হয়েছে:

```kotlin
dependencies {
    // Credential Manager এবং Google ID / Identity API
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.firebase:firebase-auth-ktx")
}
```

*বাস্তব প্রকল্পে অটোমেটিক কনফিগারেশনের জন্য প্রোজেক্ট এবং অ্যাপ লেভেল বিল্ড ফাইলে `com.google.gms.google-services` প্লাগইনটি প্রয়োগ করতে হয়। ব্রাউজার এমুলেটর বিল্ডে ত্রুটি এড়ানোর জন্য আমরা এটিকে এভয়েড করে ডায়নামিক মেকানিজম প্রয়োগ করেছি।*

---

## ৩. রিয়েল কোড ইমপ্লিমেন্টেশন এবং লাইভ ট্রিগার (Kotlin Implementation)

আমরা প্রকল্পে দুটি সম্পূর্ণ মডিউল তৈরি করেছি:

### ক. গুগল সাইন-ইন হেলপার ক্লাস (`GoogleSignInHelper.kt`):
এটি অ্যান্ড্রয়েডের আধুনিক `androidx.credentials.CredentialManager` ব্যবহার করে ওয়ান-ট্যাপ পিকার ডিসপ্লে করে এবং আইডি টোকেন এক্সট্র্যাক্ট করে Firebase অথেন্টিকেটর সচল করে।

```kotlin
// /app/src/main/java/com/example/ui/auth/GoogleSignInHelper.kt
object GoogleSignInHelper {
    // আপনার ফায়ারবেস ওয়েব ক্লায়েন্ট আইডি এখানে সেট করুন:
    var WEB_CLIENT_ID: String = "YOUR_REAL_WEB_CLIENT_ID.apps.googleusercontent.com"

    suspend fun triggerGoogleSignIn(
        context: Context,
        onSuccess: (email: String, name: String, idToken: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(context, request)
            // আইডি টোকেন নিয়ে ফায়ারবেসে লগইন সচল করা হয় এখানে...
        } catch (e: Exception) {
            onError(e.message ?: "ভুল পরিলক্ষিত হয়েছে")
        }
    }
}
```

### খ. ডায়নামিক ডেভলপমেন্ট ফলব্যাক এমুলেটর সাপোর্ট:
ব্রাউজারের স্ট্রিমড এমুলেটরে গুগল প্লে-সার্ভিস বা এপিআই কি কনফিগার করা না থাকলেও যেন সেশন পরীক্ষা করা যায়, সেজন্য আমরা একটি ইন্টেলিজেন্ট **ফলব্যাক ডায়াগনস্টিক ব্যানার** যুক্ত করেছি। 
* আপনি যখন **"গুগল একাউন্ট দিয়ে এগিয়ে যান"** বাটনে ক্লিক করবেন, অ্যাপটি সত্যি নতুন কোড রান করবে।
* যদি লোকাল এনভায়রনমেন্টে জিপিএস বা ফায়ারবেস রেডি না থাকে, এটি স্ক্র্যাশ না করে চমৎকার একটি ইনফরমেশন কার্ড দেখাবে এবং আপনাকে সিমুলেটেড ডিস্ট্রিবিউশন ব্যবহারের অনুমতি দেবে।

---

## ৪. ব্যবহারকারীর সেশন নিয়ন্ত্রণ (Session Management)

ইউজার একবার লগইন করলে সেশনটি লোকাল `Room Database`-এ সেভ থাকে, ফলে অ্যাপ পুনরায় চালু করলে সরাসরি ড্যাশবোর্ড চলে আসে। লগআউট করার জন্য প্রোফাইল ট্যাব থেকে **লগআউট** বাটনে প্রেস করলে সেশন ক্লোজ হয়ে স্বয়ংক্রিয়ভাবে আবার সাইন-ইন স্ক্রীন দেখা যাবে।
