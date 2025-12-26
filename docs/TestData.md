# Test Data Documentation

## 1. Test Accounts
The following user accounts have been created for testing purposes.
**Note**: All test accounts use the password `1234`. Please use this password regardless of the username.

| Username | Password | Name | Email | Department | Role |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **admin** | `1234` | System Admin | admin@kz.com | IT | ADMIN |
| **hr_manager** | `1234` | Kim HR | hr@kz.com | HR | USER |
| **fin_manager** | `1234` | Lee Finance | finance@kz.com | Finance | USER |
| **plan_manager** | `1234` | Park Planning | plan@kz.com | Planning | USER |
| **user1** | `1234` | Hong Gildong | hong@kz.com | Sales | USER |
| **user2** | `1234` | Kim Chulsoo | kim@kz.com | Marketing | USER |

## 2. Mock Data Summary
The database (`kz_magazine`) has been populated with the following sample data (Migration V12):

### Contents (Magazine)
*   **HR Policy Update 2025** (Published, HR)
*   **Q4 Financial Report** (Published, Finance)
*   **Upcoming Workshop** (Draft, Planning)
*   **Employee Spotlight: Sarah** (Published, HR)

### Events
*   **New Year Town Hall** (Active)
*   **Health Checkup 2025** (Active)
*   **Christmas Party 2024** (Closed)

### Social Feeds
*   **YouTube**: KZ Sustainability Vision, Global Operations Tour
*   **Instagram**: Team Building Day, Best Safety Award

### Ideas (Suggestions)
*   **Cafeteria Menu improvement** (Pending)
*   **Shuttle Bus Schedule** (Accepted)

## 3. How to Reset
To reset the data to its initial state or re-apply this test data:
1.  Stop the backend server.
2.  Run `./gradlew clean bootRun` (This will re-run Flyway migrations).
