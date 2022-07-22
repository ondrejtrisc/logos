package cz.ondrejtrisc.logos.model;

import javax.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "symbol")
    private String symbol;

    @Lob
    @Column(name = "comment")
    private String comment;

    public Stock() {}

    public Stock(String symbol) {
        this.symbol = symbol;
        this.comment = "";
    }

    public long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Expression [id=" + id + ", symbol=" + symbol + ", comment=" + comment + "]";
    }
}