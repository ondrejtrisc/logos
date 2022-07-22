package cz.ondrejtrisc.logos.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "query")
    private String query;

    @Lob
    @Convert(converter = EvaluationConverter.class)
    @Column(name = "evaluation_list")
    private ArrayList<String> evaluationList;

    @Column(name = "value")
    private String value;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitted")
    private Date submitted;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delivered")
    private Date delivered;

    @Lob
    @Column(name = "comment")
    private String comment;

    public Evaluation() {}

    public Evaluation(String query, ArrayList<String> evaluationList, Date submitted, Date delivered) {
        this.query = query;
        this.evaluationList = evaluationList;
        this.value = evaluationList.get(evaluationList.size() - 1);
        this.submitted = submitted;
        this.delivered = delivered;
        this.comment = "";
    }

    public long getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<String> getEvaluationList() {
        return evaluationList;
    }

    public void setEvaluationList(ArrayList<String> evaluationList) {
        this.evaluationList = evaluationList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        return "Evaluation [id=" + id + ", query=" + query + ", evaluationList=" + evaluationList + ", value=" + value + ", submitted=" + submitted + ", delivered=" + delivered + ", comment=" + comment + "]";
    }
}

@Converter
class EvaluationConverter implements AttributeConverter<ArrayList<String>, String> {

    @Override
    public String convertToDatabaseColumn(ArrayList<String> list) {
        return String.join(";", list);
    }

    @Override
    public ArrayList<String> convertToEntityAttribute(String str) {
        String[] strSplit = str.split(";");
        return new ArrayList<>(Arrays.asList(strSplit));
    }
}