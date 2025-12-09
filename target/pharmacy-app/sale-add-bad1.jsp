<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Новая продажа</title>
    <c:url var="cssUrl" value="/css/style.css"/>
    <link rel="stylesheet" href="${cssUrl}">
    <!-- Подключаем Select2 для красивых select с поиском -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <style>
        .form-container {
            max-width: 600px;
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
        /* Стили для Select2 */
        .select2-container--default .select2-selection--single {
            height: 38px;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        .select2-container--default .select2-selection--single .select2-selection__rendered {
            line-height: 38px;
        }
        .select2-container--default .select2-selection--single .select2-selection__arrow {
            height: 36px;
        }
        .loading-indicator {
            display: none;
            color: #666;
            font-style: italic;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Новая продажа</h1>
        <div class="nav">
            <a href="sale">Назад к списку</a>
        </div>
    </div>

    <div class="form-container">
        <c:if test="${not empty error}">
            <div class="error-message">
                ❌ ${error}
            </div>
        </c:if>

        <form action="sale" method="post" id="saleForm">
            <div class="form-group">
                <label class="form-label" for="clientId">Клиент *</label>
                <select id="clientId" name="clientId" class="form-select" required>
                    <option value="">Выберите клиента</option>
                    <c:if test="${not empty param.clientId}">
                        <option value="${param.clientId}" selected>Загрузка...</option>
                    </c:if>
                </select>
                <div id="clientLoading" class="loading-indicator">Загрузка клиентов...</div>
            </div>

            <div class="form-group">
                <label class="form-label" for="pharmacistId">Продавец *</label>
                <input type="text" id="pharmacistId" name="pharmacistId" class="form-input"
                       value="${sessionUser.fullName}" readonly>
                <input type="hidden" name="pharmacistId" value="${sessionUser.id}">
            </div>

            <div class="form-group">
                <label class="form-label" for="medicineId">Препарат *</label>
                <select id="medicineId" name="medicineId" class="form-select" required
                        onchange="updateMedicinePrice()">
                    <option value="">Выберите препарат</option>
                    <c:if test="${not empty param.medicineId}">
                        <option value="${param.medicineId}" selected>Загрузка...</option>
                    </c:if>
                </select>
                <div id="medicineLoading" class="loading-indicator">Загрузка препаратов...</div>
            </div>

            <div class="form-group">
                <label class="form-label" for="quantity">Количество *</label>
                <input type="number" id="quantity" name="quantity" class="form-input"
                       min="1" value="${param.quantity}" oninput="calculateTotal()" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="unitPrice">Цена за единицу (руб.)</label>
                <input type="number" id="unitPrice" name="unitPrice" class="form-input"
                       step="0.01" min="0" value="${param.unitPrice}" oninput="calculateTotal()" required>
                <small id="priceSource" style="color: #666; font-style: italic;"></small>
            </div>

            <div class="form-group">
                <label class="form-label" for="totalAmount">Общая сумма (руб.)</label>
                <input type="number" id="totalAmount" name="totalAmount" class="form-input"
                       step="0.01" min="0" value="${param.totalAmount}" readonly>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-success">Оформить продажу</button>
                <a href="sale" class="btn btn-primary">Отмена</a>
            </div>
        </form>
    </div>

    <!-- Подключаем jQuery и Select2 -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script>
        $(document).ready(function() {
            // Инициализация Select2 для клиентов
            $('#clientId').select2({
                placeholder: 'Поиск клиента по имени...',
                allowClear: true,
                ajax: {
                    url: '${pageContext.request.contextPath}/api/clients/search',
                    dataType: 'json',
                    delay: 250,
                    data: function (params) {
                        return {
                            query: params.term,
                            page: params.page || 1
                        };
                    },
                    processResults: function (data) {
                        return {
                            results: data.map(function(client) {
                                return {
                                    id: client.id,
                                    text: client.name + (client.phone ? ' - ' + client.phone : '')
                                };
                            })
                        };
                    },
                    cache: true
                },
                minimumInputLength: 1
            });

            // Инициализация Select2 для препаратов
            $('#medicineId').select2({
                placeholder: 'Поиск препарата по названию...',
                allowClear: true,
                ajax: {
                    url: '${pageContext.request.contextPath}/api/medicines/search',
                    dataType: 'json',
                    delay: 250,
                    data: function (params) {
                        return {
                            query: params.term,
                            page: params.page || 1
                        };
                    },
                    processResults: function (data) {
                        return {
                            results: data.map(function(medicine) {
                                return {
                                    id: medicine.id,
                                    text: medicine.name +
                                          (medicine.manufacturer ? ' (' + medicine.manufacturer + ')' : '') +
                                          ' - ' + (medicine.price ? medicine.price + ' руб.' : 'цена не указана')
                                };
                            })
                        };
                    },
                    cache: true
                },
                minimumInputLength: 1
            });

            // Загружаем предварительно выбранные значения если они есть
            loadInitialValues();
        });

        function loadInitialValues() {
            const clientId = '${param.clientId}';
            const medicineId = '${param.medicineId}';

            if (clientId) {
                $('#clientLoading').show();
                $.ajax({
                    url: '${pageContext.request.contextPath}/api/clients/' + clientId,
                    success: function(client) {
                        if (client) {
                            var option = new Option(
                                client.name + (client.phone ? ' - ' + client.phone : ''),
                                client.id,
                                true,
                                true
                            );
                            $('#clientId').append(option).trigger('change');
                        }
                        $('#clientLoading').hide();
                    },
                    error: function() {
                        $('#clientLoading').hide();
                    }
                });
            }

            if (medicineId) {
                $('#medicineLoading').show();
                $.ajax({
                    url: 'medicine?format=json&id=' + medicineId,
                    success: function(medicine) {
                        if (medicine) {
                            var option = new Option(
                                medicine.name +
                                (medicine.manufacturer ? ' (' + medicine.manufacturer + ')' : '') +
                                ' - ' + (medicine.price ? medicine.price + ' руб.' : 'цена не указана'),
                                medicine.id,
                                true,
                                true
                            );
                            $('#medicineId').append(option).trigger('change');
                            if (medicine.price) {
                                $('#unitPrice').val(medicine.price);
                                calculateTotal();
                                $('#priceSource').text('Цена из базы данных');
                            }
                        }
                        $('#medicineLoading').hide();
                    },
                    error: function() {
                        $('#medicineLoading').hide();
                    }
                });
            }
        }

        function updateMedicinePrice() {
            const medicineId = $('#medicineId').val();
            if (medicineId) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/api/medicines/' + medicineId + '/price',
                    success: function(price) {
                        if (price) {
                            $('#unitPrice').val(price);
                            $('#priceSource').text('Цена из базы данных');
                            calculateTotal();
                        }
                    }
                });
            }
        }

        function calculateTotal() {
            const price = parseFloat($('#unitPrice').val()) || 0;
            const quantity = parseInt($('#quantity').val()) || 0;
            const total = price * quantity;
            $('#totalAmount').val(total.toFixed(2));

            // Если цена введена вручную, показываем это
            if ($('#unitPrice').is(':focus') || $('#unitPrice')[0].checkValidity()) {
                $('#priceSource').text('Цена введена вручную');
            }
        }

        // Также вызываем calculateTotal при изменении цены через JavaScript
        $('#unitPrice').on('input change', calculateTotal);
        $('#quantity').on('input change', calculateTotal);
    </script>
</body>
</html>