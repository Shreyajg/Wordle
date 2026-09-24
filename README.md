# Guess the Word

A full-stack 5-letter word guessing game built using Java, Spring Boot, MongoDB, Spring Security, JWT, and vanilla JavaScript.

The application supports two roles — **Player** and **Admin** — with persistent game state, authentication, Wordle-style feedback, daily game limits, and administrative reports.

---

## Features

### Player

- User registration and login
- Username and password validation
- BCrypt password hashing
- JWT-based authentication using HttpOnly cookies
- Random selection of a 5-letter word from MongoDB
- Wordle-style feedback:
  - 🟩 **Green** — correct letter in the correct position
  - 🟨 **Orange** — correct letter in the wrong position
  - ⬜ **Grey** — letter is not present in the target word
- Maximum of **5 guesses per game**
- Maximum of **3 games per player per day**
- Guesses and game state persisted in MongoDB
- Previous guesses restored after page reload
- Validation of guesses against the stored word list

### Admin

- Role-based access control
- Daily report containing:
  - Number of unique users who played that day
  - Number of correct guesses
- User-specific report containing:
  - Date
  - Number of words tried
  - Number of correct guesses

---

## Tech Stack

### Backend

- **Java 17**
- **Spring Boot**
- **Spring Web**
- **Spring Data MongoDB**
- **Spring Security**
- **JWT**
- **BCrypt**

### Database

- **MongoDB Atlas**

### Frontend

- **HTML5**
- **CSS3**
- **JavaScript**

### Tools

- Maven
- Git / GitHub
- Postman

---

## Architecture

```text
┌──────────────────────────┐
│        Frontend          │
│      HTML / CSS / JS     │
└────────────┬─────────────┘
             │
             │ REST API
             ▼
┌──────────────────────────┐
│      Spring Boot         │
│         Backend          │
├──────────────────────────┤
│ Controllers              │
│ Services                 │
│ Repositories             │
│ Spring Security          │
│ JWT Authentication       │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│      MongoDB Atlas       │
├──────────────────────────┤
│ Users                    │
│ Words                    │
│ Games                    │
└──────────────────────────┘

## Project Structure:
guess-game/
│
├── Backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/guessgame/
│   │       │       ├── controller/
│   │       │       ├── model/
│   │       │       ├── repository/
│   │       │       ├── service/
│   │       │       └── security/
│   │       │
│   │       └── resources/
│   │           └── application.properties
│   │
│   ├── pom.xml
│   └── mvnw
│
├── Frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
│
└── README.md

## Game Flow:
Login / Register
       │
       ▼
   Start Game
       │
       ▼
Random 5-letter word
       │
       ▼
   Submit Guess
       │
       ├─────────────── Correct ──────────────► WON
       │
       ├─────────────── Incorrect
       │                       │
       │                       ▼
       │                Guesses remaining
       │                       │
       │                       ▼
       │                    Continue
       │
       └────────────── 5th incorrect ─────────► LOST

## Authentication
Login
  │
  ▼
Validate credentials
  │
  ▼
Generate JWT
  │
  ▼
Store JWT in HttpOnly cookie
  │
  ▼
Authenticated requests

## Role based Authorization:

The game supports :
PLAYER
ADMIN

## API Endpoints:

PLAYER:

| Method | Endpoint         | Description               |
| ------ | ---------------- | ------------------------- |
| POST   | `/auth/register` | Register a player         |
| POST   | `/auth/login`    | Login                     |
| POST   | `/auth/logout`   | Logout                    |
| GET    | `/auth/me`       | Get authenticated user    |
| POST   | `/games/start`   | Start or resume a game    |
| POST   | `/games/guess`   | Submit a guess            |
| GET    | `/games/current` | Retrieve the current game |

Admin:

| Method | Endpoint                        | Description                          |
| ------ | ------------------------------- | ------------------------------------ |
| GET    | `/admin/daily-report`           | Get daily statistics                 |
| GET    | `/admin/user-report/{playerId}` | Get statistics for a specific player |

## DATABASE:

### User:
id
username
passwordHash
role

### Word:
id
word

### Games:
id
playerId
targetWord
guesses
status
createdAt

where status can be:
IN_PROGRESS
WON
LOST

## Daily Game Limit

Each player can start a maximum of 3 games per day.

When starting a game, the backend checks the number of games created by that player during the current day.

An active game is resumed instead of creating another game.

## Admin Reports
Daily Report

### The daily report counts:

Unique users who played on that date
Number of games successfully won

Unique users are determined from the distinct playerId values of games created during the selected date.

Example:
{
    "noOfUsers": 10,
    "noOfCorrectGuesses": 6
}
### User Report

The user report provides statistics for an individual player on a selected date.
{
    "date": "2026-09-23",
    "noOfWordsTried": 1,
    "noOfCorrectGuesses": 0
}

## Running Locally

### Prerequisites
- Java 17 or later
- MongoDB Atlas account
- Git
-The project includes the Maven Wrapper, so Maven does not need to be installed separately.
#### 1. Clone the repository

git clone <YOUR_GITHUB_REPOSITORY_URL>
cd guess-game

#### 2. Configure environment variables
The backend requires:
MONGODB_URI
JWT_SECRET

#### 3. Start the backend

From the Backend directory:
- .\mvnw.cmd spring-boot:run

The Backend runs on:
- http://localhost:8080

#### 4.Start the frontend

From the Frontend directory:

- python -m http.server 5500

then open :
- http://localhost:5500

## Testing

The application was tested for:

- User registration
- Login and logout
- JWT authentication
- Role-based authorization
- Game creation
- Random word selection
- Word validation
- GREEN / ORANGE / GREY feedback
- Correct guesses
- Failed games
- Five-guess limit
- Three-games-per-day limit
- Persistence of guesses
- Game restoration after page reload
- MongoDB persistence
- Daily admin reports
- User-specific admin reports
- REST API testing using Postman

## Security

The application uses:

- BCrypt password hashing
- JWT authentication
- HttpOnly authentication cookies
- Role-based authorization
- Protected admin endpoints
- CORS configuration
- Environment variables for secrets

## Screenshots:
-- Admin Login:

<img width="740" height="478" alt="image" src="https://github.com/user-attachments/assets/81a3c224-3ff4-43f9-a322-4227d24a6879" />

-- Admin Dashboard:

<img width="693" height="701" alt="image" src="https://github.com/user-attachments/assets/a702e853-0415-42d6-8c0b-4be41e86fffd" />

-- Player Login:
<img width="658" height="470" alt="image" src="https://github.com/user-attachments/assets/de2553d8-b5b9-4ed3-9541-d737750eb2bc" />

### Game:

- Game Won:

<img width="702" height="763" alt="image" src="https://github.com/user-attachments/assets/8ead572f-0be0-458c-ba39-1a05b9df2362" />

- Game Lost:
<img width="807" height="825" alt="image" src="https://github.com/user-attachments/assets/e53a3845-b1e6-41dd-be0a-470bd5d13024" />

- 3 games / day limit reached:

<img width="705" height="562" alt="image" src="https://github.com/user-attachments/assets/b978e1b8-84bc-40ab-8a15-995e500ffe43" />

## Author

- Shreya Jaganatha Gowda











