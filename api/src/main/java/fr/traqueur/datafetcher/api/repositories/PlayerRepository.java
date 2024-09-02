package fr.traqueur.datafetcher.api.repositories;

import fr.traqueur.datafetcher.api.models.PlayerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerData, UUID> {
}
