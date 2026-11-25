# Capstone-Project-android
PNC capstone project android

Android Application for managing orders, view available products, and see your saved products for the AdventureWorks system

## Setup

### 1. Clone the repository:
```bash
git clone https://github.com/mannyrajan/Capstone-Project-android/
```

### 2. Open the Project

- Launch Android Studio.
- Select Open and navigate to the cloned repository.
- Allow Gradle to sync and install the required dependencies.

### 3. Configure the Base URL & Demo Customer

The application uses the provided bootcamp API.

Default Base URL:

```
https://api.bootcampcentral.com
```

The base URL is configured in `NetworkModule.kt` through Retrofit. Order and account screens use a predefined demo customer configuration, and no secrets, API keys, or local machine paths are stored in the repository.

### 4. Build and Run

- Open Device Manager in Android Studio.
- Create or select an Android emulator.
- Select the app run configuration.
- Click Run to launch the application.

## Known Limitations
- The application uses a predefined demo customer.
- Product & Order data require an internet connection.
- - Final product backlog and Scrum artifacts are maintained alongside the project deliverables rather than bundled inside app source code.


## Automated Tests

Unit tests are located under `app/src/test/java/com/pnc/capstoneprojectandroid/`:
- `OrdersTest.kt`
- `ProductCatalogTest.kt`
- `ProductsViewModelTest.kt`
- `RecentHistoryTest.kt`
- `SavedProductTest.kt`

Run the automated tests from the command line:
```bash
./gradlew test
