package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Match;
import repository.MatchRepository;

import java.io.IOException;
import java.util.List;

@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {
    private static final int LIMIT_ON_PAGE = 4;
    private final transient MatchRepository matchRepository = new MatchRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filterByName = req.getParameter("filter_by_player_name");
        String pageStr = req.getParameter("page");
        int page = 1;
        try {
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
            }
        } catch (NumberFormatException e) {
            //ignore
        }

        if (page < 1) {
            page = 1;
        }

        int limit = LIMIT_ON_PAGE;
        int offset = (page - 1) * limit;
        long totalMatches = matchRepository.countMatchesByFilter(filterByName);
        List<Match> matches = matchRepository.findAllByFilter(filterByName, limit, offset);

        int totalPages = (int) Math.ceil((double) totalMatches / limit);

        req.setAttribute("matches", matches);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("filterName", filterByName);

        req.getRequestDispatcher("/matches.jsp").forward(req, resp);
    }
}
