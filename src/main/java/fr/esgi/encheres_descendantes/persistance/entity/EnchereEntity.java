package fr.esgi.encheres_descendantes.persistance.entity;

import fr.esgi.encheres_descendantes.domaine.modele.StatutEnchere;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enchere")
@Getter
@Setter
@NoArgsConstructor
public class EnchereEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    @OneToMany(mappedBy = "enchere", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ArticleEntity> articles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vendeur_id", nullable = false)
    private ParticipantEntity vendeur;

    @Column(name = "date_heure_debut", nullable = false)
    private LocalDateTime dateHeureDebut;

    @Column(name = "date_heure_fin", nullable = false)
    private LocalDateTime dateHeureFin;

    @Column(name = "prix_de_depart", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixDeDepart;

    @Column(name = "prix_plancher", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixPlancher;

    @Column(name = "pas_decrement", nullable = false, precision = 12, scale = 2)
    private BigDecimal pasDecrement;

    @Column(name = "intervalle_decrement_secondes", nullable = false)
    private Long intervalleDecrementSecondes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEnchere statut;

    public void ajouterArticle(ArticleEntity article) {
        article.setEnchere(this);
        this.articles.add(article);
    }
}
