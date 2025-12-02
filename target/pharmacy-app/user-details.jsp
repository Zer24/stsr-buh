<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>${user.name}</title>
    <c:url var="cssUrl" value="/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
    <h1>${user.name}</h1>

    <div>
        <strong>ID:</strong> ${user.id}<br>
        <strong>email:</strong> ${user.email}<br>
        <strong>name:</strong> ${user.name}<br>
        <strong>role:</strong> ${user.role}<br>
    </div>

    <div>
        <a href="user"> Назад к списку</a>
    </div>
</body>
</html>