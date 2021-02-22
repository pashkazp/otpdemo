# One-time password demo

## Опис

Веб додаток який демонструє бізнес процес використання одноразового пароля.

Сутність User має поля: емейл, імя, прізвище, дата народження, сімейний стан.
Для користування CRUD - користувачу потрібно автентифікаціюватись та авторизуватись. Автентифікація виконується за емейлом.
У випадку успішної автентифікації - генерувється OTP та відправляється на емейл користувача.
Час життя ОТП - 5хв. 

Після того як користувач отримав ОТР, йому потрібно авторизуватись за допомогою емейла та отриманого ОТР. 
Якщо OTP - валідний, генерується токен, який передається з кожним REST запитом до CRUD. У разі не валідності OTP - помилка.
Доступ до CRUD можливий тільки з токеном.

## Тестовий запуск

Для запуску веб додатка потрібно мати інстальоване:
* Linux
* Docker
* Java JDK 13
* Maven

Для запуску веб додатка розпакуйте архів та перейдіть до КАТАЛОГУ ДОДАТКА.
Виконайте команду в консолі
$ cd otpdemo

Виконайте збірку додатку
$ mvn clean package

Для перевірки, що все вдалося запустіть додаток командою в консолі в КАТАЛОЗІ ДОДАТКА
$ java -jar target/one-time-password.jar

В браузері перейдіть за адресою 
http://localhost:8080/

Ви побачите сторінку на який буде написано щось на кшталт:
```
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.

Sat Feb 20 19:42:24 EET 2021
There was an unexpected error (type=Forbidden, status=403).
Access Denied
```

Зупініть додаток натиснувши в консолі CTRL+C

### Додатково

Перед збіркою в Doсker можна виконати деякі налаштування:

#### Пошта

Додаток в своій роботі використовуе тестовий сервер пошти. 
Тобто уся пошта, яка відправляється де інде (навіть на не існуючі адреси), 
потрапляє в вхідні цього сервера і не відправляється далі.
Таким чином відправлений ОТР пароль можна скопіювати в вхідних листах цього сервера. 
Для доступу во вхідні листи цього додатку вам потрібно зареструватись за посиланням:
https://mailtrap.io/share/914143/2a6f903cbde6eb222958c28d73a5a303


Якщо ви хочете змінити тестовий сервер на деякий іншій, вам потрібно змінити налаштування серверу на свої.
Це потрібно робити в файлі (КАТАЛОГ ДОДАТКА)/src/main/resources/application.yml

В розділі, згаданому нижче, змініть потрібні налаштування. 
```
spring:
  mail:
    host: smtp_host_name
    port: port_number
    username: user_name
    password: user_password   
```

Майте на увазі, що пошта в цьому випадку буде обробляться за правилами того серверу.

#### H2 SQL консоль

Якщо ви хочете увімкнуть доступ до вебконсолі бази даних, вам потрібно змінити налаштування в файлі
(КАТАЛОГ ДОДАТКА)/src/main/resources/application.yml

В розділі, згаданому нижче, змініть 'false' на 'true' в обох випадках
```
spring:
  h2:
    console:
      enabled: false
      settings:
        web-allow-others: false
```

Після зміни цього налаштування ви зможете отримати доступ до консолі за адресою
http://localhost:8080/h2-console

Вхід за ім'ям та паролем:
test
test

(Ім'я та пароль можна змінити у файлі (КАТАЛОГ ДОДАТКА)/src/main/resources/application.yml у розділі
```
spring:
  datasource:
    username: test
    password: test
```
)

Якщо ви зробили якісь зміни у файлі (КАТАЛОГ ДОДАТКА)/src/main/resources/application.yml
виконайте команду у консолі в КАТАЛОЗІ ДОДАТКА
$ mvn clean package

## Збірка докер контейнеру та запуск

Після того виконайте збірку Докер контейнера виконавши команду в консолі в КАТАЛОЗІ ДОДАТКА
$ docker build -t otp-demo:0.0.1 .

Після збірки виконайте запуск докер контейнера виконавши команду в консолі
$ docker run --name otp-demo -d  -p 8080:8080 -t otp-demo:0.0.1

## Робота з додатком

Робота з додатком виконується через REST запити

Не авторизований доступ є до ендпоінтів:

http://localhost:8080/api/v1/auth/request-otp
http://localhost:8080/api/v1/auth/login

Авторизований доступ до ендпоінта для виконання CRUD:

http://localhost:8080/api/v1/users

### Автентіфікація

Зробити запит
```
POST http://localhost:8080/api/v1/auth/request-otp
```

в тілі запиту вказати email існуючого аутентифікуємого користувача. Наприклад:
```
{
    "email":"user@ukr.net"
}
```

В отриманому email зкопіювати One-time password у вигляді da13e24a-36ed-4dd6-91b4-7ff22013bdb7

### Авторизція

Зробити запит

```
POST http://localhost:8080/api/v1/auth/login
```
в тілі запиту вказати email та OTP. За прикладом:
```
{
    "email":"user@ukr.net",
    "password":"da13e24a-36ed-4dd6-91b4-7ff22013bdb7"
}
```

Якщо авторізація пройде то у відповідь прийде Bearer токен виду:
```
Bearer_eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQHVrci5uZXQiLCJzY29wZXMiOlt7ImF1dGhvcml,,,,,0eSI6IlJPTEVfVVNFUiJ9X
```

Цей токен потрібно скопіювати та надсилати кожним разом с запитами для роботи з CRUD.
Токен покласти в HTTP header з ключем Authorization

### CRUD

#### Отримати список юзерів
```
GET http://localhost:8080/api/v1/users
```

#### Отримання юзера по Id
```
GET http://localhost:8080/api/v1/users/{id}
```

#### Видалння юзера по id
```
DELETE http://localhost:8080/api/v1/users/{id}
```

#### Оновлення інформації
```
PUT http://localhost:8080/api/v1/users/{id}
```

запит на оновлення передавати в тілі запиту у виді
```
{
    "name": "UserName",
    "lastName": "LastName",
    "birthDay": "yyyy-MM-dd",
    "maritalStatus": "SINGLE|MARRIED|DIVORCED|WIDOWED|COMPLICATED|COMMONLAW"
}
```

При цьому деякі поля можуть бути відсутніми.

#### Створення юзера
```
POST http://localhost:8080/api/v1/users
```

запит на створення передавати в тілі запиту у виді
```
{
    "email":"123@www.net",
    "name": "UserName",
    "lastName": "LastName",
    "birthDay": "yyyy-MM-dd",
    "maritalStatus": "SINGLE|MARRIED|DIVORCED|WIDOWED|COMPLICATED|COMMONLAW"
}
```
