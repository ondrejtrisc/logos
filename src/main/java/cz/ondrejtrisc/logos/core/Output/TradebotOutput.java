package cz.ondrejtrisc.logos.core.Output;

import cz.ondrejtrisc.logos.core.Function;
import cz.ondrejtrisc.logos.core.List;
import cz.ondrejtrisc.logos.service.PriceDataService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TradebotOutput implements Output {

    private final PriceDataService priceDataService;
    private final HashMap<String, Double> portfolio;

    private final ArrayList<Double> balance = new ArrayList<>();
    private Double cost = 0.0;
    private boolean failed = false;


    public TradebotOutput(PriceDataService priceDataService, HashMap<String, Double> portfolio) {
        this.priceDataService = priceDataService;
        this.portfolio = portfolio;
    }

    public ArrayList<Double> getBalance() {
        return balance;
    }

    public boolean isFailed() {
        return failed;
    }

    @Override
    public void out(Function ret) {
        List retList = (List) ret;
        String date = retList.get(0).toString();
        List tradeList = (List) retList.get(1);
        for (int i = 0; i < tradeList.size(); i++) {
            String symbol = ((List) tradeList.get(i)).get(0).toString();
            double quantity = Double.parseDouble(((List) tradeList.get(i)).get(1).toString());
            Double currentQuantity = portfolio.get(symbol);
            if (currentQuantity == null) {
                currentQuantity = 0.0;
            }
            portfolio.put(symbol, currentQuantity + quantity);
            try {
                cost += priceDataService.findOpenByStockAndDate(symbol, date) * quantity;
            } catch (ParseException e) {
                failed = true;
            }
        }

        double newBalance = 0.0;
        for (Map.Entry stock : portfolio.entrySet()) {
            String symbol = (String) stock.getKey();
            try {
                newBalance += priceDataService.findOpenByStockAndDate(symbol, date) * portfolio.get(symbol);
            } catch (ParseException e) {
                failed = true;
            }
        }
        newBalance = newBalance - cost;
        balance.add(newBalance);
    }
}
