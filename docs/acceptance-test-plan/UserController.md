# Executed Test Plan for UserController

## Test Case 1: Register a New User

**Endpoint:** localhost:8080/api/v1/users/register  
**HTTP Method:** POST  
**Description:** Creates a new user account (guest or host) with the provided details.  
**Parameters:** None  
**Header:** Content-Type: application/json  
**Request Body:**  
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123",
  "name": "John Doe",
  "phoneNumber": "+573101234567",
  "birthDate": "1995-08-20",
  "role": "GUEST"
}
```  
**Expected Response Code:** 201  
**Expected Response Body:**  
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "name": "John Doe",
  "phoneNumber": "+573101234567",
  "birthDate": "1995-08-20",
  "role": "GUEST",
  "profilePicture": null,
  "description": null,
  "legalDocuments": null
}
```  
**Success Criteria:** The response returns the 201 Created code and the body matches the expected format with a generated ID.  
**Expected Result:** Receive an HTTP 201 response code and the created user profile.  

**Test Execution:**  
Enter the data to register a new user in Postman and send the POST request.  

![signup_test](/docs/assets/signup_test.png)

**Obtained Result:**  
Received the expected HTTP 201 code.  

![signup_response](/docs/assets/signup_response.png)

Validate the created data in the database (user table in MariaDB).  

![signup_db](/docs/assets/signup_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data was validated in the database.  

## Test Case 2: Log In a User

**Endpoint:** localhost:8080/api/v1/users/login  
**HTTP Method:** POST  
**Description:** Authenticates a user and returns a JWT token.  
**Parameters:** None  
**Header:** Content-Type: application/json  
**Request Body:**  
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123"
}
```  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```  
**Success Criteria:** The response returns the 200 OK code and the body contains a valid JWT token.  
**Expected Result:** Receive an HTTP 200 response code and the JWT token.  

**Test Execution:**  
Enter the login credentials in Postman and send the POST request.  

![login_test](/docs/assets/login_test.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![login_response](/docs/assets/login_response.png)

Validate the token by using it in a later authenticated request (e.g., getProfile).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the token was validated.  

## Test Case 3: Update User Profile

**Endpoint:** localhost:8080/api/v1/users/profile  
**HTTP Method:** PUT  
**Description:** Updates the profile information of an authenticated user. Requires authentication.  
**Parameters:** None  
**Header:** Content-Type: application/json, X-User-Id: 1, Authorization: Bearer [eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsInVzZXJJZCI6MSwicm9sZSI6IkdVRVNUIiwiaWF0IjoxNzYwMzE2ODI5LCJleHAiOjE3NjAzMjA0Mjl9.mwoRTSRvIfBAa8mvx97Oa0TKy8QtGZx_UytTfcvRyLoZ_Z1JYNiUmSKBh3xfvv0PSOuvp_MqqRzbBsV_sN_Xag]  
**Request Body:**  
```json
{
  "name": "Juan Perez",
  "phoneNumber": "+573001234567",
  "profilePicture": "https://example.com/photo.jpg",
  "birthDate": "1995-08-20"
}
```  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "name": "Juan Perez",
  "phoneNumber": "+573001234567",
  "birthDate": "1995-08-20",
  "role": "GUEST",
  "profilePicture": "https://example.com/photo.jpg",
  "description": null,
  "legalDocuments": null
}
```  
**Success Criteria:** The response returns the 200 OK code and the body reflects the updated data.  
**Expected Result:** Receive an HTTP 200 response code and the updated user profile.  

**Test Execution:**  
Enter the user ID and updated data in Postman and send the PUT request with a valid JWT token.  

![update_body](/docs/assets/update_body.png)
![update_headers](/docs/assets/update_headers.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![update_response](/docs/assets/update_response.png)


Validate the updated data in the database.  

![update_db](/docs/assets/update_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and changes were validated in the database.  

## Test Case 4: Request Password Reset

**Endpoint:** localhost:8080/api/v1/users/request-password-reset  
**HTTP Method:** POST  
**Description:** Initiates a password reset process by sending a reset token to the user's email. Includes rate limiting.  
**Parameters:** None  
**Header:** Content-Type: application/json  
**Request Body:**  
```json
{
  "email": "john.doe@example.com"
}
```  
**Expected Response Code:** 200  
**Expected Response Body:** None  
**Success Criteria:** The response returns the 200 OK code and no body, with the reset token sent via email (mocked in test).  
**Expected Result:** Receive an HTTP 200 response code and verify the reset process.  

**Test Execution:**  
Enter the email in Postman and send the POST request. Ensure the rate limit is not exceeded.  

![reset_test](/docs/assets/reset_test.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![reset_response](/docs/assets/reset_response.png)

Validate the token generation in the database (password_reset_token table in MariaDB).  

![reset_db](/docs/assets/reset_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the token was generated. Test rate limit by exceeding requests if possible.  

## Test Case 5: Reset Password

**Endpoint:** localhost:8080/api/v1/users/reset-password  
**HTTP Method:** POST  
**Description:** Resets the password using a valid token.  
**Parameters:** None  
**Header:** Content-Type: application/json  
**Request Body:**  
```json
{
  "token": "[valid_token_from_previous_step]",
  "newPassword": "NewSecurePass123"
}
```  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "message": "Password reset successfully"
}
```  
**Success Criteria:** The response returns the 200 OK code and the body contains the success message.  
**Expected Result:** Receive an HTTP 200 response code and confirmation of password reset.  

**Test Execution:**  
Enter the token and new password in Postman and send the POST request.  

![reset_body](/docs/assets/reset_body.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![reset_response2](/docs/assets/reset_response2.png)

Validate the password update in the database (ensure the hashed password is updated).  

![reset_db2](/docs/assets/reset_db2.png) 

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the password was updated. Test with invalid/expired tokens for edge cases.  

## Test Case 6: Get User Profile

**Endpoint:** localhost:8080/api/v1/users/profile  
**HTTP Method:** GET  
**Description:** Retrieves the profile information of the authenticated user. Requires authentication.  
**Parameters:** None  
**Header:** X-User-Id: 1, Authorization: Bearer [JWT token]  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "name": "Juan Perez",
  "phoneNumber": "+573001234567",
  "birthDate": "1995-08-20",
  "role": "GUEST",
  "profilePicture": "https://example.com/photo.jpg",
  "description": null,
  "legalDocuments": null
}
```  
**Success Criteria:** The response returns the 200 OK code and the body matches the expected format.  
**Expected Result:** Receive an HTTP 200 response code and the user profile.  

**Test Execution:**  
Enter the user ID in Postman and send the GET request with a valid JWT token.  

![profile_body](/docs/assets/profile_body.png) 
![profile_headers](/docs/assets/profile_headers.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![profile_response](/docs/assets/profile_response.png)

Validate the data in the database (ensure it matches the record).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data matches the database.  