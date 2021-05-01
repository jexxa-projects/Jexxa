package io.jexxa.tutorials.bookstorej16.domainservice;

import static io.jexxa.tutorials.bookstorej16.domain.aggregate.Book.newBook;

import java.util.Objects;
import java.util.stream.Stream;

import io.jexxa.tutorials.bookstorej16.domain.valueobject.ISBN13;

public record ReferenceLibrary(IBookRepository bookRepository)
{
    public ReferenceLibrary
    {
        Objects.requireNonNull(bookRepository);
    }

    public void addLatestBooks()
    {
        getLatestBooks()
                .filter(book -> !bookRepository.isRegistered(book))
                .forEach(isbn13 -> bookRepository.add(newBook(isbn13)));
    }

    /**
     * Some Random books found in internet
     */
    private Stream<ISBN13> getLatestBooks()
    {
        return Stream.of(
                new ISBN13("978-1-60309-025-4"),
                new ISBN13("978-1-60309-025-4"),
                new ISBN13("978-1-60309-047-6"),
                new ISBN13("978-1-60309-322-4"),
                new ISBN13("978-1-891830-85-3"),
                new ISBN13("978-1-60309-016-2"),
                new ISBN13("978-1-60309-265-4")
        );
    }
}
