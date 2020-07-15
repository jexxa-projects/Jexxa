package io.jexxa.tutorials.bookstore.domainservice;

import java.util.stream.Stream;

import io.jexxa.tutorials.bookstore.domain.aggregate.Book;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;
import org.apache.commons.lang3.Validate;

public class ReferenceLibrary
{
    private final IBookRepository bookRepository;

    public ReferenceLibrary(IBookRepository bookRepository)
    {
        Validate.notNull(bookRepository);
        this.bookRepository = bookRepository;
    }

    public void addLatestBooks()
    {
        getLatestBooks()
                .filter(element -> bookRepository.search(element).isEmpty())
                .forEach(element -> bookRepository.add(Book.create(element)));
    }

    /** Some Random books found in internet */
    private Stream<ISBN13> getLatestBooks()
    {
        return Stream.of(
                new ISBN13("978-1-60309-025-4" ),
                new ISBN13("978-1-60309-025-4" ),
                new ISBN13("978-1-60309-047-6" ),
                new ISBN13("978-1-60309-322-4" ),
                new ISBN13("978-1-891830-85-3" ),
                new ISBN13("978-1-60309-016-2" ),
                new ISBN13("978-1-60309-265-4" )
        );
    }
}
