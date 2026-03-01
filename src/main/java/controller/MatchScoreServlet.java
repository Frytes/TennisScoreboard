package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Match;
import repository.MatchRepository;
import service.MatchScoreCalculationService;
import service.OngoingMatchesService;
import util.UUIDHandler;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {

    private final transient MatchRepository matchRepository = new MatchRepository();
    private final transient MatchScoreCalculationService matchScoreCalculationService = new MatchScoreCalculationService();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = UUIDHandler.getUUID(req, resp);

        if (uuid == null) {
            return;
        }

        Match match = OngoingMatchesService.getInstance().get(uuid);

        if (match == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Матч с указанным UUID не найден");
            return;
        }
        synchronized (match) {
            req.setAttribute("match", match);
            req.getRequestDispatcher("/match-score.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID uuid = UUIDHandler.getUUID(req, resp);
        String winnerIdStr = req.getParameter("winnerId");

        if (uuid == null || winnerIdStr == null) {
            return;
        }

        long winnerId;

        try {
            winnerId = Long.parseLong(winnerIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат ID игрока");
            return;
        }
        Match match = OngoingMatchesService.getInstance().get(uuid);
        if (match == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Матч не найден");
            return;
        }
        synchronized (match) {
            if (match.getWinner() != null) {
                resp.sendRedirect(req.getContextPath() + "/matches");
                return;
            }
            long player1id = match.getPlayer1().getId();
            long player2id = match.getPlayer2().getId();

            if (winnerId == player1id) {
                matchScoreCalculationService.calculateScore(match, match.getPlayer1());
            } else if (winnerId == player2id) {
                matchScoreCalculationService.calculateScore(match, match.getPlayer2());
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Игрок с таким ID не участвует в матче");
                return;
            }

            if (match.getWinner() != null) {
                matchRepository.save(match);
                OngoingMatchesService.getInstance().remove(uuid);
                resp.sendRedirect(req.getContextPath() + "/matches");
                return;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + match.getUuid());
    }
}

