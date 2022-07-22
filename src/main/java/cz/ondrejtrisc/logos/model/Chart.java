package cz.ondrejtrisc.logos.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

@Entity
@Table(name = "charts")
public class Chart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "function_code")
    private String functionCode;

    @Column(name = "start_point")
    private double startPoint;

    @Column(name = "end_point")
    private double endPoint;

    @Column(name = "step_size")
    private double stepSize;

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

    public Chart() {}

    public Chart(String functionCode, double startPoint, double endPoint, double stepSize, ArrayList<Double> chart, Date submitted, Date delivered) {
        this.functionCode = functionCode;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.stepSize = stepSize;
        this.chart = chart;
        this.submitted = submitted;
        this.delivered = delivered;
        this.comment = "";
    }

    public long getId() {
        return id;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public double getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(double startPoint) {
        this.startPoint = startPoint;
    }

    public double getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(double endPoint) {
        this.endPoint = endPoint;
    }

    public double getStepSize() {
        return stepSize;
    }

    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
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
        return "Chart [id=" + id + ", functionCode=" + functionCode + ", startPoint=" + startPoint + ", endPoint=" + endPoint + ", stepSize=" + stepSize + ", chart=" + chart + ", submitted=" + submitted + ", delivered=" + delivered + ", comment=" + comment + "]";
    }
}
