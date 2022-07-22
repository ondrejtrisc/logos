package cz.ondrejtrisc.logos.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import cz.ondrejtrisc.logos.core.ElementaryFunction;
import cz.ondrejtrisc.logos.core.Node;
import cz.ondrejtrisc.logos.service.PriceDataService;
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

import cz.ondrejtrisc.logos.model.Evaluation;
import cz.ondrejtrisc.logos.repository.EvaluationRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class EvaluationController {

    @Autowired
    EvaluationRepository evaluationRepository;

    @Autowired
    PriceDataService priceDataService;

    @GetMapping("/evaluations")
    public ResponseEntity<List<Evaluation>> getAllEvaluations(@RequestParam(required = false) String text) {
        try {
            List<Evaluation> evaluations = new ArrayList<>();

            if (text == null) {
                evaluations.addAll(evaluationRepository.findAll());
            }
            else {
                evaluations.addAll(evaluationRepository.findByQueryContaining(text));
                evaluations.addAll(evaluationRepository.findByCommentContaining(text));
            }
            if (evaluations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(evaluations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/evaluations/{id}")
    public ResponseEntity<Evaluation> getEvaluationById(@PathVariable("id") long id) {
        Optional<Evaluation> evaluationData = evaluationRepository.findById(id);

        if (evaluationData.isPresent()) {
            return new ResponseEntity<>(evaluationData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/evaluations")
    public ResponseEntity<Evaluation> createEvaluation(@RequestBody Evaluation evaluation) {
        try {
            Date submitted = new Date();

            ElementaryFunction.setPriceDataService(priceDataService);

            ArrayList<String> evaluationList = Node.evaluateAndGetEvaluation(evaluation.getQuery());

            Date delivered = new Date();
            Evaluation _evaluation = evaluationRepository
                    .save(new Evaluation(evaluation.getQuery(), evaluationList, submitted, delivered));
            return new ResponseEntity<>(_evaluation, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/evaluations/{id}")
    public ResponseEntity<Evaluation> updateEvaluation(@PathVariable("id") long id, @RequestBody Evaluation evaluation) {
        Optional<Evaluation> evaluationData = evaluationRepository.findById(id);

        if (evaluationData.isPresent()) {
            Evaluation _evaluation = evaluationData.get();
            _evaluation.setComment(evaluation.getComment());
            return new ResponseEntity<>(evaluationRepository.save(_evaluation), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/evaluations/{id}")
    public ResponseEntity<HttpStatus> deleteEvaluation(@PathVariable("id") long id) {
        try {
            evaluationRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/evaluations")
    public ResponseEntity<HttpStatus> deleteAllEvaluations() {
        try {
            evaluationRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}