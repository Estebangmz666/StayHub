# Executed Test Plan for AccommodationController

## Test Case 1: Create New Accommodation

**Endpoint:** localhost:8080/api/v1/accommodations  
**HTTP Method:** POST  
**Description:** Allows a host to create a new accommodation listing in the system.  
The ID will be generated automatically. Requires authentication and that the user is a host.  
**Parameters:** None  
**Header:** Content-Type: application/json, X-Username: [host user email, e.g., host@example.com] (simulates JWT authentication)  
**Request Body:**  
```json
{
  "title": "Modern Apartment",
  "description": "Spacious apartment in the city center",
  "capacity": 4,
  "mainImage": "https://example.com/images/main.jpg",
  "longitude": -75.5668,
  "latitude": 6.2442,
  "locationDescription": "Near El Poblado Park",
  "city": "Medellin",
  "pricePerNight": 150.00,
  "images": ["https://example.com/images/1.jpg", "https://example.com/images/2.jpg"]
}
```  
**Expected Response Code:** 201  
**Expected Response Body:**  
```json
{
  "id": 1,
  "title": "Modern Apartment",
  "description": "Spacious apartment in the city center",
  "capacity": 4,
  "mainImage": "https://example.com/images/main.jpg",
  "longitude": -75.5668,
  "latitude": 6.2442,
  "locationDescription": "Near El Poblado Park",
  "city": "Medellin",
  "pricePerNight": 150.00,
  "images": ["https://example.com/images/1.jpg", "https://example.com/images/2.jpg"]
}
```  
**Success Criteria:** The response returns the 201 Created code and the body matches the expected format with a generated ID.  
**Expected Result:** Receive a HTTP 201 response code and the created accommodation.  

**Test Execution:**  
Enter the data to create a new accommodation in Postman.  

![create_accommodation_body](/assets/create_accommodation_body.png)
![create_accommodation_headers](/assets/create_accommodation_headers.png)

**Obtained Result:**  
Received the expected HTTP 201 code.  

![create_accommodation_response](/assets/create_accommodation_response.png)

Validate the created data in the database (accommodation table in MariaDB).  

![create_accommodation_db](/assets/create_accommodation_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data was validated in the database.  

## Test Case 2: Get Accommodation by ID

**Endpoint:** localhost:8080/api/v1/accommodations/{id} (e.g., /1)  
**HTTP Method:** GET  
**Description:** Retrieves the details of a specific accommodation by its ID.  
**Parameters:** id (in path, e.g., 1)  
**Header:** None required
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 1,
  "title": "Modern Apartment",
  "description": "Spacious apartment in the city center",
  "capacity": 4,
  "mainImage": "https://example.com/images/main.jpg",
  "longitude": -75.5668,
  "latitude": 6.2442,
  "locationDescription": "Near El Poblado Park",
  "city": "Medellin",
  "pricePerNight": 150.00,
  "images": ["https://example.com/images/1.jpg", "https://example.com/images/2.jpg"]
}
```  
**Success Criteria:** The response returns the 200 OK code and the body matches the expected format.  
**Expected Result:** Receive a HTTP 200 response code and the accommodation details.  

**Test Execution:**  
Enter the accommodation ID in Postman and send the GET request.  

![get_accommodation_by_id_request](/assets/get_accommodation_by_id_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![get_accommodation_by_id_response](/assets/get_accommodation_by_id_response.png) 

Validate the data in the database (optional, as it’s a read operation).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data matches the database.  

## Test Case 3: Update Accommodation

**Endpoint:** localhost:8080/api/v1/accommodations/{id} (e.g., /1)  
**HTTP Method:** PUT  
**Description:** Allows a host to update an existing accommodation. Requires the user to be the owner.  
**Parameters:** id (in path, e.g., 1)  
**Header:** Content-Type: application/json, X-Username: [host user email]  
**Request Body:**  
```json
{
  "title": "Updated Apartment",
  "description": "Renovated apartment",
  "capacity": 5,
  "mainImage": "https://example.com/images/main_v2.jpg",
  "longitude": -75.5668,
  "latitude": 6.2442,
  "locationDescription": "Near El Poblado Park",
  "city": "Medellin",
  "pricePerNight": 175.00,
  "images": ["https://example.com/images/1.jpg", "https://example.com/images/2.jpg"]
}
```  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 1,
  "title": "Updated Apartment",
  "description": "Renovated apartment",
  "capacity": 5,
  "mainImage": "https://example.com/images/main_v2.jpg",
  "longitude": -75.5668,
  "latitude": 6.2442,
  "locationDescription": "Near El Poblado Park",
  "city": "Medellin",
  "pricePerNight": 175.00,
  "images": ["https://example.com/images/1.jpg", "https://example.com/images/2.jpg"]
}
```  
**Success Criteria:** The response returns the 200 OK code and the body reflects the changes.  
**Expected Result:** Receive a HTTP 200 response code and the updated accommodation.  

**Test Execution:**  
Enter the updated data in Postman and send the PUT request.  

![update_accommodation_body](/assets/update_accommodation_body.png)
![update_accommodation_headers](/assets/update_accommodation_headers.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![update_accommodation_response](/assets/update_accommodation_response.png)

Validate the updated data in the database.  

![update_accommodation_db](/assets/update_accommodation_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and changes were validated in the database.  

## Test Case 4: Search Accommodations with Filters

**Endpoint:** localhost:8080/api/v1/accommodations/search?city=Medellin&minCapacity=2&maxPrice=200.00&amenityIds=1,2&page=0&size=10  
**HTTP Method:** GET  
**Description:** Searches accommodations by city, minimum capacity, maximum price, and amenities with pagination.  
**Parameters:** city (optional), minCapacity (optional), maxPrice (optional), amenityIds (optional, list), page, size  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 200 (or 204 if no results)  
**Expected Response Body:**  
```json
{
  "content": [
    {
      "id": 1,
      "title": "Modern Apartment",
      "description": "Spacious apartment",
      "capacity": 4,
      "mainImage": "https://example.com/images/main.jpg",
      "longitude": -75.5668,
      "latitude": 6.2442,
      "locationDescription": "Near El Poblado Park",
      "city": "Medellin",
      "pricePerNight": 150.00,
      "images": ["https://example.com/images/1.jpg"]
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1
}
```  
**Success Criteria:** The response returns the 200 OK code and a paginated list (or 204 if empty).  
**Expected Result:** Receive a HTTP 200 response code and the filtered accommodations.  

**Test Execution:**  
Enter the filter parameters in the Postman URL and send the GET request.  

![search_accommodation_filters_request](/assets/search_accommodation_filters_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![search_accommodation_filters_response](/assets/search_accommodation_filters_response.png)

Validate the data in the database (ensure filters match).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and results match the applied filters.  

## Test Case 5: Delete Accommodation (Soft Delete)

**Endpoint:** localhost:8080/api/v1/accommodations/{id} (e.g., /1)  
**HTTP Method:** DELETE  
**Description:** Performs a soft delete by setting isDeleted to true. Requires the user to be the owner.  
**Parameters:** id (in path, e.g., 1)  
**Header:** X-Username: [host user email]  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "message": "Accommodation deleted successfully"
}
```  
**Success Criteria:** The response returns the 200 OK code and the success message.  
**Expected Result:** Receive a HTTP 200 response code and confirmation of deletion.  

**Test Execution:**  
Enter the ID in Postman and send the DELETE request.  

![delete_accommodation_request](/assets/delete_accommodation_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![delete_accommodation_response](/assets/delete_accommodation_response.png)

Validate the data in the database (ensure isDeleted = 1).  

![delete_accommodation_db](/assets/delete_accommodation_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the soft delete was validated in the database.  

## Test Case 6: List All Accommodations

**Endpoint:** localhost:8080/api/v1/accommodations?page=0&size=10  
**HTTP Method:** GET  
**Description:** Retrieves a paginated list of all active accommodations.  
**Parameters:** page (default 0), size (default 10)  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 200 (or 204 if no results)  
**Expected Response Body:**  
```json
{
  "content": [
    {
      "id": 1,
      "title": "Modern Apartment",
      "description": "Spacious apartment in the city center",
      "capacity": 4,
      "mainImage": "https://example.com/images/main.jpg",
      "longitude": -75.5668,
      "latitude": 6.2442,
      "locationDescription": "Near El Poblado Park",
      "city": "Medellin",
      "pricePerNight": 150.00,
      "images": ["https://example.com/images/1.jpg", "https://example.com/images/2.jpg"]
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1
}
```  
**Success Criteria:** The response returns the 200 OK code and a paginated list of active accommodations.  
**Expected Result:** Receive a HTTP 200 response code and the list of accommodations.  

**Test Execution:**  
Enter the pagination parameters in the Postman URL and send the GET request.

![list_accommodations_request](/assets/list_accommodations_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![list_accommodations_response](/assets/list_accommodations_response.png)

Validate the data in the database (ensure only accommodations with isDeleted = false are listed).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and only active accommodations are listed.  