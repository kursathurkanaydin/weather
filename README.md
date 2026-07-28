# Weather - Air Pollution Tracker

A Spring Boot application that fetches air pollution history for a set of supported cities from the [OpenWeatherMap API](https://openweathermap.org/api), stores it in a database, and exposes it through a simple REST API.

For each requested city and date range, the app:
1. Resolves the city's coordinates (geocoding).
2. Fetches hourly air pollution data from OpenWeatherMap for that date range.
3. Reduces it to one record per day, and computes an AQI (Air Quality Index) category for CO, O3 and SO2 based on India's CPCB thresholds.
4. Saves the result so the same data isn't fetched twice.

## Supported cities

`LONDON`, `BARCELONA`, `ANKARA`, `TOKYO`, `MUMBAI`

## Tech stack

- **Java 17**
- **Spring Boot 4.1** (Web MVC, REST Client, Validation, Data JPA)
- **Hibernate / JPA** - ORM
- **Flyway** - database migrations
- **PostgreSQL** - production database
- **H2** (in-memory) - local development database
- **Gradle** - build tool
- **Lombok** - reduces boilerplate code
- **Logback** - logging
- **springdoc-openapi** - Swagger UI / API docs
- **JUnit 5, Mockito, AssertJ** - testing
- **Docker / Docker Compose** - containerized setup

## Prerequisites

- Java 17+ (only needed if you want to run it without Docker)
- Docker and Docker Compose (recommended way to run the project)
- An OpenWeatherMap API key - get a free one at https://openweathermap.org/api

## Getting started

### 1. Clone the project

```bash
git clone <repository-url>
cd weather
```

### 2. Set up your environment variables

Copy the example file and fill in your own values:

```bash
cp .env.example .env
```

Open `.env` and edit it:

| Variable | Description |
|---|---|
| `DB_NAME` | Postgres database name |
| `DB_URL` | JDBC connection URL (defaults to the Docker Compose Postgres service) |
| `DB_USER` | Postgres username |
| `DB_PASSWORD` | Postgres password |
| `DB_DDL_AUTO` | Hibernate schema validation strategy for prod (keep as `validate`) |
| `OPENWEATHER_API_KEY` | Your OpenWeatherMap API key |
| `SPRING_PROFILES_ACTIVE` | `dev` (H2, no setup needed) or `prod` (Postgres) |

## Running the project

### Option A: Docker (recommended)

This starts both the application and a PostgreSQL database, and runs the Flyway migrations automatically.

```bash
docker compose up --build
```

The app will be available at `http://localhost:8080`.
Postgres is exposed on `localhost:5433` (mapped from the container's `5432`) if you want to inspect it with a DB client.

To stop everything:

```bash
docker compose down
```

### Option B: Run locally with Gradle

This uses the `dev` profile with an in-memory H2 database, so no Postgres setup is required.

```bash
./gradlew bootRun
```

Make sure `OPENWEATHER_API_KEY` is set in your `.env` file (or as an environment variable) before starting.

The H2 console is available at `http://localhost:8080/h2-console` while running with the `dev` profile.

### Running tests

```bash
./gradlew test
```

## API Documentation (Swagger)

Once the app is running, interactive API docs are available at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## API Endpoints

### Geocode

| Method | Path | Description |
|---|---|---|
| GET | `/api/geocode?city={city}` | Get latitude/longitude coordinates for a city name |

### Air Pollution

| Method | Path | Description |
|---|---|---|
| GET | `/api/air-pollution` | List all stored air pollution records (paginated: `page`, `size`, `sortBy`, `ascending`) |
| GET | `/api/air-pollution/history?city={city}&startDate={date}&endDate={date}` | Fetch (and store) air pollution history for a city and date range. Missing/incomplete dates are automatically fetched from OpenWeatherMap. |
| GET | `/api/air-pollution/city/{city}` | List all stored records for a given city |
| GET | `/api/air-pollution/city/{city}/range-date?startDate={date}&endDate={date}` | List stored records for a city within a date range |
| DELETE | `/api/air-pollution/{id}` | Delete an air pollution record by id |

Dates are in `yyyy-MM-dd` format.

## Project structure

```
src/main/java/com/mobileaction/weather
├── client        # OpenWeatherMap HTTP client
├── constant      # Error/log message constants
├── controller    # REST controllers
├── dto           # Request/response/mapper DTOs
├── exception     # Custom exceptions + global exception handler
├── model         # JPA entities and enums
├── repository    # Spring Data JPA repositories
└── service       # Business logic
```
