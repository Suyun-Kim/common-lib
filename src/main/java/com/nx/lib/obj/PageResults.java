package com.nx.lib.obj;

public class PageResults<T> {

    private int size;
    private int page;
    private long totalElements;
    private T content;

    public PageResults(T content, int size, int page, long totalElements) {
        this.content = content;
        this.size = size;
        this.page = page;
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public T getContent() {
        return content;
    }

    public void setContents(T content) {
        this.content = content;
    }
}
