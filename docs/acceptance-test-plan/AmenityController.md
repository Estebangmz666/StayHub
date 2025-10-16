# Executed Test Plan for AmenityController

## Test Case 1: List All Active Amenities

**Endpoint:** localhost:8080/api/v1/amenities  
**HTTP Method:** GET  
**Description:** Retrieves a list of all active amenities in the system.  
**Parameters:** None  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 200 (or 204 if no active amenities)  
**Expected Response Body:**  
```json
[
  {
    "id": 1,
    "name": "Wi-Fi",
    "description": "High-speed internet access",
    "isActive": true
  },
  {
    "id": 2,
    "name": "Parking",
    "description": "On-site parking available",
    "isActive": true
  }
]
```  
**Success Criteria:** The response returns the 200 OK code and a list of active amenities (or 204 if empty).  
**Expected Result:** Receive a HTTP 200 response code and the list of active amenities.  

**Test Execution:**  
Enter the endpoint URL in Postman and send the GET request.

![list_amenities_request](/docs/assets/list_amenities_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.

![list_amenities_response](/docs/assets/list_amenities_response.png)

Validate the data in the database (amenity table in MariaDB, ensure only isActive = true records).  


**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and only active amenities were listed.  

## Test Case 2: Create New Amenity

**Endpoint:** localhost:8080/api/v1/amenities  
**HTTP Method:** POST  
**Description:** Creates a new amenity in the system. The ID will be generated automatically.  
**Parameters:** None  
**Header:** Content-Type: application/json  
**Request Body:**  
```json
{
  "name": "Swimming Pool",
  "description": "Outdoor swimming pool"
}
```  
**Expected Response Code:** 201  
**Expected Response Body:**  
```json
{
  "id": 3,
  "name": "Swimming Pool",
  "description": "Outdoor swimming pool",
  "isActive": true
}
```  
**Success Criteria:** The response returns the 201 Created code and the body matches the expected format with a generated ID.  
**Expected Result:** Receive a HTTP 201 response code and the created amenity.  

**Test Execution:**  
Enter the data to create a new amenity in Postman and send the POST request.
![create_amenity_body](/docs/assets/create_amenity_body.png)

**Obtained Result:**  
Received the expected HTTP 201 code.

![create_amenity_response](/docs/assets/create_amenity_response.png)

Validate the created data in the database (amenity table in MariaDB).  

![create_amenity_db](/docs/assets/create_amenity_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data was validated in the database.  

## Test Case 3: Update Existing Amenity

**Endpoint:** localhost:8080/api/v1/amenities/{id} (e.g., /3)  
**HTTP Method:** PUT  
**Description:** Updates an existing amenity by its ID.  
**Parameters:** id (in path, e.g., 3)  
**Header:** Content-Type: application/json  
**Request Body:**  
```json
{
  "name": "Swimming Pool",
  "description": "Heated outdoor swimming pool"
}
```  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 3,
  "name": "Swimming Pool",
  "description": "Heated outdoor swimming pool",
  "isActive": true
}
```  
**Success Criteria:** The response returns the 200 OK code and the body reflects the updated data.  
**Expected Result:** Receive a HTTP 200 response code and the updated amenity.  

**Test Execution:**  
Enter the ID and updated data in Postman and send the PUT request.

![update_amenity_body](/docs/assets/update_amenity_body.png)

**Obtained Result:**  
Received the expected HTTP 200 code.

![update_amenity_response](/docs/assets/update_amenity_response.png)

Validate the updated data in the database.

![update_amenity_db](/docs/assets/update_amenity_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and changes were validated in the database.  

## Test Case 4: Deactivate Amenity (Soft Delete)

**Endpoint:** localhost:8080/api/v1/amenities/{id} (e.g., /3)  
**HTTP Method:** DELETE  
**Description:** Deactivates an existing amenity by setting isActive to false (soft delete).  
**Parameters:** id (in path, e.g., 3)  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 204  
**Expected Response Body:** None  
**Success Criteria:** The response returns the 204 No Content code and the amenity is marked as inactive in the database.  
**Expected Result:** Receive a HTTP 204 response code and confirmation of deactivation.  

**Test Execution:**  
Enter the ID in Postman and send the DELETE request.

![deactivate_amenity_request](/docs/assets/deactivate_amenity_request.png)

**Obtained Result:**  
Received the expected HTTP 204 code.

![deactivate_amenity_response](/docs/assets/deactivate_amenity_response.png)

Validate the data in the database (ensure active = 0 for the specified ID).

![deactivate_amenity_db](/docs/assets/deactivate_amenity_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the deactivation was validated in the database.  

## Test Case 5: Get Amenity Details by ID

**Endpoint:** localhost:8080/api/v1/amenities/{id} (e.g., /1)  
**HTTP Method:** GET  
**Description:** Retrieves the details of a specific amenity by its ID.  
**Parameters:** id (in path, e.g., 1)  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 1,
  "name": "Wi-Fi",
  "description": "High-speed internet access",
  "isActive": true
}
```  
**Success Criteria:** The response returns the 200 OK code and the body matches the expected format.  
**Expected Result:** Receive a HTTP 200 response code and the amenity details.  

**Test Execution:**  
Enter the amenity ID in Postman and send the GET request.

![get_amenity_by_id_request](/docs/assets/get_amenity_by_id_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.

![get_amenity_by_id_response](/docs/assets/get_amenity_by_id_response.png)

Validate the data in the database (optional, as it’s a read operation).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data matches the database.  