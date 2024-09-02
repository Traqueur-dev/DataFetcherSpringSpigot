package fr.traqueur.datafetcher.api.controllers;

import fr.traqueur.datafetcher.exceptions.PlayerAlreadyExistException;
import fr.traqueur.datafetcher.api.models.PlayerData;
import fr.traqueur.datafetcher.api.services.PlayerService;
import fr.traqueur.datafetcher.dto.PlayerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PlayerDTO> addPlayer(@RequestBody PlayerDTO player) throws PlayerAlreadyExistException {
        this.service.addPlayer(new PlayerData(player));
        return ResponseEntity.ok(player);
    }

    @GetMapping(path = "{player_uuid}")
    public PlayerDTO getPlayer(@PathVariable("player_uuid") UUID uuid) {
        return this.service.getPlayer(uuid).toPlayerDTO();
    }


    @GetMapping
    public List<PlayerDTO> getPlayers() {
        return this.service.getPlayers()
                .stream()
                .map(PlayerData::toPlayerDTO)
                .collect(Collectors.toList());
    }

}
