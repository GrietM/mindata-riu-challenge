# Guía de validación manual

Este archivo propone una secuencia simple de pruebas manuales para ejecutar desde Swagger UI o cualquier cliente HTTP.

## Preparación

Después de cada `POST /search`, guardar el `searchId` devuelto para reutilizarlo en las consultas `GET /count`.

Sugerencia:

- guardar el primer `searchId` como `SEARCH_ID_1`
- guardar el segundo `searchId` como `SEARCH_ID_2`
- guardar el tercero como `SEARCH_ID_3`
- y así sucesivamente

## Verificación por logs

Si el entorno fue levantado con `docker compose up --build -d`, los logs de la aplicación pueden seguirse con:

```bash
docker compose logs -f app
```

Durante la prueba debería observarse:

- un log de recepción del mensaje luego de cada `POST /search`
- un log de persistencia del mensaje en MySQL
- un log de recepción de la consulta luego de cada `GET /count`
- un log con el conteo devuelto para el `searchId` consultado

## POST /search

### Caso 1. Búsqueda base

Guardar el `searchId` como `SEARCH_ID_1`.

```json
{
  "hotelId": "4521",
  "checkIn": "2026-12-29",
  "checkOut": "2026-12-31",
  "ages": [7, 2, 7, 1]
}
```

### Caso 2. Repetición exacta del caso 1

Guardar el `searchId` como `SEARCH_ID_2`.

```json
{
  "hotelId": "4521",
  "checkIn": "2026-12-29",
  "checkOut": "2026-12-31",
  "ages": [7, 2, 7, 1]
}
```

### Caso 3. Mismo contenido, distinto orden de `ages`

Guardar el `searchId` como `SEARCH_ID_3`.

```json
{
  "hotelId": "4521",
  "checkIn": "2026-12-29",
  "checkOut": "2026-12-31",
  "ages": [2, 7, 7, 1]
}
```

### Caso 4. Hotel diferente, misma estadía

Guardar el `searchId` como `SEARCH_ID_4`.

```json
{
  "hotelId": "9999",
  "checkIn": "2026-12-29",
  "checkOut": "2026-12-31",
  "ages": [7, 2, 7, 1]
}
```

### Caso 5. Misma base, estadía diferente

Guardar el `searchId` como `SEARCH_ID_5`.

```json
{
  "hotelId": "4521",
  "checkIn": "2027-01-10",
  "checkOut": "2027-01-15",
  "ages": [7, 2]
}
```

### Caso 6. Caso simple

Guardar el `searchId` como `SEARCH_ID_6`.

```json
{
  "hotelId": "4521",
  "checkIn": "2026-12-29",
  "checkOut": "2026-12-31",
  "ages": [1]
}
```

## GET /count

Esperar unos segundos entre los `POST /search` y los `GET /count` si el consumidor Kafka todavía no procesó la persistencia.

### Consulta 1. `GET /count?searchId=SEARCH_ID_1`

Resultado esperado:

- `count = 2`
- debe devolver la misma búsqueda enviada en los casos 1 y 2

### Consulta 2. `GET /count?searchId=SEARCH_ID_2`

Resultado esperado:

- `count = 2`
- debe devolver la misma búsqueda enviada en los casos 1 y 2

### Consulta 3. `GET /count?searchId=SEARCH_ID_3`

Resultado esperado:

- `count = 1`
- no debe contarse junto a los casos 1 y 2 porque el orden de `ages` es distinto

### Consulta 4. `GET /count?searchId=SEARCH_ID_4`

Resultado esperado:

- `count = 1`
- no debe contarse junto a los demás porque cambia `hotelId`

### Consulta 5. `GET /count?searchId=SEARCH_ID_5`

Resultado esperado:

- `count = 1`
- no debe contarse junto a los demás porque cambian `checkIn` y `checkOut`

### Consulta 6. `GET /count?searchId=SEARCH_ID_6`

Resultado esperado:

- `count = 1`
- corresponde a un caso simple con una única edad
