package cz.ondrejtrisc.logos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.ondrejtrisc.logos.model.Stock;
import cz.ondrejtrisc.logos.repository.StockRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class StockController {

    @Autowired
    StockRepository stockRepository;

    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks(@RequestParam(required = false) String symbol) {
        try {
            List<Stock> stocks = new ArrayList<>();

            if (symbol == null) {
                stocks.addAll(stockRepository.findAll());
            }
            else {
                stocks.add(stockRepository.findOneBySymbol(symbol));
            }
            if (stocks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(stocks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stocks/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable("id") long id) {
        Optional<Stock> stockData = stockRepository.findById(id);

        if (stockData.isPresent()) {
            return new ResponseEntity<>(stockData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/stocks")
    public ResponseEntity<Stock> createStock(@RequestBody String symbol) {
        try {
            Stock _stock = stockRepository
                    .save(new Stock(symbol));
            return new ResponseEntity<>(_stock, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/stocks/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable("id") long id, @RequestBody Stock stock) {
        Optional<Stock> stockData = stockRepository.findById(id);

        if (stockData.isPresent()) {
            Stock _stock = stockData.get();
            _stock.setComment(stock.getComment());
            return new ResponseEntity<>(stockRepository.save(_stock), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/stocks/{id}")
    public ResponseEntity<HttpStatus> deleteStock(@PathVariable("id") long id) {
        try {
            stockRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/stocks")
    public ResponseEntity<HttpStatus> deleteAllStocks() {
        try {
            stockRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}