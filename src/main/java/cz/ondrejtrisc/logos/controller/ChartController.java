package cz.ondrejtrisc.logos.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import cz.ondrejtrisc.logos.core.ElementaryFunction;
import cz.ondrejtrisc.logos.core.Function;
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

import cz.ondrejtrisc.logos.model.Chart;
import cz.ondrejtrisc.logos.repository.ChartRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class ChartController {

    @Autowired
    ChartRepository chartRepository;

    @Autowired
    PriceDataService priceDataService;

    @GetMapping("/charts")
    public ResponseEntity<List<Chart>> getAllCharts(@RequestParam(required = false) String text) {
        try {
            List<Chart> charts = new ArrayList<>();

            if (text == null) {
                charts.addAll(chartRepository.findAll());
            }
            else {
                charts.addAll(chartRepository.findByFunctionCodeContaining(text));
                charts.addAll(chartRepository.findByCommentContaining(text));
            }
            if (charts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(charts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/charts/{id}")
    public ResponseEntity<Chart> getChartById(@PathVariable("id") long id) {
        Optional<Chart> chartData = chartRepository.findById(id);

        if (chartData.isPresent()) {
            return new ResponseEntity<>(chartData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/charts")
    public ResponseEntity<Chart> createChart(@RequestBody Chart chart) {
        try {
            Date submitted = new Date();

            ElementaryFunction.setPriceDataService(priceDataService);

            Node node = Node.parseFromString(chart.getFunctionCode());
            Function function = node.evaluate();
            ArrayList<Double> graph = new ArrayList<>();
            double x = chart.getStartPoint();
            while (x <= chart.getEndPoint()) {
                Node input = Node.parseFromString(String.valueOf(x));
                graph.add(Double.parseDouble(function.evaluate(input).toString()));
                x += chart.getStepSize();
            }

            Date delivered = new Date();
            Chart _chart = chartRepository
                    .save(new Chart(chart.getFunctionCode(), chart.getStartPoint(), chart.getEndPoint(), chart.getStepSize(), graph, submitted, delivered));
            return new ResponseEntity<>(_chart, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/charts/{id}")
    public ResponseEntity<Chart> updateChart(@PathVariable("id") long id, @RequestBody Chart chart) {
        Optional<Chart> chartData = chartRepository.findById(id);

        if (chartData.isPresent()) {
            Chart _chart = chartData.get();
            _chart.setComment(chart.getComment());
            return new ResponseEntity<>(chartRepository.save(_chart), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/charts/{id}")
    public ResponseEntity<HttpStatus> deleteChart(@PathVariable("id") long id) {
        try {
            chartRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/charts")
    public ResponseEntity<HttpStatus> deleteAllCharts() {
        try {
            chartRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}