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

import cz.ondrejtrisc.logos.model.PriceData;
import cz.ondrejtrisc.logos.repository.PriceDataRepository;
import cz.ondrejtrisc.logos.repository.StockRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class PriceDataController {

    @Autowired
    PriceDataRepository priceDataRepository;

    @Autowired
    StockRepository stockRepository;

    @GetMapping("/pricedata")
    public ResponseEntity<List<PriceData>> getAllPriceData(@RequestParam(required = false) String stockSymbol) {
        try {
            List<PriceData> priceData = new ArrayList<>();

            if (stockSymbol == null) {
                priceData.addAll(priceDataRepository.findAll());
            }
            else {
                priceData.addAll(priceDataRepository.findByStock(stockRepository.findOneBySymbol(stockSymbol)));
            }
            if (priceData.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(priceData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pricedata/{id}")
    public ResponseEntity<PriceData> getPriceDataById(@PathVariable("id") long id) {
        Optional<PriceData> priceData = priceDataRepository.findById(id);

        if (priceData.isPresent()) {
            return new ResponseEntity<>(priceData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/pricedata")
    public ResponseEntity<PriceData> createPriceData(@RequestBody PriceData priceData) {
        try {
            PriceData _priceData = priceDataRepository
                    .save(priceData);
            return new ResponseEntity<>(_priceData, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/pricedata/{id}")
    public ResponseEntity<PriceData> updatePriceData(@PathVariable("id") long id, @RequestBody PriceData priceData) {
        Optional<PriceData> priceDataOld = priceDataRepository.findById(id);

        if (priceDataOld.isPresent()) {
            PriceData _priceData = priceDataOld.get();
            _priceData.setComment(priceData.getComment());
            return new ResponseEntity<>(priceDataRepository.save(_priceData), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/pricedata/{id}")
    public ResponseEntity<HttpStatus> deletePriceData(@PathVariable("id") long id) {
        try {
            priceDataRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/pricedata")
    public ResponseEntity<HttpStatus> deleteAllPriceData() {
        try {
            priceDataRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}