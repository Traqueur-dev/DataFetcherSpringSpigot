package fr.traqueur.datafetcher.api.services;

import fr.traqueur.datafetcher.exceptions.PlayerAlreadyExistException;
import fr.traqueur.datafetcher.api.models.PlayerData;
import fr.traqueur.datafetcher.api.repositories.PlayerRepository;
import fr.traqueur.datafetcher.exceptions.PlayerNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository repository;

    @Autowired
    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public List<PlayerData> getPlayers() {
        return this.repository.findAll();
    }

    public void addPlayer(PlayerData playerData) throws PlayerAlreadyExistException {
        if (this.repository.findById(playerData.getUuid()).isPresent()) {
            throw new PlayerAlreadyExistException("Player already exists.");
        }
        this.repository.save(playerData);
    }

    public void deletePlayer(UUID uuid) {
        if (!this.repository.existsById(uuid)) {
            throw new IllegalStateException("Player with "+ uuid + " not found.");
        }
        this.repository.deleteById(uuid);
    }

    public void updatePlayer(UUID uuid, PlayerData playerData) {
        repository.findById(uuid)
                .map(player -> {
                    player.setBanned(playerData.isBanned());
                    player.setOperator(playerData.isOperator());
                    player.setLevel(playerData.getLevel());
                    player.setXp(playerData.getXp());
                    player.setHealth(playerData.getHealth());
                    return repository.save(player);
                })
                .orElseGet(() -> repository.save(playerData));

    }

    public PlayerData getPlayer(UUID uuid) throws PlayerNotExistsException {
        return this.repository.findById(uuid)
                .orElseThrow(() -> new PlayerNotExistsException("Player with "+ uuid + " not found."));
    }
}
