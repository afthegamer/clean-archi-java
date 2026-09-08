package fr.esgi.encheres_descendantes.persistance.mapper;

import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.modele.StatutEnchere;
import fr.esgi.encheres_descendantes.persistance.entity.ArticleEntity;
import fr.esgi.encheres_descendantes.persistance.entity.EnchereEntity;
import fr.esgi.encheres_descendantes.persistance.entity.ParticipantEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnchereMapperTest {

    private static final LocalDateTime DEBUT = LocalDateTime.of(2026, 3, 1, 10, 0);

    private EnchereEntity entite;

    @BeforeEach
    void avantChaqueTest() {
        ParticipantEntity vendeur = new ParticipantEntity("vendeur@esgi.fr", "hache");
        vendeur.setId(1L);

        ArticleEntity article = new ArticleEntity("Montre mécanique", "acier");
        article.setId(7L);

        entite = new EnchereEntity();
        entite.setId(10L);
        entite.setNom("Vente flash");
        entite.setDescription("Une montre");
        entite.setVendeur(vendeur);
        entite.ajouterArticle(article);
        entite.setDateHeureDebut(DEBUT);
        entite.setDateHeureFin(DEBUT.plusHours(2));
        entite.setPrixDeDepart(new BigDecimal("1000.00"));
        entite.setPrixPlancher(new BigDecimal("200.00"));
        entite.setPasDecrement(new BigDecimal("10.00"));
        entite.setIntervalleDecrementSecondes(90L);
        entite.setStatut(StatutEnchere.OUVERTE);
    }

    @Test
    @DisplayName("tous les champs de l'entité arrivent dans le modèle")
    void versDomaineComplet() {
        Enchere enchere = EnchereMapper.versDomaine(entite);

        assertEquals(10L, enchere.getId());
        assertEquals("Vente flash", enchere.getNom());
        assertEquals("Une montre", enchere.getDescription());
        assertEquals(DEBUT, enchere.getDateHeureDebut());
        assertEquals(DEBUT.plusHours(2), enchere.getDateHeureFin());
        assertEquals(0, new BigDecimal("1000.00").compareTo(enchere.getPrixDeDepart()));
        assertEquals(0, new BigDecimal("200.00").compareTo(enchere.getPrixPlancher()));
        assertEquals(0, new BigDecimal("10.00").compareTo(enchere.getPasDecrement()));
        assertEquals(StatutEnchere.OUVERTE, enchere.getStatut());
    }

    @Test
    @DisplayName("les secondes stockées redeviennent une Duration")
    void dureeReconstruite() {
        assertEquals(Duration.ofSeconds(90), EnchereMapper.versDomaine(entite).getIntervalleDecrement());
    }

    @Test
    @DisplayName("le vendeur et les articles sont traduits en objets du domaine")
    void relationsTraduites() {
        Enchere enchere = EnchereMapper.versDomaine(entite);

        assertEquals(1L, enchere.getVendeur().getId());
        assertEquals("vendeur@esgi.fr", enchere.getVendeur().getEmail());
        assertEquals(1, enchere.getArticles().size());
        assertEquals("Montre mécanique", enchere.getArticles().get(0).getNom());
        assertEquals(7L, enchere.getArticles().get(0).getId());
    }

    @Test
    @DisplayName("un aller-retour ne perd aucune donnée")
    void allerRetourStable() {
        Enchere avant = EnchereMapper.versDomaine(entite);

        EnchereEntity cible = new EnchereEntity();
        EnchereMapper.reporterSur(cible, avant);
        cible.setId(entite.getId());
        cible.setVendeur(entite.getVendeur());
        cible.setArticles(entite.getArticles());

        Enchere apres = EnchereMapper.versDomaine(cible);

        assertEquals(avant.getNom(), apres.getNom());
        assertEquals(avant.getDateHeureDebut(), apres.getDateHeureDebut());
        assertEquals(avant.getDateHeureFin(), apres.getDateHeureFin());
        assertEquals(0, avant.getPrixDeDepart().compareTo(apres.getPrixDeDepart()));
        assertEquals(0, avant.getPrixPlancher().compareTo(apres.getPrixPlancher()));
        assertEquals(0, avant.getPasDecrement().compareTo(apres.getPasDecrement()));
        assertEquals(avant.getIntervalleDecrement(), apres.getIntervalleDecrement());
        assertEquals(avant.getStatut(), apres.getStatut());
    }

    @Test
    @DisplayName("reporterSur écrase le statut, c'est ce qui persiste l'adjudication")
    void reporterSurPersisteLAdjudication() {
        Enchere enchere = EnchereMapper.versDomaine(entite);
        enchere.adjuger();

        EnchereMapper.reporterSur(entite, enchere);

        assertEquals(StatutEnchere.ADJUGEE, entite.getStatut());
    }

    @Test
    @DisplayName("une entité nulle donne un modèle nul")
    void entiteNulle() {
        assertNull(EnchereMapper.versDomaine((EnchereEntity) null));
    }
}
