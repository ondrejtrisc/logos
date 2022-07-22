package cz.ondrejtrisc.logos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ondrejtrisc.logos.model.Stock;
import cz.ondrejtrisc.logos.model.PriceData;

import java.util.Date;
import java.util.List;

public interface PriceDataRepository extends JpaRepository<PriceData, Long> {
    List<PriceData> findByStock(Stock stock);
    PriceData findByStockAndDate(Stock stock, Date date);
}