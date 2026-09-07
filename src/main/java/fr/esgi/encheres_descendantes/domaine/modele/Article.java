package fr.esgi.encheres_descendantes.domaine.modele;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Article {

    private final Long id;
    private final String nom;
    private final String description;

    public Article(Long id, String nom, String description) {
        this.id = id;
        this.nom = Objects.requireNonNull(nom, "nom");
        this.description = description;
    }

    public static Article nouveau(String nom, String description) {
        return new Article(null, nom, description);
    }
}
