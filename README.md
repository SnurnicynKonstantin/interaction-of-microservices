## MariaDB

В docker-compose был добавлен volume `./mariadb:/var/lib/mysql`. Он позволяет хранить данные при отключении контэйнера. Можно любой СУБД использовать этот файл и получить данные из него

Для подключения к базе:
- Логин - test
- Пароль - test 
- База данных - test_db
- Хост - localhost:3306

Проброшен порт 3306 для доступа к БД снаружи докера.

## Kafka
Для работы с Kafka были использваны две бмблионтеки ради практики и получения опыта работы с одоими:
- `spring-kafka` из `org.springframework.kafka` - Для консьюмера
- `kafka-clients` из `org.apache.kafka` - Для продьюсера

Для передачи объектов можно было написать сериализатори и десериализаторы, которые предавали бы в топик byte[]. Но это было бы не наглядно. В текущей имплиментации можно посмотреть какие данные в виде строки приходят в Kafka.

Для producer (mc2) реализовал следующие свойства:
- `"acks"` = "all" - Необходимо получить подтверждения о получнии от лидера и реплик. Для того что бы считать, что сообщение получено успешно.
- `"retries"` = 0 - Не делать дополнительные попытки отправки при провальной первой
- `"batch.size"` = 16384 - Размер данных, при которых произойдет отправка данных. Что бы не делать по одному запросу на сообщение.
- `"linger.ms"` = 1 - 1 мск задержка между отправками запросов. По сути задержка не нужна т.к. консьюмер успевает получить и обработать все сообщения (добавил ради эксперимента)
- `"buffer.memory"` = 33554432 - Аналогично комментарию выше. В бущере у продьюсера не накапливаются сообщения. (добавил ради эксперимента)

## Jaeger
Путь до Jaeger - http://localhost:16686/search

Каждый span в заголовке содержит имя сервиса и ИД сессии. В теле span-а собержится полная информация входящего сообщения

Для того что бы отыскать нужный span необходимо в поле Tags ввести ИД сессии.

## Прочее
Использован RestTemplate для HTTP запросов между сервисами.

Свойства application.properties для `mc1`:
- running.time=10 - Время отправки запросов в секундах
- running.delay.time=1000 - Задержка между отправкой запросов в миллисекундах.

`GET` - http://localhost:8101/start - Для запуска процесса отправки сообщений

`GET` - http://localhost:8101/stop  - Для принудительной остановки процесса отправки сообщений

## Постановка

Создать три взаимодействующих между собой микросервиса `МС1`, `МС2` и `МС3`. 

Микросервисы взаимодействую между собой следующим образом:
1. `МС1` создает сообщение следующего формата:
    ```
    {
      id: integer.
      session_id: integer,
      MC1_timestamp: datetime,
      MC2_timestamp: datetime,
      MC3_timestamp: datetime,
      end_timestamp: datetime
    }
    ```
  где `session_id` - номер сеанса взаимодействия;

  `МС1` записывает в поле `MC1_timestamp` текущее время и отправляет сообщение в `МС2` через WebSocket;
2. `МС2` принимает сообщение от `МС1`, записывает в поле сообщения `МС2_timestamp` текущее время и отправляет сообщение в `МС3` через топик брокера Kafka;
3. `МС3` принимает сообщение от `МС2`, записывает в поле сообщения `МС3_timestamp` текущее время и отправляет сообщение в `МС1` посредством отправки http запроса POST с телом, содержащим сообщение;
4. `МС1` принимает сообщение от `МС3`, записывает в поле `end_timestamp` текущее время, записывает сообщение в базу данных;
5. повторить цикл взаимодействия в течение заданного интервала взаимодействия.


Длительность интервала взаимодействия задается в секундах параметром в конфигурационном файле.

Для целей трассировки микросервисов предусмотреть в микросервисах  отправку span-ов в систему Jaeger для каждого входящего сообщения.

В качестве БД использовать СУБД MariaDB. После остановки контейнеров с микросервисами и окружением база данных должна быть доступна для просмотра средствами СУБД.

Запуск микросервисов и окружения производить в docker-compose.

Старт взаимодействия осуществить отправкой запроса GET на /start/ без параметров в МС1.

Досрочную остановку взаимодействия осуществить отправкой запроса GET на /stop/ без параметров в МС1.

Начало взаимодействия микросервисов индицировать на консоль.

Завершение взаимодействия индицировать на консоль с выводом следующих параметров:
1. время взаимодействия;
2. количество сообщений, сгенерированных во время взаимодействия.

Добавить комментарии к коду и описание функций.
