<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Вход в систему</title>
</head>
<body>
    <h2>Авторизация</h2>

    <c:if test="${not empty error}">
        <p style="color: red">${error}</p>
    </c:if>

    <form action="login" method="post">
        <div>
            <label>Логин:</label>
            <input type="text" name="email" required>
        </div>
        <div>
            <label>Пароль:</label>
            <input type="password" name="password">
        </div>
        <button type="submit">Войти</button>
    </form>
</body>
</html>