package fr.esgi.encheres_descendantes.domaine.modele;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Participant {

    private final Long id;
    private final String email;
    private final String motDePasseHache;

    public Participant(Long id, String email, String motDePasseHache) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "email");
        this.motDePasseHache = Objects.requireNonNull(motDePasseHache, "motDePasseHache");
    }

    public static Participant nouveau(String email, String motDePasseHache) {
        return new Participant(null, email, motDePasseHache);
    }

    public Participant avecId(Long id) {
        return new Participant(id, email, motDePasseHache);
    }

    public boolean estLeMemeQue(Participant autre) {
        return autre != null && id != null && id.equals(autre.id);
    }
}
