package fr.traqueur.datafetcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record PlayerDTO(@JsonProperty("uuid") UUID uuid,
                        @JsonProperty("name") String name,
                        @JsonProperty("operator") Boolean operator,
                        @JsonProperty("banned") Boolean banned,
                        @JsonProperty("level") Integer level,
                        @JsonProperty("xp") Float xp,
                        @JsonProperty("health") Double health) {
}
