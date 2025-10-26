# 🌤️ Weather App — Backend (Spring Boot)

This is the **backend service** for the Weather App — a secure, fast, and cache-enabled weather information API built with **Spring Boot 3**.  
It integrates with **OpenWeatherMap API**, uses **JWT authentication**, and supports caching to reduce redundant API calls.

---

## 🚀 Features

- ✅ RESTful API with **Spring Boot 3**
- 🔐 **JWT Authentication** via Spring Security
- 🌦️ Integration with **OpenWeatherMap API**
- ⚡ **Spring Cache (Caffeine / ConcurrentMap)** for performance
- 🧱 Structured **API response wrapper**
- 🗂️ Loads city data from `cities.json`
- 🧩 Modular service, DTO, and controller layers
- 🪵 Structured logging and exception handling

---

## 🏗️ Project Structure

```
weather-app-backend
│
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── yasidu
    │   │           └── weather_app
    │   │               ├── config
    │   │               ├── controller
    │   │               ├── dto
    │   │               ├── exception
    │   │               ├── response
    │   │               ├── service
    │   │               │   └── impl
    │   │               └── util
    │   │
    │   └── resources
    │       ├── static
    │       ├── templates
    │       └── (application.yml, cities.json, etc.)
    │
    └── test
        └── java
            └── com
                └── yasidu
                    └── weather_app

```

---

## ⚙️ Tech Stack

| Component | Technology |
|------------|-------------|
| **Framework** | Spring Boot 3 |
| **Security** | Spring Security (JWT) |
| **Cache** | Spring Cache with Caffeine |
| **API Client** | RestTemplate / WebClient |
| **Build Tool** | Maven |
| **JSON Handling** | Jackson |
| **Logging** | SLF4J / Logback |

---

## ⚙️ Setup & Configuration

### 1. Clone Repository
```bash
git clone https://github.com/<your-username>/weather-app.git
cd weather-app/backend
```

### 2. Configure `application.yml`
```yaml
server:
  port: 8080

openweather:
  api:
    base-url: https://api.openweathermap.org/data/2.5
    key: YOUR_API_KEY
    units: metric

app:
  cities:
    file-path: cities.json
```

### 3. Run Application
```bash
mvn spring-boot:run
```

---

## 🔗 API Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|-----------|-------------|----------------|
| `GET` | `/api/weather/{cityCode}` | Get live weather by city code | ✅ Yes |
| `GET` | `/api/weather/cities/all-weather` | Get weather for all cities | ✅ Yes |
| `GET` | `/api/weather/codes` | Get supported city codes | ✅ Yes |

**Example Response**
```json
{
    "success": true,
    "message": "Weather data retrieved successfully",
    "data": {
        "cityCode": "1850147",
        "name": "Tokyo",
        "description": "broken clouds",
        "staticStatus": "Clouds",
        "icon": "04d",
        "temp": 15.89,
        "feels_like": 15.96,
        "temp_min": 14.88,
        "temp_max": 17.01,
        "pressure": 1011,
        "humidity": 93,
        "visibility": 8000,
        "wind_speed": 4.12,
        "wind_deg": 350,
        "sunrise": "2:27 AM",
        "sunset": "1:23 PM",
        "cached": false
    },
    "code": 200
}
```

---

## 🔐 Authentication

- Uses **JWT tokens** or **Auth0 tokens**
- Token must be sent in the `Authorization` header:
  ```http
  Authorization: Bearer eyJhbGciOi...
  ```
- Configured with Spring Security filter chain

---

## 🧠 Caching

- Uses **Spring Cache** to store responses temporarily
- Avoids multiple API calls for the same city
- Can be cleared manually:
  ```java
  weatherService.evictCityCache(cityCode);
  weatherService.evictAllCache();
  ```

---

## 👨‍💻 Author

**Yasidu Pathiraja**  
Full Stack Developer | Spring Boot | React | Auth0  
🌐 [LinkedIn](https://linkedin.com/in/yasindu-pathiraja-411751317) | [GitHub](https://github.com/YasinduP123)

---

## 🧾 License

Licensed under the **MIT License** — free for personal and commercial use.
