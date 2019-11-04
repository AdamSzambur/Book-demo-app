package pl.szambur.bookdemoapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Transactional
    public Book updateBook(Long id, BookRequest bookRequest) {

        Optional<Book> bookFromDataBase = bookRepository.findById(id);

        if (!bookFromDataBase.isPresent()) {
            throw new BookNotFoundException(String.format("Book with id '%s' is not found",id));
        }

        Book bookToUpdate = bookFromDataBase.get();
        bookToUpdate.setTitle(bookRequest.getTitle());
        bookToUpdate.setAuthor(bookRequest.getAuthor());
        bookToUpdate.setIsbn(bookRequest.getIsbn());
        return bookToUpdate;
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
}
