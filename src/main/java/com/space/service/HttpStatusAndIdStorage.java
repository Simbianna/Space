package com.space.service;

import org.springframework.http.HttpStatus;

public class HttpStatusAndIdStorage {
    private HttpStatus status;
    private Long id;

    public HttpStatusAndIdStorage(HttpStatus status, Long id) {
        this.status = status;
        this.id = id;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
