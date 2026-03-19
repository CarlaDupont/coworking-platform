# Design Pattern choisi dans `reservation-service`

Le microservice `reservation-service` utilise un **State Pattern** pour gérer le cycle de vie d'une réservation.

## Pourquoi ce choix

Le besoin principal de ce service est de contrôler les transitions entre les statuts :

- `CONFIRMED`
- `CANCELLED`
- `COMPLETED`

Une réservation confirmée peut devenir annulée ou terminée, mais une réservation déjà annulée ou terminée ne doit plus évoluer. Ce comportement dépend directement de l'état courant de l'objet, ce qui correspond précisément au State Pattern.

## Implémentation

Les classes suivantes portent ce pattern :

- `ReservationStateHandler`
- `ConfirmedReservationStateHandler`
- `CancelledReservationStateHandler`
- `CompletedReservationStateHandler`
- `ReservationStateManager`

`ReservationStateManager` délègue la validation de la transition au gestionnaire correspondant à l'état courant. Ainsi, la logique métier des transitions est centralisée et évite les enchaînements de `if/else` dispersés dans le service.

## Bénéfices

- Les règles de transition sont explicites.
- Le service de réservation reste plus lisible.
- Ajouter un nouveau statut ou une nouvelle transition devient plus simple.
- Les effets de bord métier restent regroupés autour des changements d'état.
