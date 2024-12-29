# WellKom - A Visitor Management System #
Efficiently digitalize visitor entry processes with WellKom, an Android application developed for Organizations to enhance security and streamline visitor data management.

## Table of Contents ##
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Screenshots](#screenshots)
- [Installation](#installation)
- [Usage](#usage)
- [Testing](#testing)
- [Future Enhancements](#future-enhancements)
- [Integrating a Local Database (Optional)](#integrating-a-local-databaseoptional)


## Overview ##
WellKom is an Android application designed to replace the traditional manual visitor registration system with a digital, eco-friendly alternative. It enables visitors to scan a QR code at the gate, fill in their details on the app, and seamlessly gain entry with a unique visitor ID. This app reduces paper usage, enhances security, and provides centralized management for visitor data.

## Features ##
- QR Code Integration: Enables quick and secure access to visitor registration forms.
- Digital Visitor Registration: Collects data like name, ID proof, time of visit, and reason for entry.
- Real-Time Approvals: Automatically generates visitor IDs upon successful registration.
- Secure Data Storage: Uses Firebase Realtime Database and Storage for backend management.
- Paperless and Eco-Friendly: Replaces traditional logbooks to save resources.
- Role-Based Access: Restricted access to ensure only authorized individuals can use the app.

## Tech Stack ##
- Programming Language: Java
- UI Design: XML
- Platform: Android Studio
- Database: Firebase Realtime Database
- Storage: Firebase Storage
- Dynamic Links: Firebase Dynamic Links

## Screenshots ##
### App Logo: <br>
![image](https://github.com/user-attachments/assets/f94429d0-effa-4365-9602-ccd3d4164e1d)

### Key Screens
#### Splash Screen:
![image](https://github.com/user-attachments/assets/22a18ff1-825c-40b6-9493-75a451404b03)
#### QR Code Scanner Screen:
![image](https://github.com/user-attachments/assets/85730c7d-a38a-4860-8c59-35305888e6a8)
#### Registration Form:
#### Approval Screen:
![image](https://github.com/user-attachments/assets/789acd8b-ab97-4340-b95b-6e6f0cc767cd)


## Installation ##

- Clone the repository:
```
git clone https://github.com/yourusername/wellkom-visitor-management.git
```
- Open the project in Android Studio.
- Sync Gradle and resolve dependencies.
- Build and run the app on an emulator or physical device.

## Usage ##

- Install the app using the .apk file or build it from the source.
- Scan the QR code placed at the campus gate.
- Fill in mandatory fields, including name, government ID, reason for visit, and time.
- Submit the form to receive a unique visitor ID and entry approval.
- Use the visitor ID for authorization at further checkpoints.

## Testing ##
- The application was thoroughly tested by:
- Installing the .apk file on Android devices.
- Scanning the QR code to validate redirection to the form.
- Ensuring data submission and visitor ID generation worked as intended.
- Simulating incomplete form submission to verify error handling.

## Future Enhancements ##
- Allow visitor ID usage for authentication at multiple campus locations.
- Enable direct app download via invitation mail for enhanced accessibility.
- Add multi-language support to cater to a broader audience.
- Incorporate real-time analytics for visitor tracking.

## Integrating a Local Database (Optional) ## 
To enable offline functionality or reduce dependency on Firebase, you can integrate a local database like SQLite or Room in your project. Follow these steps:

1. Add Dependencies

	For SQLite: No additional dependency is needed as it’s built into Android.For Room: Add the Room dependencies to your build.gradle file:

```
dependencies {
    implementation "androidx.room:room-runtime:2.5.0"
    annotationProcessor "androidx.room:room-compiler:2.5.0" // or kapt for Kotlin
}
```

2. Create a Database Schema

	Define an entity class for your visitor data:

```java
@Entity(tableName = "visitor_table")
public class Visitor {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String governmentId;
    private String reason;
    private String visitTime;

    // Add getters and setters
}
```


3. Define a DAO (Data Access Object)

	Create an interface for database operations:

```java
@Dao
public interface VisitorDao {
    @Insert
    void insert(Visitor visitor);

    @Query("SELECT * FROM visitor_table")
    List<Visitor> getAllVisitors();

    @Delete
    void delete(Visitor visitor);
}
```

4. Create the Database Class

	Set up the database using Room:

```java
@Database(entities = {Visitor.class}, version = 1)
public abstract class VisitorDatabase extends RoomDatabase {
    private static VisitorDatabase instance;

    public abstract VisitorDao visitorDao();

    public static synchronized VisitorDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    VisitorDatabase.class, "visitor_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
```

5. Update the Code for Offline Data Handling
	- Modify your app logic to store data locally when there’s no internet connection:
	- Insert Visitor Data: Use the insert method from the VisitorDao to save data offline.
	- Retrieve Visitor Data: Use the getAllVisitors method to display locally stored data.

6. Sync with Firebase
	- To ensure data consistency between the local database and Firebase:
	- Use a background service (e.g., WorkManager) to periodically upload local data to Firebase when the internet is available.
	- Remove data from the local database after a successful sync.


## License ##

This project is licensed under the MIT License. See the **LICENSE** file for details.
