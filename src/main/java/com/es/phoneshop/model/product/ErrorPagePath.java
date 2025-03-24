package com.es.phoneshop.model.product;

public enum ErrorPagePath {
    ERROR_404("/WEB-INF/pages/error404.jsp");

    private final String template;

    ErrorPagePath(String template) {
        this.template = template;
    }
}
