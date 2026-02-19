# 📚 Folio — AI Book Explorer

A modern full-stack web application that lets you search millions of books, view covers and summaries, and generate personalized AI action items using free LLMs.

---

## ✨ Features

- 🔍 **Book Search** — Searches 20M+ books via Open Library (free, no key needed)
- 📖 **Rich Details** — Covers, summaries, subjects, publication year, page count
- 🤖 **AI Action Items** — 7 personalized action steps per book using Groq's free LLMs
- 🎨 **Editorial Design** — Refined literary aesthetic with smooth animations
- 🐳 **Docker Ready** — One-command deployment

---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- A free [Groq API key](https://console.groq.com) (for AI features)

### 1. Clone / download the project
```bash
git clone <repo>
cd book-explorer
```

### 2. Set up environment
```bash
cp .env.example .env
# Edit .env and paste your GROQ_API_KEY
```

### 3. Run with Docker
```bash
docker-compose up --build
```

### 4. Open the app
Visit [http://localhost:8080](http://localhost:8080)

---

## 🛠 Tech Stack

| Layer      | Technology                                    |
|------------|-----------------------------------------------|
| Backend    | Java 21 + Spring Boot 3.2                    |
| HTTP Client | Spring WebFlux (reactive WebClient)          |
| Frontend   | Vanilla HTML/CSS/JS (served by Spring Boot)   |
| Book Data  | [Open Library API](https://openlibrary.org)  |
| AI         | [Groq API](https://console.groq.com) (free)  |
| Container  | Docker + Docker Compose                       |
| Proxy      | Nginx (optional profile)                      |

---

## 🔑 API Keys

### Groq (Free AI)
1. Go to [https://console.groq.com](https://console.groq.com)
2. Sign up for a free account
3. Create an API key
4. Add it to your `.env` file

**Free models available:**
- `llama3-8b-8192` — Fast, great for action items (default)
- `llama3-70b-8192` — More nuanced responses
- `mixtral-8x7b-32768` — Long context
- `gemma-7b-it` — Lightweight

### Open Library
No API key needed! It's completely free and open.

---

## 🏗 Project Structure

```
book-explorer/
├── src/
│   └── main/
│       ├── java/com/bookexplorer/
│       │   ├── BookExplorerApplication.java   # Entry point
│       │   ├── config/
│       │   │   └── WebClientConfig.java        # HTTP clients
│       │   ├── controller/
│       │   │   └── BookController.java         # REST API
│       │   ├── service/
│       │   │   ├── BookService.java            # Open Library integration
│       │   │   └── AiService.java              # Groq AI integration
│       │   └── dto/                            # Data transfer objects
│       └── resources/
│           ├── application.yml                 # App configuration
│           └── static/
│               └── index.html                  # Frontend SPA
├── nginx/
│   └── nginx.conf                              # Reverse proxy config
├── Dockerfile                                  # Multi-stage build
├── docker-compose.yml                          # Full deployment
├── .env.example                                # Environment template
└── pom.xml                                     # Maven dependencies
```

---

## 🌐 REST API

### Search Books
```
GET /api/search?q={query}&limit={limit}
```
**Response:**
```json
{
  "query": "atomic habits",
  "total": 145,
  "books": [
    {
      "key": "/works/OL...",
      "title": "Atomic Habits",
      "author": "James Clear",
      "year": 2018,
      "coverUrl": "https://covers.openlibrary.org/...",
      "summary": "...",
      "subjects": ["habits", "productivity"],
      "pages": 320
    }
  ]
}
```

### Generate Action Items
```
POST /api/action-items
Content-Type: application/json

{
  "title": "Atomic Habits",
  "author": "James Clear",
  "summary": "...",
  "subjects": ["habits", "productivity"]
}
```
**Response:**
```json
{
  "bookTitle": "Atomic Habits",
  "bookAuthor": "James Clear",
  "model": "llama3-8b-8192",
  "actionItems": [
    "Start with a 2-minute version of any habit you want to build...",
    "..."
  ]
}
```

### Health Check
```
GET /api/health
```

---

## 🐳 Docker Commands

```bash
# Start (with build)
docker-compose up --build

# Start in background
docker-compose up -d --build

# View logs
docker-compose logs -f book-explorer

# Stop
docker-compose down

# Start with Nginx reverse proxy
docker-compose --profile with-nginx up -d --build

# Rebuild without cache
docker-compose build --no-cache
```

---

## 💻 Local Development (without Docker)

### Prerequisites
- Java 21+
- Maven 3.9+

```bash
# Run locally
export GROQ_API_KEY=your_key_here
./mvnw spring-boot:run

# Build JAR
./mvnw package -DskipTests

# Run JAR
java -jar target/book-explorer-1.0.0.jar
```

---

## 🔧 Configuration

All settings in `application.yml` can be overridden with environment variables:

| Environment Variable   | Default              | Description                    |
|------------------------|----------------------|--------------------------------|
| `GROQ_API_KEY`         | *(empty)*            | Your Groq API key              |
| `GROQ_MODEL`           | `llama3-8b-8192`     | AI model to use                |
| `SERVER_PORT`          | `8080`               | Application port               |

---

## 📄 License

MIT License — use freely for personal and commercial projects.
