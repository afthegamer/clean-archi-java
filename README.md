# Enchères descendantes

Spring Boot 4.1.1, JDK 25, H2 en mémoire.

## Règle métier

Le prix part haut et décroît par paliers réguliers. La première offre valide emporte l'article
et clôt la vente.

```
prix(t) = max( prixDeDepart − pasDecrement × paliersEcoulés , prixPlancher )
paliersEcoulés = floor( (t − dateHeureDebut) / intervalleDecrement )
```

Une offre est recevable si :

- l'enchère est `OUVERTE` ;
- `dateHeureDebut` ≤ instant ≤ `dateHeureFin` ;
- l'enchérisseur n'est pas le vendeur ;
- le montant est supérieur ou égal au prix courant.

## Structure

```
domaine/
  modele/         Participant, Article, Enchere, Offre, Reglement, StatutEnchere
  commande/       SInscrireCommande, SeConnecterCommande, FaireUneOffreCommande,
                  ReglerLArticleEmporteCommande
  exception/      DomaineException et ses sept sous-classes
  repository/     ParticipantRepository, EnchereRepository, OffreRepository,
                  ReglementRepository
  service/        MotDePasseEncodeur
  usecase/        SInscrire, SeConnecter, FaireUneOffre, ReglerLArticleEmporte,
                  ConsulterUneEnchere

adapter/
  repository/     implémentations des repositories du domaine
  securite/       BCryptMotDePasseEncodeur
                  SInscrireAdapter, SeConnecterAdapter, FaireUneOffreAdapter,
                  ReglerLArticleEmporteAdapter, ConsulterUneEnchereAdapter

persistance/
  entity/         entités JPA
  repository/     repositories Spring Data
  mapper/         conversion domaine <-> entité

presentation/
  controller/     ParticipantController, EnchereController, ReglementController
  request/        records entrants, validés par Jakarta Validation
  response/       records sortants
                  GestionnaireExceptions

configuration/
  DomaineConfiguration    déclare les use cases en @Bean et l'horloge
  JeuDeDonneesInitial     jeu de données de démarrage
```

`domaine/` ne contient aucun import `org.springframework` ni `jakarta.persistence`.

## Lancer

```bash
./mvnw spring-boot:run
```

Comptes créés au démarrage, mot de passe `motdepasse123` :

| Email               | id |
|---------------------|----|
| `vendeur@esgi.fr`   | 1  |
| `acheteur@esgi.fr`  | 2  |

Deux enchères sont créées : l'enchère 1 est ouverte (1000 € au départ, −10 € toutes les 30 s,
plancher 200 €), l'enchère 2 ouvre dans une heure.

Console H2 : `http://localhost:8080/h2-console` — JDBC URL `jdbc:h2:mem:encheres`, user `sa`,
sans mot de passe.

Désactiver le jeu de données : `application.jeu-de-donnees.actif=false`.

## Sécurité

Spring Security en HTTP Basic, sans session (`STATELESS`). Les comptes viennent de la table
`participant`, mots de passe hachés BCrypt.

L'identité de l'appelant est prise sur le **principal authentifié**, jamais dans le corps de la
requête : un participant ne peut ni enchérir ni régler à la place d'un autre.

## API

| Méthode | Route                          | Accès  | Rôle                                        |
|---------|--------------------------------|--------|---------------------------------------------|
| `POST`  | `/api/participants`            | public | S'inscrire                                  |
| `POST`  | `/api/participants/connexion`  | public | Se connecter                                |
| `GET`   | `/api/encheres`                | public | Lister les enchères avec leur prix courant  |
| `GET`   | `/api/encheres/{id}`           | public | Consulter une enchère et son prix courant   |
| `POST`  | `/api/encheres/{id}/offres`    | **authentifié** | Faire une offre                    |
| `POST`  | `/api/reglements`              | **authentifié** | Régler l'article emporté           |

Également publics : `/swagger-ui.html`, `/v3/api-docs`, `/h2-console`.

```bash
curl http://localhost:8080/api/encheres/1
```

```bash
curl -u acheteur@esgi.fr:motdepasse123 -X POST http://localhost:8080/api/encheres/1/offres -H "Content-Type: application/json" -d '{"montant": 990.00}'
```

```bash
curl -u acheteur@esgi.fr:motdepasse123 -X POST http://localhost:8080/api/reglements -H "Content-Type: application/json" -d '{"offreId": 1, "montant": 990.00}'
```

## Documentation

Swagger UI : `http://localhost:8080/swagger-ui.html`

OpenAPI JSON : `http://localhost:8080/v3/api-docs`

## Codes d'erreur

| Exception                        | HTTP |
|----------------------------------|------|
| `DonneesInvalidesException`      | 400  |
| `MontantInsuffisantException`    | 400  |
| `IdentifiantsInvalidesException` | 401  |
| `OperationInterditeException`    | 403  |
| `RessourceIntrouvableException`  | 404  |
| `EmailDejaUtiliseException`      | 409  |
| `EnchereFermeeException`         | 409  |

## Tests

```bash
./mvnw test
```

50 tests. Ceux du domaine tournent sans Spring, sans base et sans framework de mock.

## Limites

- Pas d'authentification : `participantId` est passé dans le corps des requêtes.
- Pas de création d'enchère par l'API.
- Pas de clôture automatique à `dateHeureFin` ; le statut reste `OUVERTE`.
- Pas de verrou sur l'adjudication : deux offres simultanées peuvent toutes deux passer.
  Il faudrait un `@Version` sur `EnchereEntity`.
