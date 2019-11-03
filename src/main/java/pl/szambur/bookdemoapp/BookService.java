package pl.szambur.bookdemoapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Long createNewBook(BookRequest bookRequest) {
        Book book = new Book();
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setTitle(bookRequest.getTitle());

        book = bookRepository.save(book);
        return book.getId();
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        Optional<Book> requestBook = bookRepository.findById(id);

        if (!requestBook.isPresent()){
            throw new BookNotFoundException(String.format("Book with id '%s' is not found",id));
        }

        return requestBook.get();
    }
}
