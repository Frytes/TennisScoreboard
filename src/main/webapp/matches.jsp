<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | Finished Matches</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
    <script src="js/app.js"></script>
</head>

<body>
<header class="header">
    <section class="nav-header">
        <div class="brand">
            <div class="nav-toggle">
                <img src="images/menu.png" alt="Logo" class="logo">
            </div>
            <span class="logo-text">TennisScoreboard</span>
        </div>
        <div>
            <nav class="nav-links">
                <a class="nav-link" href="${pageContext.request.contextPath}/">Home</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/matches">Matches</a>
            </nav>
        </div>
    </section>
</header>
<main>
    <div class="container">
        <h1>Matches</h1>

        <form action="${pageContext.request.contextPath}/matches" method="GET">
            <div class="input-container">
                <input
                    class="input-filter"
                    name="filter_by_player_name"
                    placeholder="Filter by name"
                    type="text"
                    value="<c:out value='${param.filter_by_player_name}'/>"
                />
                <button type="submit" class="btn-filter" style="width: 100px; margin-left: 10px;">Search</button>
                <a href="${pageContext.request.contextPath}/matches">
                    <button type="button" class="btn-filter" style="width: 100px; background-color: #999;">Reset</button>
                </a>
            </div>
        </form>

        <table class="table-matches">
            <thead>
            <tr>
                <th>Player One</th>
                <th>Player Two</th>
                <th>Winner</th>
                <th>Score</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${empty matches}">
                <tr>
                    <td colspan="4" style="text-align: center; padding: 20px;">
                        No matches found.
                    </td>
                </tr>
            </c:if>

            <c:forEach items="${matches}" var="match">
                <tr>
                    <td><c:out value="${match.player1.name}"/></td>
                    <td><c:out value="${match.player2.name}"/></td>
                    <td>
                        <span class="winner-name-td"><c:out value="${match.winner.name}"/></span>
                    </td>

                    <td style="font-weight: bold;">
                        ${match.score.player1sets} - ${match.score.player2sets}
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a class="prev" href="${pageContext.request.contextPath}/matches?page=${currentPage - 1}&filter_by_player_name=<c:out value='${filterName}'/>"> < </a>
            </c:if>

            <span class="num-page current">${currentPage}</span>

            <c:if test="${currentPage < totalPages}">
                <a class="next" href="${pageContext.request.contextPath}/matches?page=${currentPage + 1}&filter_by_player_name=<c:out value='${filterName}'/>"> > </a>
            </c:if>
        </div>
    </div>
</main>
<footer>
    <div class="footer">
        <p>&copy; Tennis Scoreboard, project from <a href="https://zhukovsd.github.io/java-backend-learning-course/">zhukovsd/java-backend-learning-course</a>
            roadmap.</p>
    </div>
</footer>
</body>
</html>