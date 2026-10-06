package com.scallop.ledger.request;

import java.util.UUID;

import com.scallop.ledger.constant.PlatformRole;

/**
 * The authenticated caller, as carried in the security context. Deliberately
 * minimal: it holds only what the token proved, so nothing here can go stale
 * against the database mid-request.
 */
public record AuthenticatedUser(UUID id, String phone, PlatformRole role) {

    public boolean isAdmin() {
        return role == PlatformRole.ADMIN;
    }
}
