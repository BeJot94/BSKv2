package pl.bsk.projekt.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import pl.bsk.projekt.entities.Book;
import pl.bsk.projekt.servlet.BooksContext;

/**
 *
 * @author psysiu
 */
@Path("/books")
public class BookResource {

    public static final String BOOK_CONTEXT = "bookContext";

    public static final String MARKED_BOOKS = "markedBooks";

    @Context
    ServletContext context;

    @Context
    HttpServletRequest request;

    @Context
    HttpServletResponse response;

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> listBooks() {
        return getBookContext().findAllBooks();
    }
    
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() {
        return "Hello world!";
    }

    @GET
    @Path("marked/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> listMarkedBooks() {
        return getMarkedBooks();
    }

    @GET
    @Path("find")
    @Produces(MediaType.APPLICATION_JSON)
    public Book findBook(@DefaultValue("-1") @QueryParam("book_id") Integer bookId) {
        return getBookContext().findBook(bookId);
    }

    @GET
    @Path("mark")
    public Response markBook(@DefaultValue("-1") @QueryParam("marked_book_id") Integer markedBookId) {
        List<Book> markedBooks = getMarkedBooks();
        Book markedBook = getBookContext().findBook(markedBookId);
        if (!markedBooks.contains(markedBook)) {
            markedBooks.add(markedBook);
        }
        return Response.ok().build();
    }

    @POST
    @Path("save/form")
    public void saveBook(@FormParam("id") Integer id,
            @FormParam("title") String title,
            @FormParam("author") String author) throws IOException {
        Book book = new Book(id, author, title);
        getBookContext().saveBook(book);
        response.sendRedirect("../../list_books.html");
    }

    @POST
    @Path("save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveBook(Book book) {
        getBookContext().saveBook(book);
        return Response.ok().build();
    }
    
    @GET
    @Path("find/{id:[0-9]+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Book findBook2(@PathParam("id") Integer id) {
        return getBookContext().findBook(id);
    }
    
    @GET
    @Path("find/{id:[0-9]+}/translated")
    public Response findBook3(@PathParam("id") Integer id, @Context Request r) {
        List<Variant> vs = Variant.mediaTypes(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE).languages(Locale.ENGLISH, Locale.forLanguageTag("pl")).add().build();
        Variant v = r.selectVariant(vs);
        if (v == null) {
            return Response.notAcceptable(vs).build();
        } else {
            Book book = getBookContext().findBook(id);
            if (book == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (v.getLanguage() == Locale.forLanguageTag("pl")) {
                return Response.ok(new Book(book.getId(), book.getAuthor(), book.getTitle() + "-translated")).build();
            }
            return Response.ok(book).build();
        }
    }
    
    @GET
    @Path("find/{id:[0-9]+}/title")
    @Produces(MediaType.TEXT_PLAIN)
    public String findBookTitle(@PathParam("id") Integer id) {
        return getBookContext().findBook(id).getTitle();
    }
    
    @GET
    @Path("add/{book}")
    @Produces(MediaType.TEXT_PLAIN)
    public String addBook(@PathParam("book") Book book) {
        getBookContext().saveBook(book);
        return book.getId() + " " + book.getAuthor() + " - " + book.getTitle();
    }
    
    @GET
    @Path("add")
    @Produces(MediaType.TEXT_PLAIN)
    public String addBook2(@DefaultValue("Milena Wójtowicz:Wrota") @QueryParam("book") Book book) {
        getBookContext().saveBook(book);
        return book.getId() + " " + book.getAuthor() + " - " + book.getTitle();
    }
    
    @GET
    @Path("add/many")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> addBooks(@DefaultValue("Milena Wójtowicz") @QueryParam("book") List<Book> books) {
        for (Book book : books) {
            getBookContext().saveBook(book);
        }
        return books;
    }
    
    private BooksContext getBookContext() {
        BooksContext bookContext = (BooksContext) context.getAttribute(BOOK_CONTEXT);
        if (bookContext == null) {
            bookContext = new BooksContext();
            context.setAttribute(BOOK_CONTEXT, bookContext);
        }
        return bookContext;
    }

    private List<Book> getMarkedBooks() {
        List<Book> markedBooks = (List<Book>) request.getSession().getAttribute(MARKED_BOOKS);
        if (markedBooks == null) {
            markedBooks = new ArrayList<>();
            request.getSession().setAttribute(MARKED_BOOKS, markedBooks);
        }
        return markedBooks;
    }
}
