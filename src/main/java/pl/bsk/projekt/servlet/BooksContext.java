package pl.bsk.projekt.servlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import pl.bsk.projekt.entities.Book;

/**
 *
 * @author psysiu
 */
public class BooksContext implements Serializable {

    private final SortedMap<Integer, Book> books;

    public List<Book> findAllBooks() {
        return new ArrayList<>(books.values());
    }

    public BooksContext() {
        books = Collections.synchronizedSortedMap(new TreeMap<Integer, Book>());
        books.put(1, new Book(1, "Maja Lidia Kossakowska", "Ruda Sfora"));
        books.put(2, new Book(2, "Orson Scott Card", "Gra Endera"));
        books.put(3, new Book(3, "Delaney Joseph", "Jestem Grimalkin"));
    }
    
    public void saveBook(Book book) {
        if (book.getId() == null) {
            book.setId(books.lastKey() + 1);
        }
        books.put(book.getId(), book);
    }

    public Book findBook(Integer id) {
        return books.get(id);
    }
}
