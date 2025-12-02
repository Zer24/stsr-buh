<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Аптека - Главная</title>
    <c:url var="cssUrl" value="/style.css"/>
        <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
    <div class="header">
        <h1>💊 Аптека "Здоровье"</h1>
        <p>Система управления аптекой</p>
    </div>

    <div class="nav">
        <a href="medicine">Лекарства</a>
        <a href="user">Пользователи</a>
        <a href="sale">Продажи</a>
        <a href="medicines?action=add">Добавить лекарство</a>
    </div>

    <div class="container">
        <div class="card">
            <h3>📦 Управление лекарствами</h3>
            <p>Просмотр, добавление и редактирование лекарственных препаратов</p>
            <div class="stats" id="medicineCount">-</div>
            <a href="medicine">Перейти к лекарствам →</a>
        </div>

        <div class="card">
            <h3>👥 Управление клиентами</h3>
            <p>Работа с базой клиентов и их данными</p>
            <div class="stats" id="userCount">-</div>
            <a href="user">Перейти к клиентам →</a>
        </div>

        <div class="card">
            <h3>💰 Управление продажами</h3>
            <p>История продаж и создание новых</p>
            <div class="stats" id="saleCount">-</div>
            <a href="sale">Перейти к продажам →</a>
        </div>
    </div>

    <script>
        // Загружаем статистику при загрузке страницы
        fetch('medicine?format=json')
            .then(r => r.json())
            .then(data => document.getElementById('medicineCount').textContent = data.length || '0');

        fetch('user?format=json')
            .then(r => r.json())
            .then(data => document.getElementById('userCount').textContent = data.length || '0');

        fetch('sale?format=json')
            .then(r => r.json())
            .then(data => document.getElementById('saleCount').textContent = data.length || '0');
    </script>
</body>
</html>