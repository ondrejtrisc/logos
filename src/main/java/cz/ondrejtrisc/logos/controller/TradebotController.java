package cz.ondrejtrisc.logos.controller;

import java.util.*;
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

import cz.ondrejtrisc.logos.model.Tradebot;
import cz.ondrejtrisc.logos.service.PriceDataService;
import cz.ondrejtrisc.logos.repository.TradebotRepository;
import cz.ondrejtrisc.logos.core.ElementaryFunction;
import cz.ondrejtrisc.logos.core.Function;
import cz.ondrejtrisc.logos.core.ExternalInput.TradebotInput;
import cz.ondrejtrisc.logos.core.Output.TradebotOutput;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TradebotController {

    @Autowired
    TradebotRepository tradebotRepository;

    @Autowired
    PriceDataService priceDataService;

    @GetMapping("/tradebots")
    public ResponseEntity<List<Tradebot>> getAllTradebots(@RequestParam(required = false) String text) {
        try {
            List<Tradebot> tradebots = new ArrayList<>();

            if (text == null) {
                tradebots.addAll(tradebotRepository.findAll());
            }
            else {
                tradebots.addAll(tradebotRepository.findByCodeContaining(text));
                tradebots.addAll(tradebotRepository.findByCommentContaining(text));
            }
            if (tradebots.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tradebots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tradebots/{id}")
    public ResponseEntity<Tradebot> getTradebotById(@PathVariable("id") long id) {
        Optional<Tradebot> tradebotData = tradebotRepository.findById(id);

        if (tradebotData.isPresent()) {
            return new ResponseEntity<>(tradebotData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tradebots")
    public ResponseEntity<Tradebot> createTradebot(@RequestBody Tradebot tradebot) {
        try {
            Date submitted = new Date();
            ElementaryFunction.setPriceDataService(priceDataService);
            HashMap<String, Double> portfolio = new HashMap<>();
            ElementaryFunction.setPortfolio(portfolio);
            TradebotOutput tradebotOutput = new TradebotOutput(priceDataService, portfolio);
            TradebotInput tradebotInput = new TradebotInput();
            Function tradebotFunction = Function.parseFromString(tradebot.getCode());
            tradebotFunction.evaluateAndOutputValue(tradebotInput, tradebotOutput);
            if (tradebotOutput.isFailed()) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Date delivered = new Date();
            Tradebot _tradebot = tradebotRepository
                    .save(new Tradebot(tradebot.getCode(), tradebotOutput.getBalance(), submitted, delivered));
            return new ResponseEntity<>(_tradebot, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/tradebots/{id}")
    public ResponseEntity<Tradebot> updateTradebot(@PathVariable("id") long id, @RequestBody Tradebot tradebot) {
        Optional<Tradebot> tradebotData = tradebotRepository.findById(id);

        if (tradebotData.isPresent()) {
            Tradebot _tradebot = tradebotData.get();
            _tradebot.setComment(tradebot.getComment());
            return new ResponseEntity<>(tradebotRepository.save(_tradebot), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tradebots/{id}")
    public ResponseEntity<HttpStatus> deleteTradebot(@PathVariable("id") long id) {
        try {
            tradebotRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tradebots")
    public ResponseEntity<HttpStatus> deleteAllTradebots() {
        try {
            tradebotRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}