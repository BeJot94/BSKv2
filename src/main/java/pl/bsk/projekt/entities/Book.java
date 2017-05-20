package pl.bsk.projekt.entities;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author psysiu
 */
@XmlType(name = "book")
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "book")
public class Book implements Serializable {

    @XmlAttribute(name = "id")
    private Integer id;

    @XmlAttribute(name = "author")
    private String author;

    @XmlAttribute(name = "title")
    private String title;

    public Book() {
    }

    public Book(Integer id, String author, String title) {
        this.id = id;
        this.author = author;
        this.title = title;
    }

    public Book(String author, String title) {
        this.author = author;
        this.title = title;
    }

    public Book(String authorAndTitle) {
        String[] split = authorAndTitle.split(":");
        if (split.length == 2) {
            this.author = split[1];
            this.title = split[0];
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
