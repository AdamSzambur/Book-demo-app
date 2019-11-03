package pl.szambur.bookdemoapp;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;


import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Captor
    private ArgumentCaptor<BookRequest> argumentCaptor;


    @Test
    public void postingANewBookShouldANewBookInDatabase() throws Exception {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setAuthor("Duke");
        bookRequest.setTitle("Java 11");
        bookRequest.setIsbn("123");


        when (bookService.createNewBook(argumentCaptor.capture())).thenReturn(1L);

        this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location","http://localhost/api/books/1"));

        assertThat(argumentCaptor.getValue().getAuthor(), is("Duke"));
        assertThat(argumentCaptor.getValue().getIsbn(), is("123"));
        assertThat(argumentCaptor.getValue().getTitle(), is("Java 11"));
    }


    @Test
    public void allBooksEndPointShouldReturnTwoBooks() throws Exception {

        when(bookService.getAllBooks()).thenReturn(Arrays.asList(createBook(1L,"Java 11", "Duke","123")
                , createBook(2L, "Java 8", "Adam", "1234")));

        this.mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title",is("Java 11")))
                .andExpect(jsonPath("$[0].author",is("Duke")))
                .andExpect(jsonPath("$[0].isbn",is("123")))
                .andExpect(jsonPath("$[0].id",is(1)));
    }

    @Test
    public void getBookWithIdOneShouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(createBook(1L,"Java 11", "Duke","123"));

        this.mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title",is("Java 11")))
                .andExpect(jsonPath("$.author",is("Duke")))
                .andExpect(jsonPath("$.isbn",is("123")))
                .andExpect(jsonPath("$.id",is(1)));
    }

    @Test
    public void getBookWithUnknownIdShouldReturn404() throws Exception {
        when(bookService.getBookById(42L)).thenThrow(new BookNotFoundException("Book with id '42' is not found"));

        this.mockMvc.perform(get("/api/books/42"))
                .andExpect(status().isNotFound());
    }

    private Book createBook(Long id, String title, String author, String isbn) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setAuthor(author);
        book.setTitle(title);
        book.setId(id);
        return book;
    }
}
