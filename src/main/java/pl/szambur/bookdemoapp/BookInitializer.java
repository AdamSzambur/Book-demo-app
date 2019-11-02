package pl.szambur.bookdemoapp;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BookInitializer implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Starting initialize sample data ...");

        Faker faker = new Faker();
        for (int i=0; i<10; i++) {
            Book book = new Book();

            book.setAuthor(faker.book().author());
            book.setTitle(faker.book().title());
            book.setIsbn(UUID.randomUUID().toString());

            bookRepository.save(book);

        }

        System.out.println("... finished initializing data");
    }
}
