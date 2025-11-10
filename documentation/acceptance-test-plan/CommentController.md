# Executed Test Plan for CommentController

## Test Case 1: Create New Comment

**Endpoint:** localhost:8080/api/v1/comments  
**HTTP Method:** POST  
**Description:** Allows an authenticated guest to create a comment/review for an accommodation. Requires authentication.  
**Parameters:** None  
**Header:** Content-Type: application/json, Authorization: Bearer [JWT token]  
**Request Body:**  
```json
{
  "accommodationId": 1,
  "rating": 4,
  "comment": "Great stay, highly recommend!",
  "title": "Excellent Experience"
}
```  
**Expected Response Code:** 201  
**Expected Response Body:**  
```json
{
  "id": 1,
  "accommodationId": 1,
  "userId": 1,
  "rating": 4,
  "comment": "Great stay, highly recommend!",
  "title": "Excellent Experience",
  "createdAt": "2025-10-12T19:27:00Z",
  "isActive": true
}
```  
**Success Criteria:** The response returns the 201 Created code and the body matches the expected format with a generated ID.  
**Expected Result:** Receive a HTTP 201 response code and the created comment.  
**Test Execution:**  

Enter the data to create a new comment in Postman and send the POST request with a valid JWT token.  
![create_comment_body](/documentation/assets/create_comment_body.png)
![create_comment_headers](/documentation/assets/create_comment_headers.png)


**Obtained Result:**  
Received the expected HTTP 201 code.

![create_comment_response](/documentation/assets/create_comment_response.png)

Validate the created data in the database (comment table in MariaDB).  

**Test Passed? (Yes/No):** Yes
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data was validated in the database.  

## Test Case 2: Get Comments by Accommodation

**Endpoint:** localhost:8080/api/v1/comments/accommodation/{accommodationId} (e.g., /1)  
**HTTP Method:** GET  
**Description:** Retrieves all non-deleted comments for a specific accommodation, ordered by creation date.  
**Parameters:** accommodationId (in path, e.g., 1)  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
[
  {
    "id": 1,
    "accommodationId": 1,
    "userId": 1,
    "rating": 4,
    "comment": "Great stay, highly recommend!",
    "title": "Excellent Experience",
    "createdAt": "2025-10-12T19:27:00Z",
    "isActive": true
  }
]
```  
**Success Criteria:** The response returns the 200 OK code and a list of non-deleted comments.  
**Expected Result:** Receive a HTTP 200 response code and the list of comments.  

**Test Execution:**  
Enter the accommodation ID in Postman and send the GET request.

![get_comments_by_accommodation_request](/documentation/assets/get_comments_by_accommodation_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.  

![get_comments_by_accommodation_response](/documentation/assets/get_comments_by_accommodation_response.png)

Validate the data in the database (ensure only isActive = true records for the accommodation).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data matches the database.  

## Test Case 3: Get Paginated Comments by Accommodation

**Endpoint:** localhost:8080/api/v1/comments/accommodation/{accommodationId}/paged (e.g., /1?paged=0&size=5)  
**HTTP Method:** GET  
**Description:** Retrieves paginated non-deleted comments for a specific accommodation, ordered by creation date.  
**Parameters:** accommodationId (in path, e.g., 1), page, size (e.g., page=0&size=5)  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "content": [
    {
      "id": 1,
      "accommodationId": 1,
      "userId": 1,
      "rating": 4,
      "comment": "Great stay, highly recommend!",
      "title": "Excellent Experience",
      "createdAt": "2025-10-12T19:27:00Z",
      "isActive": true
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 1
}
```  
**Success Criteria:** The response returns the 200 OK code and a paginated list of non-deleted comments.  
**Expected Result:** Receive a HTTP 200 response code and the paginated comments.  

**Test Execution:**  
Enter the accommodation ID and pagination parameters in Postman and send the GET request.

![get_paginated_comments_by_accommodation_request](/documentation/assets/get_paginated_comments_by_accommodation_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.

![get_paginated_comments_by_accommodation_response](/documentation/assets/get_paginated_comments_by_accommodation_response.png)

Validate the data in the database (ensure pagination matches active records).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and pagination worked as expected.  

## Test Case 4: Get Comments by User

**Endpoint:** localhost:8080/api/v1/comments/user/{userId} (e.g., /1)  
**HTTP Method:** GET  
**Description:** Retrieves all non-deleted comments made by a specific user. Requires authentication.  
**Parameters:** userId (in path, e.g., 1)  
**Header:** Authorization: Bearer [JWT token]  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
[
  {
    "id": 1,
    "accommodationId": 1,
    "userId": 1,
    "rating": 4,
    "comment": "Great stay, highly recommend!",
    "title": "Excellent Experience",
    "createdAt": "2025-10-12T19:27:00Z",
    "isActive": true
  }
]
```  
**Success Criteria:** The response returns the 200 OK code and a list of non-deleted comments by the user.  
**Expected Result:** Receive a HTTP 200 response code and the list of comments.  

**Test Execution:**  
Enter the user ID in Postman and send the GET request with a valid JWT token.

![get_comments_by_user_request](/documentation/assets/get_comments_by_user_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.

![get_comments_by_user_response](/documentation/assets/get_comments_by_user_response.png)


Validate the data in the database (ensure only the user’s active comments).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and data matches the user’s comments.  

## Test Case 5: Get Average Rating for an Accommodation

**Endpoint:** localhost:8080/api/comments/accommodation/{accommodationId}/average-rating (e.g., /1)  
**HTTP Method:** GET  
**Description:** Calculates the average rating of non-deleted comments for a specific accommodation.  
**Parameters:** accommodationId (in path, e.g., 1)  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
4.0
```  
**Success Criteria:** The response returns the 200 OK code and the average rating as a double value.  
**Expected Result:** Receive a HTTP 200 response code and the average rating.  

**Test Execution:**  
Enter the accommodation ID in Postman and send the GET request.

![get_average_rating_request](/documentation/assets/get_average_rating_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.

![get_average_rating_response](/documentation/assets/get_average_rating_response.png)

Validate the calculation in the database (average of ratings where isActive = true).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the average rating matches the database.  

## Test Case 6: Get Comment Count for an Accommodation

**Endpoint:** localhost:8080/api/v1/comments/accommodation/{accommodationId}/count (e.g., /1)  
**HTTP Method:** GET  
**Description:** Counts the number of non-deleted comments for a specific accommodation.  
**Parameters:** accommodationId (in path, e.g., 1)  
**Header:** None required  
**Request Body:** None  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
1
```  
**Success Criteria:** The response returns the 200 OK code and the comment count as a long value.  
**Expected Result:** Receive a HTTP 200 response code and the comment count.  

**Test Execution:**  
Enter the accommodation ID in Postman and send the GET request.

![get_comment_count_request](/documentation/assets/get_comment_count_request.png)

**Obtained Result:**  
Received the expected HTTP 200 code.

![get_comment_count_response](/documentation/assets/get_comment_count_response.png)

Validate the count in the database (count of records where isActive = true).  

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the count matches the database.  

## Test Case 7: Update a Comment

**Endpoint:** localhost:8080/api/comments/{id} (e.g., /1)  
**HTTP Method:** PUT  
**Description:** Allows an authenticated guest to update their own comment. Requires authentication.  
**Parameters:** id (in path, e.g., 1)  
**Header:** Content-Type: application/json, Authorization: Bearer [JWT token]  
**Request Body:**  
```json
{
  "text": "Casa muy fria, no recomendada",
  "rating": 2
}
```  
**Expected Response Code:** 200  
**Expected Response Body:**  
```json
{
  "id": 1,
  "userId": 2,
  "accommodationId": 1,
  "text": "Casa muy fria, no recomendada",
  "rating": 2,
  "createdAt": "2025-10-15T22:22:04.463679"
}
```  
**Success Criteria:** The response returns the 200 OK code and the body reflects the updated data.  
**Expected Result:** Receive a HTTP 200 response code and the updated comment.  

**Test Execution:**  
Enter the ID and updated data in Postman and send the PUT request with a valid JWT token.

![update_comment_body](/documentation/assets/update_comment_body.png)
![update_comment_headers](/documentation/assets/update_comment_headers.png)

**Obtained Result:**  
Received the expected HTTP 200 code.

![update_comment_response](/documentation/assets/update_comment_response.png)

Validate the updated data in the database.

![update_comment_db](/documentation/assets/update_comment_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and changes were validated in the database.  

## Test Case 8: Delete a Comment (Soft Delete)

**Endpoint:** localhost:8080/api/v1/comments/{id} (e.g., /1)  
**HTTP Method:** DELETE  
**Description:** Allows an authenticated guest to delete their own comment (soft delete). Requires authentication.  
**Parameters:** id (in path, e.g., 1)  
**Header:** Authorization: Bearer [JWT token]  
**Request Body:** None  
**Expected Response Code:** 204  
**Expected Response Body:** None  
**Success Criteria:** The response returns the 204 No Content code and the comment is marked as inactive in the database.  
**Expected Result:** Receive a HTTP 204 response code and confirmation of deletion.  

**Test Execution:**  
Enter the ID in Postman and send the DELETE request with a valid JWT token.

![delete_comment_request](/documentation/assets/delete_comment_request.png)

**Obtained Result:**  
Received the expected HTTP 204 code.

![delete_comment_response](/documentation/assets/delete_comment_response.png)

Validate the data in the database (ensure isActive = false for the specified ID).

![delete_comment_db](/documentation/assets/delete_comment_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the soft delete was validated in the database.  

## Test Case 9: Reply to a Comment

**Endpoint:** localhost:8080/api/v1/comments/{id}/reply (e.g., /1)  
**HTTP Method:** POST  
**Description:** Allows an authenticated host to reply to a guest's comment on their accommodation. Requires authentication.  
**Parameters:** id (in path, e.g., 1)  
**Header:** Content-Type: application/json, Authorization: Bearer [JWT token]  
**Request Body:**  
```json
{
  "replyText": "Thank you for your feedback!"
}
```  
**Expected Response Code:** 201  
**Expected Response Body:**  
```json
{
  "id": 1,
  "userId": 2,
  "accommodationId": 1,
  "text": "Casa muy fria, no recomendada",
  "rating": 2,
  "createdAt": "2025-10-15T22:22:04.463679"
}
```  
**Success Criteria:** The response returns the 201 Created code and the body includes the reply.  
**Expected Result:** Receive a HTTP 201 response code and the comment with the reply.  

**Test Execution:**  
Enter the ID and reply data in Postman and send the POST request with a valid JWT token for a host.

![reply_to_comment_body](/documentation/assets/reply_to_comment_body.png)
![reply_to_comment_headers](/documentation/assets/reply_to_comment_headers.png)

**Obtained Result:**  
Received the expected HTTP 201 code.  

![reply_to_comment_response](/documentation/assets/reply_to_comment_response.png)

Validate the updated data in the database (ensure reply and repliedBy are recorded).

![reply_to_comment_db](/documentation/assets/reply_to_comment_db.png)

**Test Passed? (Yes/No):** Yes  
**Test Status (Accepted / Rejected):** Accepted  
**Observations:** The test is accepted as the expected code was received and the reply was validated in the database.  