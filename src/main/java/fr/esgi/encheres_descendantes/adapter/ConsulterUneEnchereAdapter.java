package fr.esgi.encheres_descendantes.adapter;

import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.repository.EnchereRepository;
import fr.esgi.encheres_descendantes.domaine.usecase.ConsulterUneEnchereUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ConsulterUneEnchereAdapter implements ConsulterUneEnchereUseCase.OutputPort {

    private final EnchereRepository enchereRepository;

    @Override
    public Optional<Enchere> trouverParId(Long enchereId) {
        return enchereRepository.trouverParId(enchereId);
    }

    @Override
    public List<Enchere> lister() {
        return enchereRepository.lister();
    }
}
