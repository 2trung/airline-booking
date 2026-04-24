# Location Service - Airport API Documentation

This document outlines the available endpoints, requests, and response models for the Airport API.

## Base URL
`/airports`

---

## Data Models

### 1. `AirportRequest`
Used for creating or updating an airport.

| Field | Type | Description / Constraints |
|-------|------|---------------------------|
| `iataCode` | `String` | **Required**. Must be exactly 3 characters. |
| `name` | `String` | **Required**. Name of the airport. |
| `timeZone` | `String` | Time zone of the airport (e.g., "America/New_York"). |
| `address` | `Object` | Physical address details of the airport. |
| `cityId` | `Long` | **Required**. The ID of the city where the airport is located. |
| `geoCode` | `Object` | Latitude/Longitude coordinates object. |

### 2. `AirportResponse`
Returned when querying, creating, or updating an airport.

| Field | Type | Description |
|-------|------|-------------|
| `id` | `Long` | Unique identifier for the airport. |
| `iataCode` | `String` | 3-letter IATA code. |
| `name` | `String` | Short name of the airport. |
| `detailedName` | `String` | A more detailed/full name. |
| `timeZone` | `String` | Configured time zone for the airport. |
| `address` | `Object` | Details of the address. |
| `city` | `CityResponse` | Full localized details for the associated city. |
| `geoCode` | `Object` | Coordinates structure. |
| `analytics` | `Object` | Relevant analytics/metadata. |

---

## Endpoints

### 1. Create Airport
Creates a new airport entry.

* **URL**: `/airports`
* **Method**: `POST`
* **Request Body**: `AirportRequest`
* **Response Body**: `AirportResponse`
* **Status Code**: `201 Created`

### 2. Create Bulk Airports
Creates multiple airports simultaneously in a batch.

* **URL**: `/airports/bulk`
* **Method**: `POST`
* **Request Body**: `List<AirportRequest>` (Array of `AirportRequest` objects)
* **Response Body**: `List<AirportResponse>` (Array of newly created `AirportResponse` objects)
* **Status Code**: `201 Created`

### 3. Get Airport By ID
Fetches details of a single airport by its unique ID.

* **URL**: `/airports/{id}`
* **Method**: `GET`
* **Path Variables**:
  * `id` (`Long`) - The unique ID of the airport
* **Response Body**: `AirportResponse`
* **Status Code**: `200 OK`

### 4. Get All Airports
Retrieves a complete list of all airports.

* **URL**: `/airports`
* **Method**: `GET`
* **Response Body**: `List<AirportResponse>`
* **Status Code**: `200 OK`

### 5. Get Airports By City ID
Retrieves all airports belonging to a specified city.

* **URL**: `/airports/city/{cityId}`
* **Method**: `GET`
* **Path Variables**:
  * `cityId` (`Long`) - The ID of the city
* **Response Body**: `List<AirportResponse>`
* **Status Code**: `200 OK`

### 6. Update Airport
Updates the information of an existing airport.

* **URL**: `/airports/{id}`
* **Method**: `PUT`
* **Path Variables**:
  * `id` (`Long`) - The unique ID of the airport
* **Request Body**: `AirportRequest`
* **Response Body**: `AirportResponse`
* **Status Code**: `200 OK`

### 7. Delete Airport
Removes an airport record from the system.

* **URL**: `/airports/{id}`
* **Method**: `DELETE`
* **Path Variables**:
  * `id` (`Long`) - The unique ID of the airport
* **Response Body**: *(Empty)*
* **Status Code**: `204 No Content`

---
---

# Location Service - City API Documentation

This section outlines the available endpoints, requests, and response models for the City API.

## Base URL
`/city`

---

## Data Models

### 1. `CityRequest`
Used for creating or updating a city.

| Field | Type | Description / Constraints |
|-------|------|---------------------------|
| `name` | `String` | **Required**. City name (Max 100 chars). |
| `cityCode` | `String` | **Required**. City code (Max 10 chars). |
| `countryCode` | `String` | **Required**. Country code (Max 5 chars). |
| `countryName` | `String` | **Required**. Country name (Max 100 chars). |
| `regionCode` | `String` | Region code (Max 10 chars). |
| `timeZoneOffset` | `String` | Time zone offset (e.g. "+01:00", Max 10 chars). |

### 2. `CityResponse`
Returned when querying, creating, or updating a city.

| Field | Type | Description |
|-------|------|-------------|
| `id` | `Long` | Unique identifier for the city. |
| `name` | `String` | Name of the city. |
| `cityCode` | `String` | City code. |
| `countryCode` | `String` | Country code. |
| `countryName` | `String` | Country name. |
| `regionCode` | `String` | Region code. |
| `timeZoneOffset`| `String` | Time zone offset. |

---

## Endpoints

### 1. Create City
Creates a new city entry.

* **URL**: `/city`
* **Method**: `POST`
* **Request Body**: `CityRequest`
* **Response Body**: `CityResponse`
* **Status Code**: `201 Created`

### 2. Create Bulk Cities
Creates multiple cities simultaneously in a batch.

* **URL**: `/city/bulk`
* **Method**: `POST`
* **Request Body**: `List<CityRequest>` (Array of `CityRequest` objects)
* **Response Body**: `List<CityResponse>` (Array of newly created `CityResponse` objects)
* **Status Code**: `201 Created`

### 3. Get City By ID
Fetches details of a single city by its unique ID.

* **URL**: `/city/{id}`
* **Method**: `GET`
* **Path Variables**:
  * `id` (`Long`) - The unique ID of the city
* **Response Body**: `CityResponse`
* **Status Code**: `200 OK`

### 4. Get All Cities
Retrieves a paginated list of all cities.

* **URL**: `/city`
* **Method**: `GET`
* **Query Parameters**:
  * `page` (`int`) - Page index (Default: 0)
  * `size` (`int`) - Page size (Default: 20)
  * `sortBy` (`String`) - Field to sort by (Default: "name")
  * `sortDirection` (`String`) - Sort direction (Default: "asc")
* **Response Body**: `Page<CityResponse>`
* **Status Code**: `200 OK`

### 5. Update City
Updates the information of an existing city.

* **URL**: `/city/{id}`
* **Method**: `PUT`
* **Path Variables**:
  * `id` (`Long`) - The unique ID of the city
* **Request Body**: `CityRequest`
* **Response Body**: `CityResponse`
* **Status Code**: `200 OK`

### 6. Delete City
Removes a city record from the system.

* **URL**: `/city/{id}`
* **Method**: `DELETE`
* **Path Variables**:
  * `id` (`Long`) - The unique ID of the city
* **Response Body**: *(Empty)*
* **Status Code**: `204 No Content`

### 7. Search Cities
Searches for cities based on a keyword.

* **URL**: `/city/search`
* **Method**: `GET`
* **Query Parameters**:
  * `keyword` (`String`) - Search keyword
  * `page` (`int`) - Page index (Default: 0)
  * `size` (`int`) - Page size (Default: 20)
* **Response Body**: `Page<CityResponse>`
* **Status Code**: `200 OK`

### 8. Get Cities By Country Code
Retrieves a paginated list of cities for a given country code.

* **URL**: `/city/country/{countryCode}`
* **Method**: `GET`
* **Path Variables**:
  * `countryCode` (`String`) - The country code
* **Query Parameters**:
  * `page` (`int`) - Page index (Default: 0)
  * `size` (`int`) - Page size (Default: 20)
* **Response Body**: `Page<CityResponse>`
* **Status Code**: `200 OK`

### 9. Check City Exists
Checks whether a city exists by its city code.

* **URL**: `/city/exists/{cityCode}`
* **Method**: `GET`
* **Path Variables**:
  * `cityCode` (`String`) - The city code
* **Response Body**: `Boolean` (true/false)
* **Status Code**: `200 OK`

---

# Seat Service API Documentation

> **Note on Authentication:** All requests must be routed through the API Gateway. The `X-User-Id` header is automatically resolved and appended by the API Gateway based on your authentication context (e.g., JWT token). The frontend agent **does not** need to manually include `X-User-Id` in the requests.

---

## 1. Cabin Class API (Base path: `/cabin-classes`)

### Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/cabin-classes/{id}` | Get cabin class by ID |
| `GET` | `/cabin-classes/aircraft/{aircraftId}` | Get all cabin classes for a specific aircraft |
| `GET` | `/cabin-classes/aircraft/{id}/name/{cabinClass}` | Get cabin class by aircraft ID and name |
| `POST` | `/cabin-classes` | Create a new cabin class |
| `POST` | `/cabin-classes/create/bulk` | Create multiple cabin classes in bulk |
| `PUT` | `/cabin-classes/{id}` | Update an existing cabin class |
| `DELETE` | `/cabin-classes/{id}` | Delete a cabin class |

### Request Entity: `CabinClassRequest`
```json
{
  "name": "string (Required)",
  "code": "string (Required, 1-5 chars)",
  "description": "string (Max 500 chars)",
  "aircraftId": "number (Required)",
  "displayOrder": "number",
  "isActive": "boolean",
  "isBookable": "boolean",
  "typicalSeatPitch": "number",
  "typicalSeatWidth": "number",
  "seatType": "string"
}
```

### Response Entity: `CabinClassResponse`
```json
{
  "id": "number",
  "name": "string",
  "code": "string",
  "description": "string",
  "aircraftId": "number",
  "displayOrder": "number",
  "isActive": "boolean",
  "isBookable": "boolean",
  "typicalSeatPitch": "number",
  "typicalSeatWidth": "number",
  "seatType": "string",
  "createdAt": "string (ISO-8601)",
  "updatedAt": "string (ISO-8601)",
  "seatMap": {
     // See SeatMapResponse
  }
}
```

---

## 2. Seat Map API (Base path: `/seat-maps`)

### Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/seat-maps/{id}` | Get seat map by ID |
| `GET` | `/seat-maps/cabin-class/{cabinClassId}` | Get seat map associated with a cabin class |
| `POST` | `/seat-maps` | Create a new seat map |
| `POST` | `/seat-maps/create/bulk` | Create multiple seat maps |
| `PUT` | `/seat-maps/{id}` | Update an existing seat map |
| `DELETE` | `/seat-maps/{id}` | Delete a seat map |

### Request Entity: `SeatMapRequest`
```json
{
  "name": "string (Required)",
  "totalRows": "number (Required, Positive)",
  "leftSeatsPerRow": "number (Required, Positive)",
  "rightSeatsPerRow": "number (Required, Positive)",
  "cabinClassId": "number"
}
```

### Response Entity: `SeatMapResponse`
```json
{
  "id": "number",
  "name": "string",
  "totalRows": "number",
  "airlineId": "number",
  "airlineName": "string",
  "airlineCode": "string",
  "cabinClassId": "number",
  "cabinClassName": "string",
  "cabinClassCode": "string",
  "totalSeats": "number",
  "availableSeats": "number",
  "occupiedSeats": "number",
  "seats": [
    // Array of SeatResponse items
  ],
  "windowSeats": "number",
  "aisleSeats": "number",
  "middleSeats": "number",
  "premiumSeats": "number",
  "emergencyExitSeats": "number",
  "leftSeatsPerRow": "number",
  "rightSeatsPerRow": "number"
}
```

---

## 3. Seat API (Base path: `/seats`)

### Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/seats` | Get all seats |
| `GET` | `/seats/{id}` | Get seat details by ID |
| `PUT` | `/seats/{id}` | Update a specific seat |

### Request Entity: `SeatRequest`
```json
{
  "seatNumber": "string (Required, 2-10 chars)",
  "seatRow": "number (Required, Positive)",
  "columnLetter": "string (Character)",
  "seatType": "string (Enum: STANDARD, PREMIUM, etc.)",
  "seatMapId": "number (Required)",
  "cabinClassId": "number",
  "isAvailable": "boolean",
  "isBlocked": "boolean",
  "isEmergencyExit": "boolean",
  "isActive": "boolean",
  "basePrice": "number",
  "premiumSurcharge": "number",
  "hasExtraLegroom": "boolean",
  "hasBassinet": "boolean",
  "isNearLavatory": "boolean",
  "isNearGalley": "boolean",
  "hasPowerOutlet": "boolean",
  "hasTvScreen": "boolean",
  "isWheelchairAccessible": "boolean",
  "hasExtraWidth": "boolean",
  "seatPitch": "number",
  "seatWidth": "number",
  "reclineAngle": "number"
}
```

### Response Entity: `SeatResponse`
```json
{
  "id": "number",
  "seatNumber": "string",
  "seatRow": "number",
  "columnLetter": "string (Character)",
  "seatType": "string",
  "isAvailable": "boolean",
  "isBlocked": "boolean",
  "isEmergencyExit": "boolean",
  "isActive": "boolean",
  "basePrice": "number",
  "premiumSurcharge": "number",
  "totalPrice": "number",
  "hasExtraLegroom": "boolean",
  "hasBassinet": "boolean",
  "isNearLavatory": "boolean",
  "isNearGalley": "boolean",
  "hasPowerOutlet": "boolean",
  "hasTvScreen": "boolean",
  "isWheelchairAccessible": "boolean",
  "hasExtraWidth": "boolean",
  "seatPitch": "number",
  "seatWidth": "number",
  "reclineAngle": "number",
  "seatMapId": "number",
  "seatMapName": "string",
  "cabinClassId": "number",
  "cabinClassName": "string",
  "createdAt": "string (ISO-8601)",
  "updatedAt": "string (ISO-8601)",
  "createdBy": "string",
  "updatedBy": "string",
  "isPremiumSeat": "boolean",
  "isBookable": "boolean",
  "fullPosition": "string",
  "seatCharacteristics": "string"
}
```
