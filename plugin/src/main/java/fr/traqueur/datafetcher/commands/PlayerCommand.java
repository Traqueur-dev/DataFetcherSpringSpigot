package fr.traqueur.datafetcher.commands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.datafetcher.DataFetcher;
import fr.traqueur.datafetcher.dto.PlayerDTO;
import fr.traqueur.datafetcher.managers.PlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class PlayerCommand extends Command<DataFetcher> {

    private final PlayerManager playerManager;

    public PlayerCommand(DataFetcher plugin) {
        super(plugin, "player");
        this.playerManager = plugin.getPlayerManager();
        this.setDescription("Retourne des informations sur un joueur.");
        this.setPermission("datafetcher.command.player");
        this.setUsage("/player <player>");
        this.addAlias("p");
        this.addArgs("player:offlineplayer");
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        OfflinePlayer player = arguments.get("player");
        playerManager.get(player.getUniqueId(), playerDTO -> {
            commandSender.sendMessage("§7Informations sur le joueur §e" + player.getName() + "§7:");
            commandSender.sendMessage("§7UUID: §e" + playerDTO.uuid());
            commandSender.sendMessage("§7Nom: §e" + playerDTO.name());
            commandSender.sendMessage("§7Opérateur: §e" + playerDTO.operator());
            commandSender.sendMessage("§7Banni: §e" + playerDTO.banned());
            commandSender.sendMessage("§7Niveau: §e" + playerDTO.level());
            commandSender.sendMessage("§7Expérience: §e" + playerDTO.xp());
            commandSender.sendMessage("§7Vie: §e" + playerDTO.health());
        }, error -> {
            commandSender.sendMessage("§cErreur: Joueur " + player.getName() + " introuvable.");
        });
    }
}
