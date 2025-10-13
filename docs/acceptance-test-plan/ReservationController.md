# Executed Test Plan for ReservationController

## Test Case 1: Create New Reservation

**Endpoint:** localhost:8080/api/v1/reservations  
**HTTP Method:** POST  
**Description:** Allows an authenticated guest to create a new reservation for an accommodation. Requires authentication.  
**Parameters:** None  
**Header:** Content-Type: application/json, X-User-Email: [guest email, e.g., guest@example.com], Authorization: Bearer [JWT token]  
**Request Body:**  
```json
{
  "accommodationId": 1,
  "checkInDate": "2025-10-15",
  "checkOutDate": "2025-10-20",
  "numberOfGuests": 2,
  "totalPrice": 750.00
}
```  
**Expected Response Code:** 201  
**Expected Response Body:**  
```json
{
  "id": 1,
  "accommodationId": 1,
  "userId": 1,
  "checkInDate": "2025-10-15",
  "checkOutDate": "2025-10-20",
  "numberOfGuests": 2,
  "totalPrice": 750.00,
  "status": "PENDING",
  "createdAt": "2025-10-12T19:29:00Z",
  "isDeleted": false
}
```  
**Success Criteria:** The response returns the 201 Created code and the body matches the expected format with a generated ID.  
**Expected Result:** Receive a HTTP 201 response code and the created reservation.  

**Test Execution:**  
Enter the data to create a new reservation in Postman and send the POST request with a valid JWT token.  

*(Paste the Postman screenshot here showing the body, headers, and request submission.)*  

**Obtained Result:**  
Received the expected HTTP 201 code.  

Validate the created data in the database (reservation table in MariaDB).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data was validated in the database.  

## Test Case 2: Get Reservation by ID

**Endpoint:** localhost:8080/api/v1/reservations/{id} (e.g., /1)  
**HTTP Method:** GET  
**Description:** Retrieves the details of a specific reservation by its ID. Requires authentication.  
**Parameters:** id (in path, e.g., 1)  
**Header:** X-User-Email: [guest email, e.g., guest@example.com], Authorization: Bearer [JWT token]  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 1,
  "accommodationId": 1,
  "userId": 1,
  "checkInDate": "2025-10-15",
  "checkOutDate": "2025-10-20",
  "numberOfGuests": 2,
  "totalPrice": 750.00,
  "status": "PENDING",
  "createdAt": "2025-10-12T19:29:00Z",
  "isDeleted": false
}
```  
**Success Criteria:** The response returns the 200 OK code and the body matches the expected format.  
**Expected Result:** Receive a HTTP 200 response code and the reservation details.  

**Test Execution:**  
Enter the reservation ID in Postman and send the GET request with a valid JWT token.  

**Obtained Result:**  
Received the expected HTTP 200 code.  

Validate the data in the database (ensure it matches the record).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data matches the database.  

## Test Case 3: Update Reservation

**Endpoint:** localhost:8080/api/v1/reservations/{id} (e.g., /1)  
**HTTP Method:** PUT  
**Description:** Allows updating the status of an existing reservation. Requires authentication.  
**Parameters:** id (in path, e.g., 1)  
**Header:** Content-Type: application/json, X-User-Email: [guest email, e.g., guest@example.com], Authorization: Bearer [JWT token]  
**Request Body:**  
```json
{
  "status": "CONFIRMED"
}
```  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 1,
  "accommodationId": 1,
  "userId": 1,
  "checkInDate": "2025-10-15",
  "checkOutDate": "2025-10-20",
  "numberOfGuests": 2,
  "totalPrice": 750.00,
  "status": "CONFIRMED",
  "createdAt": "2025-10-12T19:29:00Z",
  "isDeleted": false
}
```  
**Success Criteria:** The response returns the 200 OK code and the body reflects the updated status.  
**Expected Result:** Receive a HTTP 200 response code and the updated reservation.  

**Test Execution:**  
Enter the ID and updated data in Postman and send the PUT request with a valid JWT token.  

**Obtained Result:**  
Received the expected HTTP 200 code.  

Validate the updated data in the database (ensure status is updated).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and changes were validated in the database.  

## Test Case 4: Cancel Reservation (Soft Delete)

**Endpoint:** localhost:8080/api/v1/reservations/{id} (e.g., /1)  
**HTTP Method:** DELETE  
**Description:** Soft deletes a reservation by setting isDeleted to true and status to CANCELLED. Requires authentication.  
**Parameters:** id (in path, e.g., 1)  
**Header:** X-User-Email: [guest email, e.g., guest@example.com], Authorization: Bearer [JWT token]  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "message": "Reservation cancelled successfully"
}
```  
**Success Criteria:** The response returns the 200 OK code and the body contains the success message.  
**Expected Result:** Receive a HTTP 200 response code and confirmation of cancellation.  

**Test Execution:**  
Enter the ID in Postman and send the DELETE request with a valid JWT token.  

**Obtained Result:**  
Received the expected HTTP 200 code.  

Validate the data in the database (ensure isDeleted = true and status = CANCELLED).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the cancellation was validated in the database.  

## Test Case 5: List User's Reservations

**Endpoint:** localhost:8080/api/v1/reservations?page=0&size=10  
**HTTP Method:** GET  
**Description:** Retrieves a paginated list of reservations for the authenticated guest, optionally filtered by status. Requires authentication.  
**Parameters:** page (default 0), size (default 10), status (optional, e.g., PENDING)  
**Header:** X-User-Email: [guest email, e.g., guest@example.com], Authorization: Bearer [JWT token]  
**Request Body:** None  
**Expected Response Code:** 200 (or 204 if no reservations)  
**Expected Response Body:**  
```json
{
  "content": [
    {
      "id": 1,
      "accommodationId": 1,
      "userId": 1,
      "checkInDate": "2025-10-15",
      "checkOutDate": "2025-10-20",
      "numberOfGuests": 2,
      "totalPrice": 750.00,
      "status": "PENDING",
      "createdAt": "2025-10-12T19:29:00Z",
      "isDeleted": false
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1
}
```  
**Success Criteria:** The response returns the 200 OK code and a paginated list of reservations (or 204 if empty).  
**Expected Result:** Receive a HTTP 200 response code and the list of reservations.  

**Test Execution:**  
Enter the pagination parameters and optional status in Postman and send the GET request with a valid JWT token.  

**Obtained Result:**  
Received the expected HTTP 200 code.  

Validate the data in the database (ensure it matches the user’s active reservations).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data matches the user’s reservations.  

## Test Case 6: List Accommodation Reservations

**Endpoint:** localhost:8080/api/v1/reservations/accommodations/{accommodationId}?page=0&size=10 (e.g., /1)  
**HTTP Method:** GET  
**Description:** Retrieves a paginated list of reservations for a specific accommodation (only for the host). Requires authentication.  
**Parameters:** accommodationId (in path, e.g., 1), page (default 0), size (default 10), status (optional, e.g., PENDING)  
**Header:** X-User-Email: [host email, e.g., host@example.com], Authorization: Bearer [JWT token]  
**Request Body:** None  
**Expected Response Code:** 200 (or 204 if no reservations)  
**Expected Response Body:**  
```json
{
  "content": [
    {
      "id": 1,
      "accommodationId": 1,
      "userId": 1,
      "checkInDate": "2025-10-15",
      "checkOutDate": "2025-10-20",
      "numberOfGuests": 2,
      "totalPrice": 750.00,
      "status": "PENDING",
      "createdAt": "2025-10-12T19:29:00Z",
      "isDeleted": false
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1
}
```  
**Success Criteria:** The response returns the 200 OK code and a paginated list of reservations (or 204 if empty).  
**Expected Result:** Receive a HTTP 200 response code and the list of reservations for the accommodation.  

**Test Execution:**  
Enter the accommodation ID, pagination parameters, and optional status in Postman and send the GET request with a valid JWT token for the host.  

**Obtained Result:**  
Received the expected HTTP 200 code.  

Validate the data in the database (ensure it matches the accommodation’s active reservations).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data matches the accommodation’s reservations.  