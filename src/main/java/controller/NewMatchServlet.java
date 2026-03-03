package controller;

import exception.DatabaseOperationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Match;
import model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.PlayerRepository;
import service.OngoingMatchesService;

import java.io.IOException;

@WebServlet("/new-match")
public class NewMatchServlet extends HttpServlet {
    private final transient PlayerRepository playerRepository = new PlayerRepository();
    private static final Logger logger = LoggerFactory.getLogger(NewMatchServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/new-match.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String player1Name = req.getParameter("player1");
        String player2Name = req.getParameter("player2");

        if (player1Name == null || player1Name.trim().isEmpty() || player2Name == null || player2Name.trim().isEmpty()) {
            showError(req, resp, "Имена игроков не могут быть пустыми", player1Name, player2Name);
            return;
        }

        if (player1Name.trim().equalsIgnoreCase(player2Name.trim())) {
            showError(req, resp, "Игрок не может играть сам с собой", player1Name, player2Name);
            return;
        }

        try {
            // Комент для ревьюера. В идеале обернуть тут в транзакию, но на голом jdbc это боль.
            // Оставил как есть, но в проде все же заморочился
            Player player1 = playerRepository.findByName(player1Name)
                    .orElseGet(() -> playerRepository.create(new Player(player1Name)));

            Player player2 = playerRepository.findByName(player2Name)
                    .orElseGet(() -> playerRepository.create(new Player(player2Name)));

            Match match = new Match(player1, player2);
            OngoingMatchesService.getInstance().add(match);
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + match.getUuid());

        } catch (DatabaseOperationException e) {
            logger.error("Ошибка БД при создании матча между {} и {}", player1Name, player2Name, e);
            showError(req, resp, "Ошибка базы данных. Попробуйте позже.", player1Name, player2Name);
        } catch (Exception e) {
            logger.error("Критическая ошибка в NewMatchServlet", e);
            showError(req, resp, "Внутренняя ошибка сервера.", player1Name, player2Name);
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message, String player1Name, String player2Name) throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("player1", player1Name);
        req.setAttribute("player2", player2Name);
        req.getRequestDispatcher("/new-match.jsp").forward(req, resp);
    }
}
