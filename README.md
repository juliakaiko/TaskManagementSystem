# Task Management System 🚀

Современная система управления задачами с REST API, реализованная на Spring Boot с полным циклом аутентификации и авторизации.

## 🌟 Ключевые возможности

- 🔐 Аутентификация через **JWT** + ролевая модель (ADMIN, USER)
- 📊 Полноценное **CRUD** для пользователей, задач и комментариев к ним
- 🗃️ Версионность БД через **Liquibase**
- 📑 Автогенерация API-документации (**Swagger UI**)
- 🐳 Готовая **Docker**-конфигурация
- 🧩 Поддержка **DTO** через MapStruct

## 🛠 Технологический стек

| Категория       | Технологии                          |
|-----------------|-------------------------------------|
| **Backend**     | Java 17+, Spring Boot 3.3.4          |
| **Security**    | Spring Security, JWT                |
| **Database**    | PostgreSQL, Liquibase               |
| **Dev Tools**   | Lombok, MapStruct, Swagger UI       |
| **Infra**       | Docker, Docker Compose              |

### Требования
- Java 17+
- Docker & Docker Compose
- Maven

### Запуск через Docker
git clone https://github.com/juliakaiko/TaskManagementSystem.git
cd TaskManagementSystem
docker-compose up --build
Приложение будет доступно на:
http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html

###Локальная разработка
Создайте БД PostgreSQL:
CREATE DATABASE tasksystemDB;

###Настройте application.properties:

spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.url=jdbc:postgresql://localhost:5432/tasksystemDB
spring.liquibase.user=postgres
spring.liquibase.password= password

spring.datasource.driver-class-name = org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/tasksystemDB
spring.datasource.username=postgres
spring.datasource.password= password

###Запустите приложение:
mvn spring-boot:run

# API Документация 📚

🛠 AuthController

1. Аутентификация пользователя

`POST /start/authentication`

**Примеры запросов:**
```http
POST http://localhost:8080/start/authentication
Content-Type: application/json

# Валидные данные пользователя
{
  "email": "user1@yandex.ru",
  "password": "user1"
}

# Данные администратора
{
  "email": "admin1@yandex.ru",
  "password": "admin1"
}

# Невалидные данные (400 Bad Request)
{
  "email": "user1yandex.ru",
  "password": " "
}
2. Регистрация пользователя

POST /start/registration
POST http://localhost:8080/start/registration
Content-Type: application/json

# Валидные данные
{
  "email": "user@gmail.com",
  "password": "password",
  "role": "USER"
}

# Невалидные данные (400 Bad Request)
{
  "email": "user1yandex.ru",
  "password": " ",
  "role": "CAT"
}

🛠 PerformerController

a) Изменение статуса задачи
PUT /api/performer/change_status/{taskId}/{status}
Примеры:
PUT http://localhost:8080/api/performer/change_status/4/completed
# 200 OK

PUT http://localhost:8080/api/performer/change_status/6/completed
# 403 Forbidden - "This user is not assigned to perform this task with id"

PUT http://localhost:8080/api/performer/change_status/4/LALLA
# 400 Bad Request - "Invalid status value"

b) Добавление комментария
POST /api/performer/comments/task/{taskId}
POST http://localhost:8080/api/performer/comments/task/4
Content-Type: application/json

{"text":"CREATED COMMENT"}
# 200 OK

POST http://localhost:8080/api/performer/comments/task/1
# 403 Forbidden

c) Получение комментариев
GET /api/tasks/performer/{userId}
GET http://localhost:8080/api/tasks/performer/1

🛠  AdminController
Управление задачами
Метод	Путь	Описание
POST	/api/tasks	Создание задачи
PUT	/api/tasks/{id}	Обновление задачи
PUT	/api/tasks/change_priority/{id}/{priority}	Изменение приоритета
PUT	/api/tasks/change_status/{id}/{status}	Изменение статуса
PUT	/api/tasks/{taskId}/{performerId}	Назначение исполнителя
DELETE	/api/tasks/{id}	Удаление задачи

Примеры:
POST http://localhost:8080/api/tasks
Content-Type: application/json

{
  "title": "MY CREATED TASK",
  "description": "MY CREATED DESCRIPTION",
  "status": "pending",
  "priority": "high"
}
Управление комментариями
POST http://localhost:8080/api/comments/task/1
Content-Type: application/json

{"text":"CREATED COMMENT"}

Управление пользователями
PUT http://localhost:8080/api/users/1
Content-Type: application/json

{
  "email": "updated_email@yandex.ru",
  "password": "updated_password",
  "role": "USER"
}

🛠  GeneralUserController
Основные GET-эндпоинты
# Задачи
GET http://localhost:8080/api/tasks
GET http://localhost:8080/api/tasks/1
GET http://localhost:8080/api/pageable_tasks?page=0&size=2
GET http://localhost:8080/api/tasks/author/1
GET http://localhost:8080/api/tasks/performer/1

# Комментарии
GET http://localhost:8080/api/comments
GET http://localhost:8080/api/comments/1
GET http://localhost:8080/api/comments/task/2
GET http://localhost:8080/api/pageable_comments?page=0&size=2

# Пользователи
GET http://localhost:8080/api/users
GET http://localhost:8080/api/users/1
GET http://localhost:8080/api/users/find_by_email?email=user1@yandex.ru
GET http://localhost:8080/api/pageable_users?page=0&size=2
###Полная документация доступна в Swagger UI после запуска.

### Структура проекта
src/
├── main/
│ ├── java/
│ │ └── com/myproject/tasksystem/
│ │ ├── advices/ # Глобальные обработчики исключений
│ │ ├── annotations/ # Кастомные аннотации
│ │ ├── configuration/ # SpringSecurity, Swagger, JWT filter-конфигурации
│ │ ├── controller/ # REST API контроллеры
│ │ ├── dto/ # Data Transfer Objects
│ │ ├── exceptions/ # Кастомные исключения
│ │ ├── mapper/ # MapStruct мапперы
│ │ ├── model/ # JPA-сущности (DB модели)
│ │ ├── repository/ # Spring Data JPA репозитории
│ │ ├── service/
│ │ │ ├── impl/ # Реализации сервисов
│ │ │ ├── AuthenticationService.java # Сервис аутентификации
│ │ │ ├── CommentService.java # Логика комментариев
│ │ │ ├── TaskService.java # Бизнес-логика задач
│ │ │ └── UserService.java # Операции с пользователями
│ │ ├── util/ # Вспомогательные утилиты
│ │ └── TasksystemApplication.java # Главный класс приложения
│ └── resources/
│ ├── db.changelog/ # Миграции Liquibase
│ ├── static/ # Статические ресурсы (CSS/JS)
│ ├── templates/ # Thymeleaf/HTML шаблоны
│ └── application.properties # Конфигурация приложения

________________________________________
Автор: juliakaiko
Поддержка: pugaculia94@gmail.com



