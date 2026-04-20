package com.distributedsystem.common.node;

public class LogEntry {
    private long term;
    private long index;
    private String key;
    private String value;

    public LogEntry() {}

    public LogEntry(long term, long index, String key, String value) {
        this.term = term;
        this.index = index;
        this.key = key;
        this.value = value;
    }

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
