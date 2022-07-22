package cz.ondrejtrisc.logos.service;

import cz.ondrejtrisc.logos.model.PriceData;
import cz.ondrejtrisc.logos.model.Stock;
import cz.ondrejtrisc.logos.repository.PriceDataRepository;
import cz.ondrejtrisc.logos.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PriceDataService {

    @Autowired
    PriceDataRepository priceDataRepository;

    @Autowired
    StockRepository stockRepository;

    public double findOpenByStockAndDate(String stockString, String dateString) throws ParseException {
        Stock stock = stockRepository.findOneBySymbol(stockString);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dateString);
        PriceData priceData = priceDataRepository.findByStockAndDate(stock, date);
        return priceData.getOpen();
    }
}
