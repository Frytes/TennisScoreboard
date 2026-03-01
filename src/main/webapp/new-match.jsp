<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | New Match</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
    <script src="js/app.js"></script>
    <style>
        .error-alert {
            width: 100%;
            background-color: #ffe6e6;
            color: #d63031;
            padding: 12px;
            border-radius: 18px;
            margin-bottom: 20px;
            text-align: center;
            border: 1px solid #ffcccc;
            box-sizing: border-box;
            font-weight: 400;
        }
    </style>
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
        <div>
            <h1>Start new match</h1>
            <div class="new-match-image"></div>
            <div class="form-container center">

                <c:if test="${not empty error}">
                    <div class="error-alert">
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/new-match">
                    <label class="label-player" for="playerOne">Player one</label>
                    <input class="input-player"
                           id="playerOne"
                           name="player1"
                           value="<c:out value='${player1}'/>"
                           placeholder="Name"
                           type="text"
                           required
                           title="Enter a name">

                    <label class="label-player" for="playerTwo">Player two</label>
                    <input class="input-player"
                           id="playerTwo"
                           name="player2"
                           value="<c:out value='${player2}'/>"
                           placeholder="Name"
                           type="text"
                           required
                           title="Enter a name">

                    <input class="form-button" type="submit" value="Start">
                </form>
            </div>
        </div>
    </div>
</main>
<footer>
    <div class="footer">
        <p>&copy; Tennis Scoreboard, project from <a href="https://zhukovsd.github.io/java-backend-learning-course/">zhukovsd/java-backend-learning-course</a> roadmap.</p>
    </div>
</footer>
</body>
</html>