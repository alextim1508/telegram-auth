# Telegram Authentication Service

Этот проект демонстрирует, как реализовать аутентификацию через Telegram Login Widget в приложении Spring Boot. Он безопасно проверяет данные аутентификации, полученные от Telegram, с использованием HMAC-SHA256. Этот сервис может быть интегрирован в более крупные приложения, такие как [витрина интернет-магазина](https://github.com/alextim1508/intershop), для предоставления пользовательского входа через Telegram.

## ⚙️ Настройка

1.  **Создайте файл конфигурации Ngrok:** Создайте файл `secrets/ngrok.yml` в корне проекта.
    ```yaml
    version: "2"
    authtoken: YOUR_NGROK_AUTH_TOKEN # Замените на ваш действительный токен Ngrok
    tunnels:
      myapp:
        proto: http
        addr: host.docker.internal:8080 # Адрес приложения Spring Boot внутри контейнера
    ```

2.  **Создайте файл переменных окружения приложения:** Создайте файл `secrets/telegramtoken.env`.
    ```env
    # Токен Telegram-бота (обязательно)
    TELEGRAM_BOT_TOKEN=YOUR_TELEGRAM_BOT_TOKEN # Замените на действительный токен вашего Telegram-бота

    # Имя Telegram-бота (обязательно для виджета)
    TELEGRAM_BOT_NAME=YOUR_TELEGRAM_BOT_USERNAME # Замените на имя пользователя вашего бота (например, mybot)
    ```

3.  **Установите базовый URL приложения:** `APP_BASE_URL` настраивается непосредственно в файле `docker-compose.yml`. Обновите раздел `environment` для сервиса `app`:
    ```yaml
    environment:
      APP_BASE_URL: https://YOUR_NGROK_SUBDOMAIN.ngrok-free.dev # Замените на ваш действительный URL Ngrok
    ```

## 🚀 Запуск приложения

1.  **Склонируйте код и перейдите в директорию проекта.**
    ```bash
    git clone https://github.com/alextim1508/telegram-auth
    cd telegram-auth # Перейдите в директорию проекта
    ```

2.  **Запустите сервисы:** 
    ```bash
    docker-compose -f docker-compose.yml up -d
    ```

3.  **Доступ к приложению:** 
    ```
    https://YOUR_NGROK_URL/auth/telegram
    ```
    Этот адрес отдает HTML-файл, содержащий Telegram Login Widget, и обрабатывает запрос аутентификации (`/auth/telegram/token`).

## 🔐 Как это работает

1.  Приложение отдает HTML-страницу (`telegramAuth.html`), содержащую Telegram Login Widget.
2.  Когда пользователь нажимает на виджет и проходит аутентификацию в Telegram, Telegram отправляет данные пользователя (ID, имя и т.д.) и `hash` на указанный URL обратного вызова (`/auth/telegram/token`).
3.  Backend Spring Boot получает эти данные.
4.  Backend удаляет `hash` из данных, сортирует оставшиеся пары ключ-значение в лексикографическом порядке, объединяет их в строку (`key1=value1\nkey2=value2...`) и вычисляет хеш HMAC-SHA256, используя токен Telegram-бота в качестве секретного ключа.
5.  Вычисленный хеш сравнивается с `hash`, полученным от Telegram.
6.  Если хеши совпадают, аутентификация считается действительной, и возвращается фиктивный токен (или реальный токен в продакшен-сценарии). В противном случае возвращается ошибка.
