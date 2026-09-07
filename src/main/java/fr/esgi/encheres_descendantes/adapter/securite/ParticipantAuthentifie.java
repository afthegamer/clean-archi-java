package fr.esgi.encheres_descendantes.adapter.securite;

import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class ParticipantAuthentifie implements UserDetails {

    private final Long id;
    private final String email;
    private final String motDePasseHache;

    public ParticipantAuthentifie(Participant participant) {
        this.id = participant.getId();
        this.email = participant.getEmail();
        this.motDePasseHache = participant.getMotDePasseHache();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_PARTICIPANT"));
    }

    @Override
    public String getPassword() {
        return motDePasseHache;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
