<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Список продаж</title>
    <c:url var="cssUrl" value="/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
    <h1>💰 Список продаж</h1>

    <table>
        <tr>
            <th>ID</th>
            <th>Клиент</th>
            <th>Продавец</th>
            <th>Препарат</th>
            <th>Количество</th>
            <th>Цена</th>
            <th>Время</th>
        </tr>

        <c:forEach var="sale" items="${sales}">
            <tr>
                <td>${sale.id}</td>
                <td>${sale.clientName}</td>
                <td>${sale.pharmacistName}</td>
                <td>${sale.medicineName}</td>
                <td>${sale.quantity}</td>
                <td>${sale.totalAmount}</td>
                <td>${sale.saleDateTime}</td>
            </tr>
        </c:forEach>
    </table>

    <p><a href="index.jsp">На главную</p>
</body>
</html>