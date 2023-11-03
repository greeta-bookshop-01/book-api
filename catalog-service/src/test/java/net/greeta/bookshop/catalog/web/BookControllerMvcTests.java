package net.greeta.bookshop.catalog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.greeta.bookshop.catalog.domain.Book;
import net.greeta.bookshop.catalog.domain.BookNotFoundException;
import net.greeta.bookshop.catalog.domain.BookService;
import net.greeta.bookshop.catalog.security.JwtAuthConverter;
import net.greeta.bookshop.catalog.security.JwtAuthConverterProperties;
import net.greeta.bookshop.catalog.security.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import({JwtGrantedAuthoritiesConverter.class, JwtAuthConverterProperties.class, JwtAuthConverter.class, WebSecurityConfig.class})
class BookControllerMvcTests {

	private static final String ROLE_BOOK_MANAGER = "ROLE_BOOK_MANAGER";
	private static final String ROLE_CUSTOMER = "ROLE_customer";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	BookService bookService;

	@MockBean
	JwtDecoder jwtDecoder;

	@Test
	void whenGetBookExistingAndAuthenticatedThenShouldReturn200() throws Exception {
		var isbn = "7373731394";
		var expectedBook = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");
		given(bookService.viewBookDetails(isbn)).willReturn(expectedBook);
		mockMvc
				.perform(get("/" + isbn)
						.with(jwt()))
				.andExpect(status().isOk());
	}

	@Test
	void whenGetBookExistingAndNotAuthenticatedThenShouldReturn200() throws Exception {
		var isbn = "7373731394";
		var expectedBook = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");
		given(bookService.viewBookDetails(isbn)).willReturn(expectedBook);
		mockMvc
				.perform(get("/" + isbn))
				.andExpect(status().isOk());
	}

	@Test
	void whenGetBookNotExistingAndAuthenticatedThenShouldReturn404() throws Exception {
		var isbn = "7373731394";
		given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);
		mockMvc
				.perform(get("/" + isbn)
						.with(jwt()))
				.andExpect(status().isNotFound());
	}

	@Test
	void whenGetBookNotExistingAndNotAuthenticatedThenShouldReturn404() throws Exception {
		var isbn = "7373731394";
		given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);
		mockMvc
				.perform(get("/" + isbn))
				.andExpect(status().isNotFound());
	}

	@Test
	void whenDeleteBookWithEmployeeRoleThenShouldReturn204() throws Exception {
		var isbn = "7373731394";
		mockMvc
				.perform(delete("/" + isbn)
						.with(jwt().authorities(new SimpleGrantedAuthority(ROLE_BOOK_MANAGER))))
				.andExpect(status().isNoContent());
	}

	@Test
	void whenDeleteBookWithCustomerRoleThenShouldReturn403() throws Exception {
		var isbn = "7373731394";
		mockMvc
				.perform(delete("/" + isbn)
						.with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
				.andExpect(status().isForbidden());
	}

	@Test
	void whenDeleteBookNotAuthenticatedThenShouldReturn401() throws Exception {
		var isbn = "7373731394";
		mockMvc
				.perform(delete("/" + isbn))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void whenPostBookWithEmployeeRoleThenShouldReturn201() throws Exception {
		var isbn = "7373731394";
		var bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");
		given(bookService.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
		mockMvc
				.perform(post("/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookToCreate))
						.with(jwt().authorities(new SimpleGrantedAuthority(ROLE_BOOK_MANAGER))))
				.andExpect(status().isCreated());
	}

	@Test
	void whenPostBookWithCustomerRoleThenShouldReturn403() throws Exception {
		var isbn = "7373731394";
		var bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");
		given(bookService.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
		mockMvc
				.perform(post("/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookToCreate))
						.with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
				.andExpect(status().isForbidden());
	}

	@Test
	void whenPostBookAndNotAuthenticatedThenShouldReturn403() throws Exception {
		var isbn = "7373731394";
		var bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");
		mockMvc
				.perform(post("/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookToCreate)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void whenPutBookWithEmployeeRoleThenShouldReturn200() throws Exception {
		var isbn = "7373731394";
		var bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");
		given(bookService.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
		mockMvc
				.perform(put("/" + isbn)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookToCreate))
						.with(jwt().authorities(new SimpleGrantedAuthority(ROLE_BOOK_MANAGER))))
				.andExpect(status().isOk());
	}

	@Test
	void whenPutBookWithCustomerRoleThenShouldReturn403() throws Exception {
		var isbn = "7373731394";
		var bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");
		given(bookService.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
		mockMvc
				.perform(put("/" + isbn)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookToCreate))
						.with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
				.andExpect(status().isForbidden());
	}

	@Test
	void whenPutBookAndNotAuthenticatedThenShouldReturn401() throws Exception {
		var isbn = "7373731394";
		var bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");
		mockMvc
				.perform(put("/" + isbn)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookToCreate)))
				.andExpect(status().isUnauthorized());
	}

}
