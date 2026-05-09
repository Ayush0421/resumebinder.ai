package com.resumebinder.model;
import lombok.Data;
import java.util.List;

@Data
public class Experience {
    private String title;
    private String company;
    private String duration;
    private List<String> bullets;
}
