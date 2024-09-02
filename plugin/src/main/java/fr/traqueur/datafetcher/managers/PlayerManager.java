package fr.traqueur.datafetcher.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.traqueur.datafetcher.DataFetcher;
import fr.traqueur.datafetcher.dto.PlayerDTO;
import fr.traqueur.datafetcher.exceptions.PlayerAlreadyExistException;
import org.bukkit.entity.Player;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerManager {

    private final WebClient restAPI;
    private final DataFetcher plugin;
    private final Gson gson;


    public PlayerManager(WebClient restAPI, DataFetcher plugin) {
        this.restAPI = restAPI;
        this.plugin = plugin;
        this.gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    }

    public PlayerDTO toDTO(Player player) {
        return new PlayerDTO(player.getUniqueId(),
                player.getName(), player.isOp(),
                player.isBanned(),
                player.getLevel(),
                player.getExp(),
                player.getHealth());
    }

    public boolean create(Player player) {
        PlayerDTO playerDTO = toDTO(player);
        AtomicBoolean created = new AtomicBoolean(false);
        this.restAPI.post()
                .uri("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(gson.toJson(playerDTO))
                .retrieve()
                .toEntity(PlayerDTO.class)
                .onErrorResume(Mono::error)
                .subscribe(
                        entityPlayer -> {
                            plugin.getLogger().info("Player " + entityPlayer.getBody().name() + " has been created.");
                            created.set(true);
                        },
                        error -> {
                            if (error instanceof PlayerAlreadyExistException) {
                                created.set(false);
                            } else {
                                plugin.getLogger().severe("An error occurred while creating player " + player.getName() + " : " + error.getMessage());
                            }
                        }
                );
        return created.get();
    }

    public void save(Player player) {
        PlayerDTO playerDTO = toDTO(player);
        this.restAPI.put()
                .uri("/players/{player_uuid}", player.getUniqueId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(gson.toJson(playerDTO))
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(Mono::error)
                .subscribe();
    }

    public void delete(Player player) {
        this.restAPI.delete()
                .uri("/players/{player_uuid}", player.getUniqueId())
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(Mono::error)
                .subscribe();
    }

    public void saveAll(Collection<? extends Player> onlinePlayers) {
        onlinePlayers.forEach(this::save);
    }
}
