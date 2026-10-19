<div align="center">

<img src="src/main/webapp/resources/weather-app.png" alt="Weather App" width="140"/>

# Weather App

Веб-приложение для отслеживания погоды: поиск городов, сохранение избранных локаций и просмотр текущей погоды по ним.

**Java 22 · Spring MVC · PostgreSQL**

</div>

## О проекте

Классическое Spring MVC-приложение (без Spring Boot), собираемое в WAR и разворачиваемое в Tomcat. Пользователи регистрируются, входят по сессии в cookie, ищут города через геокодинг OpenWeather и сохраняют их в избранное. На главной странице погода по всем сохранённым локациям загружается параллельно.

## Стек

| Слой | Технологии |
|---|---|
| Язык | Java 22 |
| Веб | Spring MVC 6.2, Jakarta Servlet 6.1, Thymeleaf, Bootstrap 5 |
| Данные | Spring Data JPA, Hibernate, PostgreSQL, HikariCP, Flyway |
| Прочее | Lombok, Jackson, Hibernate Validator, RestTemplate, SLF4J + Logback |
| Тесты | JUnit Jupiter |
| Сборка | Maven, WAR-упаковка |

## Функционал

- **Регистрация и вход** - валидация форм, уникальность логина, пароли хранятся в виде хэша.
- **Сессии** - серверные сессии (UUID) в таблице `Sessions`, идентификатор передаётся через cookie; interceptor проверяет доступ к защищённым страницам.
- **Поиск локаций** - поиск городов по названию через Geocoding API OpenWeather (до 10 совпадений).
- **Избранное** - добавление и удаление локаций, защита от дубликатов.
- **Погода** - текущая погода по всем сохранённым локациям, запросы выполняются параллельно (`CompletableFuture`).
- **Обработка ошибок** - централизованный `GlobalExceptionHandler` со страницей ошибок (сессия не найдена, локация не найдена, дубликаты и т.д.).

## Быстрый старт

### Требования

- JDK 22
- PostgreSQL 14+
- Apache Tomcat 11 (Servlet 6.1)
- API-ключ [OpenWeather](https://openweathermap.org/api)

### Настройка

1. Создайте базу данных `WeatherAPI` в PostgreSQL.
2. Укажите параметры подключения и API-ключ в `src/main/resources/application.properties`:

   ```properties
   db.url=jdbc:postgresql://localhost:5432/WeatherAPI
   db.username=postgres
   db.password=postgres
   openweather.api.key=<ваш_ключ>
   ```

   Схема создаётся автоматически миграциями Flyway при первом запуске.

### Сборка и запуск

```bash
mvn clean package
```

Скопируйте `target/WeatherAPI.war` в `webapps/` Tomcat и откройте <http://localhost:8080/WeatherAPI/>.

## Структура проекта

```
src/main/java/ru/
├── config/         # Конфигурация Spring, инициализатор приложения
├── controller/     # Страницы: главная, поиск, вход, регистрация
│   ├── error/      # Глобальный обработчик исключений
│   └── interceptor/# Проверка сессии
├── service/        # Бизнес-логика: сессии, локации, OpenWeather API
├── repository/     # Spring Data JPA репозитории
├── entity/         # JPA-сущности (User, Location, Session)
│   └── dto/        # Формы и DTO ответов OpenWeather
├── exception/      # Доменные исключения
└── util/           # Кастомный десериализатор ответов о погоде
```
