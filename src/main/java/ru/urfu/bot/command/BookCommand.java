package ru.urfu.bot.command;

import org.springframework.stereotype.Component;
import ru.urfu.bot.service.BookService;

/**
 * Обработчик команды /book и кнопок быстрого поиска книг.
 */
@Component
public class BookCommand implements BotCommand {

    private static final String BTN_JAVA = "Книги по Java";
    private static final String BTN_CLEAN = "Книги Clean Code";

    private final BookService bookService;

    public BookCommand(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean supports(String messageText) {
        String normalized = messageText.trim();
        return normalized.toLowerCase().startsWith("/book")
                || BTN_JAVA.equalsIgnoreCase(normalized)
                || BTN_CLEAN.equalsIgnoreCase(normalized);
    }

    @Override
    public String execute(String messageText) {
        String normalized = messageText.trim();
        if (BTN_JAVA.equalsIgnoreCase(normalized)) {
            return bookService.searchBooks("Java");
        }
        if (BTN_CLEAN.equalsIgnoreCase(normalized)) {
            return bookService.searchBooks("Clean Code");
        }
        String query = normalized.substring("/book".length()).trim();
        return bookService.searchBooks(query);
    }
}
