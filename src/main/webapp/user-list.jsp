<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Пользователи системы</title>
    <c:url var="cssUrl" value="/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
    <style>
        .role-admin { color: #d35400; font-weight: bold; }
        .role-user { color: #27ae60; }
        .role-pharmacist { color: #2980b9; }
    </style>
</head>
<body>
    <h1>👥 Пользователи системы</h1>

    <table>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Email</th>
            <th>Роль</th>
            <th>Действия</th>
        </tr>

        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.id}</td>
                <td>
                    <a href="user?id=${user.id}&format=html">${user.name}</a>
                </td>
                <td>${user.email}</td>
                <td class="role-${user.role}">
                    <c:choose>
                        <c:when test="${user.role == 'ADMIN'}">Администратор</c:when>
                        <c:when test="${user.role == 'PHARMACIST'}">Фармацевт</c:when>
                        <c:when test="${user.role == 'USER'}">Пользователь</c:when>
                        <c:otherwise>${user.role}</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <button class="btn-danger" onclick="confirmDelete(${user.id},'${user.name}')">Удалить</button>
                </td>
            </tr>
        </c:forEach>
    </table>

    <div>
        <a href="index.jsp">На главную</a> |
        <a href="user-add.jsp">Добавить пользователя</a>
    </div>

    <script>
        function confirmDelete(userId, userName) {
            if (confirm('Вы уверены, что хотите удалить пользователя "' + userName + '" (ID: ' + userId + ')?')) {
                fetch('user?id=' + userId, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    }
                }).then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Ошибка при удалении пользователя');
                    }
                }).catch(error => {
                    console.error('Ошибка:', error);
                    alert('Ошибка при удалении пользователя');
                });
            }
        }
    </script>
</body>
</html>