<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Список пользователей</title>
    <c:url var="cssUrl" value="/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
    <h1>🥸 Список пользователей </h1>

    <table>
        <tr>
            <th>ID</th>
            <th>email</th>
            <th>name</th>
            <th>role</th>
        </tr>

        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.id}</td>
                <td>${user.email}</td>
                <td>${user.name}</td>
                <td>${user.role}</td>
            </tr>
        </c:forEach>
    </table>

    <p>
        <a href="index.jsp">На главную
    </p>
</body>
</html>