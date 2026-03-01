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
        String name1 = req.getParameter("player1");
        String name2 = req.getParameter("player2");

        if (name1 == null || name1.trim().isEmpty() || name2 == null || name2.trim().isEmpty()) {
            showError(req, resp, "Имена игроков не могут быть пустыми", name1, name2);
            return;
        }

        if (name1.trim().equalsIgnoreCase(name2.trim())) {
            showError(req, resp, "Игрок не может играть сам с собой", name1, name2);
            return;
        }

        try {
            // Комент для ревьюера. В идеале обернуть тут в транзакию, но на голом jdbc это боль.
            // Оставил как есть, но в проде все же заморочился
            Player player1 = playerRepository.findByName(name1)
                    .orElseGet(() -> playerRepository.create(new Player(name1)));

            Player player2 = playerRepository.findByName(name2)
                    .orElseGet(() -> playerRepository.create(new Player(name2)));

            Match match = new Match(player1, player2);
            OngoingMatchesService.getInstance().add(match);
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + match.getUuid());

        } catch (DatabaseOperationException e) {
            logger.error("Ошибка БД при создании матча между {} и {}", name1, name2, e);
            showError(req, resp, "Ошибка базы данных. Попробуйте позже.", name1, name2);
        } catch (Exception e) {
            logger.error("Критическая ошибка в NewMatchServlet", e);
            showError(req, resp, "Внутренняя ошибка сервера.", name1, name2);
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message, String name1, String name2) throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("player1", name1);
        req.setAttribute("player2", name2);
        req.getRequestDispatcher("/new-match.jsp").forward(req, resp);
    }
}
