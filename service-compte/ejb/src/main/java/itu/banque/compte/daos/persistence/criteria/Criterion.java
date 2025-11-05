package itu.banque.compte.daos.persistence.criteria;

public record Criterion(String fieldName, Object value, Operator operator, boolean or) {
    public Criterion(String fieldName, Object value, Operator operator) {
        this(fieldName, value, operator, false); 
    }
}
