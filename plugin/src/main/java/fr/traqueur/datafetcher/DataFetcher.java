package fr.traqueur.datafetcher;

import com.google.common.net.HttpHeaders;
import fr.traqueur.datafetcher.exceptions.PlayerAlreadyExistException;
import fr.traqueur.datafetcher.listeners.PlayerListener;
import fr.traqueur.datafetcher.managers.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

public final class DataFetcher extends JavaPlugin {

    private WebClient webClient;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        this.webClient = this.buildWebClient();
        this.playerManager = new PlayerManager(this.webClient, this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this.playerManager), this);
    }

    @Override
    public void onDisable() {
        this.playerManager.saveAll(this.getServer().getOnlinePlayers());
    }

    private WebClient buildWebClient() {
        String baseUrl = "http://localhost:8080/api/v1";
        return WebClient.builder()
                .filter(this.errorHandler())
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", baseUrl))
                .build();
    }

    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isSameCodeAs(HttpStatus.CONFLICT)) {
                return clientResponse.bodyToMono(Error.class)
                        .flatMap(error -> Mono.error(new PlayerAlreadyExistException(error.getMessage())));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
