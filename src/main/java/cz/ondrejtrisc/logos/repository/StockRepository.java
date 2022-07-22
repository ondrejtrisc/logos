package cz.ondrejtrisc.logos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ondrejtrisc.logos.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findOneBySymbol(String symbol);
}