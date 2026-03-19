# TP Architecture Logicielle

## Gestion d'une plateforme de réservation de salles de coworking

Prérequis :

- Java 17+
- Docker et Docker Compose

1. Démarrer Kafka et Zookeeper :

```bash
docker compose up -d
```

2. Démarrer les services dans cet ordre, chacun dans un terminal séparé :

```bash
cd config-server && sh mvnw spring-boot:run
cd discovery-server && sh mvnw spring-boot:run
cd room-service && sh mvnw spring-boot:run
cd member-service && sh mvnw spring-boot:run
cd reservation-service && sh mvnw spring-boot:run
cd api-gateway && sh mvnw spring-boot:run
```

## URLs utiles

- Gateway : `http://localhost:8080`
- Eureka : `http://localhost:8761`
- Config Server : `http://localhost:8888`

Swagger :

- `http://localhost:8081/swagger-ui.html`
- `http://localhost:8082/swagger-ui.html`
- `http://localhost:8083/swagger-ui.html`

## API via Gateway

Toutes les routes publiques passent par `/api` :

- `/api/rooms`
- `/api/members`
- `/api/reservations`

