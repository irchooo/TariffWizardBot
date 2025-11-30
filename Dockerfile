# Этап 1: Сборка (используем Maven)
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
# Собираем проект и пропускаем тесты (для ускорения сборки образа)
RUN mvn clean package -DskipTests

# Этап 2: Запуск (JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Копируем JAR-файл (предполагаем, что главный модуль создает jar с таким именем)
# Если у тебя несколько модулей, нужно указать путь к jar запускаемого модуля,
# например: COPY --from=builder /app/tariff-wizard-bot/target/*.jar app.jar
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]