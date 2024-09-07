package fr.traqueur.datafetcher.api.players;

import fr.traqueur.datafetcher.dto.PlayerDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table
public class PlayerData {

    @Id
    private UUID uuid;
    private String name;
    private boolean operator;
    private boolean banned;
    private int level;
    private float xp;
    private double health;


    public PlayerData(UUID uuid, String name, boolean operator, boolean banned, int level, float xp, double health) {
        this.uuid = uuid;
        this.name = name;
        this.operator = operator;
        this.banned = banned;
        this.level = level;
        this.xp = xp;
        this.health = health;
    }

    public PlayerData(PlayerDTO playerDTO) {
        this(playerDTO.uuid(), playerDTO.name(), playerDTO.operator(), playerDTO.banned(), playerDTO.level(), playerDTO.xp(), playerDTO.health());
    }

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.operator = false;
        this.banned = false;
        this.level = 0;
        this.xp = 0;
        this.health = 20.0;
    }

    public PlayerData() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isOperator() {
        return operator;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public PlayerDTO toPlayerDTO() {
        return new PlayerDTO(uuid, name, operator, banned, level, xp, health);
    }
}
