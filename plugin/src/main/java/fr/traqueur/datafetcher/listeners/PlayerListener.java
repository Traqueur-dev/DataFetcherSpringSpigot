package fr.traqueur.datafetcher.listeners;

import fr.traqueur.datafetcher.managers.PlayerManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final PlayerManager playerManager;

    public PlayerListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean created = this.playerManager.create(player);
        if (created) {
            player.sendMessage(MINI_MESSAGE.deserialize("<rainbow>Bienvenue sur le serveur !"));
        } else {
            player.sendMessage(MINI_MESSAGE.deserialize("<rainbow>Re-bienvenue sur le serveur !"));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.playerManager.save(player);
    }

}
