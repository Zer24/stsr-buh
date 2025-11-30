<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Продажа ${sale.id</title>
    <c:url var="cssUrl" value="/style.css/"/>
    <link rel="stylesheet" href"${cssUrl}">
</head>
<body>
    <div>
        <strong>ID:</strong> ${sale.id}<br>
        <strong>Покупатель:</strong> [${sale.clientId}] ${sale.clientName}<br>
        <strong>Продавец:</strong> [${sale.pharmacistId}] ${sale.pharmacistName}<br>
        <strong>Препарат:</strong> [${sale.medicineId}] ${sale.medicineName}<br>
        <strong>Количество:</strong> ${sale.quantity}<br>
        <strong>Стоимость:</strong> ${sale.totalAmount}<br>
    </div>
    <div>
        <a href="sale">Назад к списку</a>
    </div>
</body>
</html>