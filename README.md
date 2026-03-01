# Tennis Scoreboard 🎾

Веб-приложение для ведения счета теннисных матчей.
Реализовано на чистых Java Servlets + JSP + JDBC (без Spring/Hibernate).
##  Live Demo
 **Попробовать приложение вживую:**  
 **[http://85.198.64.202/](http://85.198.64.202/)**

## 🛠 Технологический стек
- **Java 17**
- **Maven** (сборка)
- **Jakarta Servlets & JSP** (MVC)
- **PostgreSQL** (хранение данных)
- **JDBC / HikariCP** (работа с БД)
- **Docker & Docker Compose** (деплой)
- **JUnit 5** (тесты)

## 🚀 Быстрый старт (Docker)

Для запуска требуется установленный Docker и Docker Compose.

1. Клонируйте репозиторий:
   git clone https://github.com/your-username/tennis-scoreboard.git
2. cd tennis-scoreboard
3. Соберите проект и запустите контейнеры одной командой:
# Linux / macOS
./mvnw clean package && docker-compose up -d --build

# Windows
mvn clean package && docker-compose up -d --build
Приложение доступно по адресу: http://localhost:8080
