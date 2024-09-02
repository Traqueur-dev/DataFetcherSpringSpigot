package fr.traqueur.datafetcher.api.controllers;

import fr.traqueur.datafetcher.api.models.PlayerData;
import fr.traqueur.datafetcher.api.services.PlayerService;
import fr.traqueur.datafetcher.dto.PlayerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/players")
public class PlayerController {

    private final PlayerService service;

    @Autowired
    public PlayerController(PlayerService service) {
        this.service = service;
    }


    @PutMapping(path = "{player_uuid}")
    public void updatePlayer(@PathVariable("player_uuid") UUID uuid,
                             @RequestBody PlayerDTO player) {
        this.service.updatePlayer(uuid, new PlayerData(player));
    }

    @DeleteMapping(path = "{player_uuid}")
    public void deletePlayer(@PathVariable("player_uuid") UUID uuid) {
        this.service.deletePlayer(uuid);
    }

    @PostMapping
    public void addPlayer(@RequestBody PlayerDTO player) {
        this.service.addPlayer(new PlayerData(player));
    }

    @GetMapping
    public List<PlayerDTO> getPlayers() {
        return this.service.getPlayers()
                .stream()
                .map(PlayerData::toPlayerDTO)
                .collect(Collectors.toList());
    }

}
