package org.example.web.controllers;

import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import org.apache.log4j.Logger;
import org.example.app.exceptions.ExceptionFileNotFount;
import org.example.app.exceptions.ExceptionSyntax;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.example.web.dto.BookIdToRemove;
import org.example.web.dto.RegexToRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;

@Controller
@RequestMapping(value = "/books")
@Scope("singleton")
public class BookShelfController {

    private final Logger logger = Logger.getLogger(BookShelfController.class);
    private final BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        logger.info(this.toString());
        model.addAttribute("book", new Book());
        model.addAttribute("bookIdToRemove", new BookIdToRemove());
        model.addAttribute("bookList", bookService.getAllBooks());
        model.addAttribute("regexToRemove", new RegexToRemove());
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            model.addAttribute("bookIdToRemove", new BookIdToRemove());
            model.addAttribute("bookList", bookService.getAllBooks());
            model.addAttribute("regexToRemove", new RegexToRemove());
            return "book_shelf";
        } else {
            bookService.saveBook(book);
            logger.info("current repository size: " + bookService.getAllBooks().size());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/remove")
    public String removeBook(@Valid BookIdToRemove bookIdToRemove, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            model.addAttribute("regexToRemove", new RegexToRemove());
            return "book_shelf";
        } else {
            bookService.removeBookById(bookIdToRemove.getId());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            String name = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            //create dir
            String rootPath = System.getProperty("catalina.home");
            File dir = new File(rootPath + File.separator + "external_uploads");
            System.out.println(rootPath + " " + File.separator);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //create file
            File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
            BufferedOutputStream stream = new BufferedOutputStream(Files.newOutputStream(serverFile.toPath()));
            stream.write(bytes);
            stream.close();

            logger.info("new file saved at: " + serverFile.getAbsolutePath());

            return "redirect:/books/shelf";
        } else {
            throw new ExceptionFileNotFount("NotFoundException");
        }
    }

    @PostMapping("/removeByRegex")
    public String removeByRegex(@Valid RegexToRemove regexToRemove, BindingResult bindingResult, Model model)throws ExceptionSyntax {
        System.out.println(regexToRemove.getText());
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            model.addAttribute("bookIdToRemove", new BookIdToRemove());
            return "book_shelf";
        } else {
            for (Book book : bookService.getAllBooks()) {
                if (String.valueOf(book.getSize()).matches(regexToRemove.getText())
                        || book.getAuthor().matches(regexToRemove.getText())
                        || book.getTitle().matches(regexToRemove.getText())) {
                    bookService.removeBookById(book.getId());
                }else {
                    throw new ExceptionSyntax("Not found by regex");
                }
            }
            return "redirect:/books/shelf";
        }
    }

    @ExceptionHandler(ExceptionFileNotFount.class)
    public String handleError(Model model, ExceptionFileNotFount exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/404(2)";
    }
    @ExceptionHandler(ExceptionSyntax.class)
    public String handleErrors(Model model, ExceptionSyntax exceptionSyntax){
        model.addAttribute("errorSyntax", exceptionSyntax.getMessage());
        return "errors/404(3)";
    }
}

