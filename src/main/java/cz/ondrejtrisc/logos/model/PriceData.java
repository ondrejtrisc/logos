package cz.ondrejtrisc.logos.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "price_data")
public class PriceData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Stock stock;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date date;

    @Column(name = "open")
    private double open;

    @Column(name = "high")
    private double high;

    @Column(name = "low")
    private double low;

    @Column(name = "close")
    private double close;

    @Column(name = "adj_close")
    private double adjClose;

    @Column(name = "volume")
    private int volume;

    @Lob
    @Column(name = "comment")
    private String comment;

    public PriceData() {}

    public PriceData(Stock stock, Date date, double open, double high, double low, double close, double adjClose, int volume) {
        this.stock = stock;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.adjClose = adjClose;
        this.volume = volume;
        this.comment = "";
    }

    public long getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(double adjClose) {
        this.adjClose = adjClose;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Expression [id=" + id + ", stock=" + stock.getSymbol() + ", date=" + date + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", adjClose=" + adjClose + ", volume=" + volume + ", comment=" + comment + ", comment=" + comment + "]";
    }
}