<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Добавить пользователя</title>
    <c:url var="cssUrl" value="/css/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
    <style>
        .form-container {
            max-width: 500px;
            margin: 20px auto;
            padding: 20px;
            background: white;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #2c3e50;
        }
        .form-input, .form-select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 3px;
            box-sizing: border-box;
        }
        .password-hint {
            font-size: 12px;
            color: #7f8c8d;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>👤 Добавить пользователя</h1>
        <div class="nav">
            <a href="user">Назад к списку</a>
        </div>
    </div>

    <div class="form-container">
        <c:if test="${not empty error}">
            <div class="error-message">
                ❌ ${error}
            </div>
        </c:if>

        <c:if test="${not empty success}">
            <div class="success-message">
                ✅ ${success}
            </div>
        </c:if>

        <form action="user?action=add" method="post">
            <div class="form-group">
                <label class="form-label" for="name">Полное имя *</label>
                <input type="text" id="name" name="name" class="form-input"
                       value="${param.name}" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="email">Email *</label>
                <input type="email" id="email" name="email" class="form-input"
                       value="${param.email}" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Пароль *</label>
                <input type="password" id="password" name="password" class="form-input"
                       required>
                <div class="password-hint">
                    Минимум 6 символов
                </div>
            </div>

            <div class="form-group">
                <label class="form-label" for="confirmPassword">Подтверждение пароля *</label>
                <input type="password" id="confirmPassword" name="confirmPassword"
                       class="form-input" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="role">Роль *</label>
                <select id="role" name="role" class="form-select" required>
                    <option value="">Выберите роль</option>
                    <option value="user" ${param.role == 'user' ? 'selected' : ''}>Пользователь</option>
                    <option value="employee" ${param.role == 'employee' ? 'selected' : ''}>Фармацевт</option>
                    <option value="admin" ${param.role == 'admin' ? 'selected' : ''}>Администратор</option>
                </select>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-success">Создать пользователя</button>
                <a href="user" class="btn btn-primary">Отмена</a>
            </div>
        </form>
    </div>
</body>
</html>