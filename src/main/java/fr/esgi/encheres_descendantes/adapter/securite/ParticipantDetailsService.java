package fr.esgi.encheres_descendantes.adapter.securite;

import fr.esgi.encheres_descendantes.domaine.repository.ParticipantRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParticipantDetailsService implements UserDetailsService {

    private final ParticipantRepository participantRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        String normalise = email == null ? null : email.trim().toLowerCase();
        return participantRepository.trouverParEmail(normalise)
                .map(ParticipantAuthentifie::new)
                .orElseThrow(() -> new UsernameNotFoundException("Identifiants invalides"));
    }
}
