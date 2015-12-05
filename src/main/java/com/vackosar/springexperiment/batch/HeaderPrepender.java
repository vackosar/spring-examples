package com.vackosar.springexperiment.batch;

public class HeaderPrepender implements org.springframework.batch.item.ItemProcessor<Book, Book> {

    private static final String HEADER = "Library-Klementinum--";

    @Override
    public Book process(Book book) throws Exception {
        book.setId(HEADER + book.getId());
        return book;
    }
}