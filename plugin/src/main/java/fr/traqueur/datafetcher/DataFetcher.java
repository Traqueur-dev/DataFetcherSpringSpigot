package fr.traqueur.datafetcher;

import com.google.common.net.HttpHeaders;
import fr.traqueur.commands.api.CommandManager;
import fr.traqueur.datafetcher.commands.PlayerCommand;
import fr.traqueur.datafetcher.exceptions.PlayerAlreadyExistException;
import fr.traqueur.datafetcher.exceptions.PlayerNotExistsException;
import fr.traqueur.datafetcher.listeners.PlayerListener;
import fr.traqueur.datafetcher.managers.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

public final class DataFetcher extends JavaPlugin {

    private WebClient webClient;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        CommandManager commandsManager = new CommandManager(this);
        commandsManager.registerCommand(new PlayerCommand(this));
        buildWebClient().thenAccept(webClient -> {
            this.webClient = webClient;
            this.playerManager = new PlayerManager(this.webClient, this);
            this.getServer().getPluginManager().registerEvents(new PlayerListener(this.playerManager), this);

            this.getLogger().info("DataFetcher has been enabled.");
        });
    }

    @Override
    public void onDisable() {
        //Little trick to save all players in a new thread to make blocking call
        //blocking call is necessary to prevent disable before all data are saved
        Thread thread = new Thread(() -> this.playerManager.saveAll(this.getServer().getOnlinePlayers()));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.getLogger().info("DataFetcher has been disabled.");

    }

    private CompletableFuture<WebClient> buildWebClient() {
        String baseUrl = "http://localhost:8080/api/v1";
        return getToken(baseUrl).thenApply(token -> WebClient.builder()
                .filter(this.errorHandler())
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(token))
                .defaultUriVariables(Collections.singletonMap("url", baseUrl))
                .build());
    }

    private CompletableFuture<String> getToken(String baseUrl) {
        WebClient userClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth("traqueur-dev", "password"))
                .defaultUriVariables(Collections.singletonMap("url", baseUrl))
                .build();
        return userClient.post()
                .uri("/auth/token")
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();
    }

    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse ->
            switch (clientResponse.statusCode()) {
                case HttpStatus.CONFLICT -> clientResponse.bodyToMono(Error.class)
                    .flatMap(error -> Mono.error(new PlayerAlreadyExistException(error.getMessage())));
                case HttpStatus.NOT_FOUND -> clientResponse.bodyToMono(Error.class)
                    .flatMap(error -> Mono.error(new PlayerNotExistsException(error.getMessage())));
                default -> Mono.just(clientResponse);
        });
    }


    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
