package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public final class UUIDHandler {
    private UUIDHandler() {
    }

    public static UUID getUUID(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uuidString = req.getParameter("uuid");

        if (uuidString == null || uuidString.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "UUID is missing");
            return null;
        }

        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID format");
            return null;
        }
    }
}
