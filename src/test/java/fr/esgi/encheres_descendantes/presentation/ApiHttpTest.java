package fr.esgi.encheres_descendantes.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiHttpTest {

    private static final String ACHETEUR = "acheteur@esgi.fr";
    private static final String VENDEUR = "vendeur@esgi.fr";
    private static final String MOT_DE_PASSE = "motdepasse123";
    private static final String MONTANT_GENEREUX = "99999.00";

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> envoyer(HttpRequest requete) {
        try {
            return client.send(requete, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Appel HTTP impossible", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Appel HTTP interrompu", e);
        }
    }

    private HttpRequest.Builder vers(String chemin) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + chemin));
    }

    private String basic(String utilisateur, String motDePasse) {
        String jeton = utilisateur + ":" + motDePasse;
        return "Basic " + Base64.getEncoder().encodeToString(jeton.getBytes(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> get(String chemin) {
        return envoyer(vers(chemin).GET().build());
    }

    private HttpResponse<String> poste(String chemin, String json) {
        return envoyer(vers(chemin)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build());
    }

    private HttpResponse<String> posteAvec(String utilisateur, String motDePasse, String chemin, String json) {
        return envoyer(vers(chemin)
                .header("Content-Type", "application/json")
                .header("Authorization", basic(utilisateur, motDePasse))
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build());
    }

    private HttpResponse<String> posteAuthentifie(String utilisateur, String chemin, String json) {
        return posteAvec(utilisateur, MOT_DE_PASSE, chemin, json);
    }

    private String champ(String json, String nom) {
        Matcher m = Pattern.compile("\"" + nom + "\"\\s*:\\s*\"?([^,\"}]+)\"?").matcher(json);
        assertTrue(m.find(), "champ " + nom + " absent de : " + json);
        return m.group(1);
    }

    @Test
    @Order(1)
    @DisplayName("les routes de lecture sont publiques")
    void lecturePublique() {
        assertEquals(200, get("/api/encheres").statusCode());
        assertEquals(200, get("/api/encheres/1").statusCode());
    }

    @Test
    @Order(2)
    @DisplayName("la documentation OpenAPI est accessible sans authentification")
    void documentationPublique() {
        HttpResponse<String> reponse = get("/v3/api-docs");

        assertEquals(200, reponse.statusCode());
        assertTrue(reponse.body().contains("/api/encheres/{id}/offres"));
    }

    @Test
    @Order(3)
    @DisplayName("faire une offre sans authentification renvoie 401")
    void offreSansAuthentification() {
        assertEquals(401, poste("/api/encheres/1/offres", "{\"montant\":990.00}").statusCode());
    }

    @Test
    @Order(4)
    @DisplayName("régler sans authentification renvoie 401")
    void reglementSansAuthentification() {
        assertEquals(401, poste("/api/reglements", "{\"offreId\":1,\"montant\":990.00}").statusCode());
    }

    @Test
    @Order(5)
    @DisplayName("un mot de passe faux renvoie 401")
    void mauvaisMotDePasse() {
        assertEquals(401, posteAvec(ACHETEUR, "faux", "/api/encheres/1/offres",
                "{\"montant\":990.00}").statusCode());
    }

    @Test
    @Order(6)
    @DisplayName("l'inscription renvoie 201 et ne divulgue jamais le mot de passe")
    void inscription() {
        HttpResponse<String> reponse = poste("/api/participants",
                "{\"email\":\"nouveau@esgi.fr\",\"motDePasse\":\"motdepasse123\"}");

        assertEquals(201, reponse.statusCode());
        assertEquals("nouveau@esgi.fr", champ(reponse.body(), "email"));
        assertFalse(reponse.body().contains("motDePasse"));
    }

    @Test
    @Order(7)
    @DisplayName("un email déjà pris renvoie 409")
    void emailDejaPris() {
        assertEquals(409, poste("/api/participants",
                "{\"email\":\"" + ACHETEUR + "\",\"motDePasse\":\"motdepasse123\"}").statusCode());
    }

    @Test
    @Order(8)
    @DisplayName("un mot de passe trop court renvoie 400 en nommant le champ fautif")
    void motDePasseTropCourt() {
        HttpResponse<String> reponse = poste("/api/participants",
                "{\"email\":\"court@esgi.fr\",\"motDePasse\":\"abc\"}");

        assertEquals(400, reponse.statusCode());
        assertTrue(reponse.body().contains("motDePasse"));
    }

    @Test
    @Order(9)
    @DisplayName("la connexion renvoie 200, un mot de passe faux renvoie 401")
    void connexion() {
        assertEquals(200, poste("/api/participants/connexion",
                "{\"email\":\"" + ACHETEUR + "\",\"motDePasse\":\"" + MOT_DE_PASSE + "\"}").statusCode());
        assertEquals(401, poste("/api/participants/connexion",
                "{\"email\":\"" + ACHETEUR + "\",\"motDePasse\":\"faux\"}").statusCode());
    }

    @Test
    @Order(10)
    @DisplayName("une enchère inexistante renvoie 404")
    void enchereIntrouvable() {
        assertEquals(404, get("/api/encheres/9999").statusCode());
    }

    @Test
    @Order(11)
    @DisplayName("une offre sous le prix courant renvoie 400")
    void offreTropBasse() {
        assertEquals(400, posteAuthentifie(ACHETEUR, "/api/encheres/1/offres",
                "{\"montant\":1.00}").statusCode());
    }

    @Test
    @Order(12)
    @DisplayName("le vendeur ne peut pas enchérir sur sa propre enchère : 403")
    void vendeurExclu() {
        assertEquals(403, posteAuthentifie(VENDEUR, "/api/encheres/1/offres",
                "{\"montant\":" + MONTANT_GENEREUX + "}").statusCode());
    }

    @Test
    @Order(13)
    @DisplayName("une enchère pas encore ouverte renvoie 409")
    void enchereNonOuverte() {
        assertEquals(409, posteAuthentifie(ACHETEUR, "/api/encheres/2/offres",
                "{\"montant\":" + MONTANT_GENEREUX + "}").statusCode());
    }

    @Test
    @Order(20)
    @DisplayName("parcours complet : offre acceptée, enchère adjugée, règlement, refus des doublons")
    void parcoursComplet() {
        HttpResponse<String> offre = posteAuthentifie(ACHETEUR, "/api/encheres/1/offres",
                "{\"montant\":" + MONTANT_GENEREUX + "}");
        assertEquals(201, offre.statusCode());

        String offreId = champ(offre.body(), "id");
        String montant = champ(offre.body(), "montant");

        assertEquals("ADJUGEE", champ(get("/api/encheres/1").body(), "statut"));

        assertEquals(409, posteAuthentifie(ACHETEUR, "/api/encheres/1/offres",
                "{\"montant\":" + MONTANT_GENEREUX + "}").statusCode());

        assertEquals(403, posteAuthentifie(VENDEUR, "/api/reglements",
                "{\"offreId\":" + offreId + ",\"montant\":" + montant + "}").statusCode());

        assertEquals(400, posteAuthentifie(ACHETEUR, "/api/reglements",
                "{\"offreId\":" + offreId + ",\"montant\":1.00}").statusCode());

        assertEquals(201, posteAuthentifie(ACHETEUR, "/api/reglements",
                "{\"offreId\":" + offreId + ",\"montant\":" + montant + "}").statusCode());

        assertEquals(403, posteAuthentifie(ACHETEUR, "/api/reglements",
                "{\"offreId\":" + offreId + ",\"montant\":" + montant + "}").statusCode());
    }
}
