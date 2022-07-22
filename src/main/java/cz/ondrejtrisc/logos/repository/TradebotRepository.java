package cz.ondrejtrisc.logos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ondrejtrisc.logos.model.Tradebot;

public interface TradebotRepository extends JpaRepository<Tradebot, Long> {
    List<Tradebot> findByCodeContaining(String text);
    List<Tradebot> findByCommentContaining(String text);
}