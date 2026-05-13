package com.resumebinder.model;

import lombok.Data;
import java.util.List;

@Data
public class Project {
    private String title;
    private String duration;
    private List<String> bullets;
}
