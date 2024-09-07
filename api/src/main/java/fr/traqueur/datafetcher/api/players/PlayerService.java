package fr.traqueur.datafetcher.api.players;

import fr.traqueur.datafetcher.exceptions.PlayerAlreadyExistException;
import fr.traqueur.datafetcher.exceptions.PlayerNotExistsException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository repository;
    private final Counter putPlayerCounter;
    private final Counter deletePlayerCounter;
    private final Counter getPlayerCounter;
    private final Counter postPlayerCounter;

    @Autowired
    public PlayerService(PlayerRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.putPlayerCounter = meterRegistry.counter("player.put");
        this.deletePlayerCounter = meterRegistry.counter("player.delete");
        this.getPlayerCounter = meterRegistry.counter("player.get");
        this.postPlayerCounter = meterRegistry.counter("player.post");
    }

    public List<PlayerData> getPlayers() {
        return this.repository.findAll();
    }

    public void addPlayer(PlayerData playerData) throws PlayerAlreadyExistException {
        if (this.repository.findById(playerData.getUuid()).isPresent()) {
            throw new PlayerAlreadyExistException("Player already exists.");
        }
        this.postPlayerCounter.increment();
        this.repository.save(playerData);
    }

    public void deletePlayer(UUID uuid) {
        if (!this.repository.existsById(uuid)) {
            throw new IllegalStateException("Player with " + uuid + " not found.");
        }
        this.deletePlayerCounter.increment();
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
        this.putPlayerCounter.increment();
    }

    public PlayerData getPlayer(UUID uuid) throws PlayerNotExistsException {
        this.getPlayerCounter.increment();
        return this.repository.findById(uuid)
                .orElseThrow(() -> new PlayerNotExistsException("Player with " + uuid + " not found."));
    }
}
