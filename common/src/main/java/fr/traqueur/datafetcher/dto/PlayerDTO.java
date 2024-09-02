package fr.traqueur.datafetcher.dto;

import java.util.UUID;

public record PlayerDTO(UUID uuid,
                        String name,
                        boolean operator,
                        boolean banned,
                        int level,
                        int xp,
                        double health) {
}
