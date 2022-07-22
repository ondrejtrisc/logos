package cz.ondrejtrisc.logos.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Entity
@Table(name = "tradebots")
public class Tradebot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "code")
    private String code;

    @Lob
    @Column(name = "chart")
    private ArrayList<Double> chart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitted")
    private Date submitted;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delivered")
    private Date delivered;

    @Lob
    @Column(name = "comment")
    private String comment;

    public Tradebot() {}

    public Tradebot(String code, ArrayList<Double> chart, Date submitted, Date delivered) {
        this.code = code;
        this.chart = chart;
        this.submitted = submitted;
        this.delivered = delivered;
        this.comment = "";
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Double> getChart() {
        return chart;
    }

    public void setChart(ArrayList<Double> chart) {
        this.chart = chart;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    public Date getDelivered() {
        return delivered;
    }

    public void setDelivered(Date delivered) {
        this.delivered = delivered;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Tradebot [id=" + id + ", code=" + code + ", chart=" + chart + ", submitted=" + submitted + ", delivered=" + delivered + ", comment=" + comment + "]";
    }
}
