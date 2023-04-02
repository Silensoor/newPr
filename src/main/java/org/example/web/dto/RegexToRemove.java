package org.example.web.dto;

import javax.validation.constraints.NotEmpty;

public class RegexToRemove {
    @NotEmpty
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

