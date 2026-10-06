package com.scallop.ledger.response;

import java.util.List;


/**
 * A deliberately small page envelope. Spring's own {@code Page} serialises
 * with a large, unstable shape, so the API exposes only what a client needs.
 * 
 * A stable JSON shape for paged lists. Spring's own Page type is not meant to
 * be serialised directly (its JSON layout is not a contract), so every list
 * endpoint returns this instead.
 * 
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext) {	

}
