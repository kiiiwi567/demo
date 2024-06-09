insert into ticket (name, description, created_on, desired_resolution_date, assignee_id, owner_id, state, category_id, urgency, approver_id) values
('InitTicket1','Description1','2023-01-01','2024-01-01',1,2,'New',1,'Low',3),
('InitTicket2','Description2','2023-01-01','2024-02-02',1,2,'New',1,'Low',3),
('InitTicket3','Description3','2023-01-01','2024-03-03',1,2,'New',1,'Low',3),
('forEmployee1','Description1','2023-01-01','2024-04-01',1,1,'Done',1,'Low',1),
('forManager1','Description1','2023-01-01','2024-05-01',1,3,'In_progress',3,'High',1),
('forManager1','Description1','2023-01-01','2024-06-01',1,1,'New',3,'High',3),
('forManager1','Description1','2023-01-01','2024-07-01',1,1,'Approved',3,'High',3),
('forEngineer1','Description1','2023-01-01','2024-08-01',1,2,'Approved',3,'High',1),
('forEngineer1','Description1','2023-01-01','2024-09-01',5,3,'Done',3,'High',1),
('forManager1AfterF','Description1','2023-01-01','2024-10-01',5,3,'Approved',3,'High',1);
--
--init other info (feedbacks, history, etc.) ONLY FOR CURRENT STATE OF DB
UPDATE ticket
SET description =
        CASE
            WHEN id = 66 THEN 'Не удается найти необходимый модуль для выполнения задачи.'
            WHEN id = 67 THEN 'При попытке создания элемента возникает ошибка, элемент не создается.'
            WHEN id = 68 THEN 'Приложение застревает в бесконечной загрузке после запуска.'
            WHEN id = 69 THEN 'Приложение не совместимо с моим телефоном, возникают проблемы при запуске.'
            WHEN id = 70 THEN 'Не приходят уведомления на мой телефон, необходимо исправить проблему.'
            WHEN id = 71 THEN 'Приложение постоянно запрашивает ввод логина при каждом запуске.'
            WHEN id = 72 THEN 'Приложение часто выходит из строя именно вечером, нужно разобраться в причинах.'
            WHEN id = 73 THEN 'Приложение загружается очень долго, требуется оптимизация процесса загрузки.'
            WHEN id = 74 THEN 'Приложение неожиданно выбрасывает из системы в процессе использования.'
            WHEN id = 75 THEN 'Шаблон, который приходит, кажется испорченным или неправильно отображается.'
            WHEN id = 80 THEN 'Возникают проблемы с входом в систему, не удается выполнить аутентификацию.'
            WHEN id = 82 THEN 'При обработке больших объемов данных возникает ошибка, данные не обрабатываются корректно.'
            WHEN id = 84 THEN 'Приложение выдает непонятную ошибку без каких-либо дополнительных пояснений.'
            WHEN id = 86 THEN 'Получаемые данные кажутся неверными или несоответствующими ожидаемому формату.'
            WHEN id = 87 THEN 'Приложение не совместимо с моим телефоном, не удается запустить его.'
            WHEN id = 88 THEN 'Ожидаемые вычисления или расчеты не выполняются, возникают ошибки при их выполнении.'
            WHEN id = 89 THEN 'При попытке авторизации возникает ошибка, доступ к системе невозможен.'
            WHEN id = 90 THEN 'Отчет, который приходит, имеет неверный формат или неправильно отображается.'
            WHEN id = 91 THEN 'Полученные данные не анализируются или не обрабатываются как ожидалось.'
            WHEN id = 92 THEN 'Строки данных неправильно форматируются или отображаются.'
            WHEN id = 93 THEN 'Полученный список пользователей пуст, необходимо исправить эту проблему.'
            WHEN id = 94 THEN 'Получение формы занимает слишком много времени, процесс должен быть оптимизирован.'
            WHEN id = 95 THEN 'Приложение не может подключиться к серверу, нет доступа к онлайн-сервисам.'
            WHEN id = 96 THEN 'Попытка авторизации неудачна, не удается получить доступ к системе.'
            WHEN id = 97 THEN 'Запрашиваемая страница отсутствует, ошибка 404.'
            WHEN id = 98 THEN 'Ошибка доступа, пользователь не имеет необходимых прав для выполнения операции.'
            WHEN id = 99 THEN 'Ограничение доступа к определенным страницам, не удается получить к ним доступ.'
            WHEN id = 100 THEN 'Полученные данные недостаточны для выполнения необходимых действий.'
            WHEN id = 101 THEN 'Не приходят уведомления на мой телефон, необходимо разобраться в причинах.'
            WHEN id = 102 THEN 'Сервер не отвечает на запросы, нет доступа к онлайн-сервисам.'
            WHEN id = 103 THEN 'Даже небольшие объемы данных анализируются слишком долго, нужно оптимизировать процесс анализа.'
            END
WHERE id IN (66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 80, 82, 84, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103);


WITH RandomDates AS (
    SELECT
        id,
        CASE
            WHEN id BETWEEN 1 AND 7 THEN DATE('2023-11-01') + (RANDOM() * (CURRENT_DATE - DATE('2023-11-01') + 1))::INTEGER
            ELSE DATE('2023-11-01') + (RANDOM() * (CURRENT_DATE - DATE('2023-11-01') + 1))::INTEGER
            END AS random_date
    FROM ticket
)
UPDATE ticket t
SET created_on = rd.random_date
FROM RandomDates rd
WHERE t.id = rd.id;

WITH RandomResolutionDates AS (
    SELECT
        id,
        CASE
            WHEN id BETWEEN 1 AND 7 THEN created_on + INTERVAL '3 days' + (RANDOM() * INTERVAL '11 days')
            ELSE created_on + INTERVAL '3 days' + (RANDOM() * INTERVAL '11 days')
            END AS random_resolution_date
    FROM ticket
)
UPDATE ticket t
SET desired_resolution_date = rd.random_resolution_date
FROM RandomResolutionDates rd
WHERE t.id = rd.id;

UPDATE ticket
SET category_id =
        CASE
            WHEN id IN (66, 72, 95, 102) THEN 1 -- Облачные вычисления
            WHEN id IN (67, 71, 80, 82, 84, 86, 88, 89, 91, 92, 93, 96, 98, 99, 100, 103) THEN 2 -- Анализ данных
            WHEN id IN (68, 73, 70, 101) THEN 5 -- Совместимость с устройствами
            WHEN id IN (75, 94) THEN 3 -- Формирование отчёта
            WHEN id IN (74, 97) THEN 4 -- Предоставление информации
            END
WHERE id IN (66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 80, 82, 84, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103);

UPDATE ticket
SET
    approver_id = CASE
                      WHEN RANDOM() < 0.2 THEN NULL
                      ELSE FLOOR( RANDOM() * 10 ) + 3
        END,
    assignee_id = CASE
                      WHEN RANDOM() < 0.5 THEN NULL
                      ELSE FLOOR( RANDOM() * 6 ) + 5
        END,
    owner_id = CASE
                   WHEN RANDOM() < 0.2 THEN NULL
                   ELSE FLOOR( RANDOM() * 17 ) + 1
        END,
    state = CASE
                WHEN approver_id IS NULL AND assignee_id IS NULL THEN 'New'
                WHEN approver_id IS NOT NULL AND assignee_id IS NULL THEN
                    CASE
                        WHEN desired_resolution_date < CURRENT_DATE THEN 'Declined'
                        ELSE 'Approved'
                        END
                WHEN approver_id IS NOT NULL AND assignee_id IS NOT NULL THEN
                    CASE
                        WHEN desired_resolution_date < CURRENT_DATE THEN 'Cancelled'
                        ELSE 'In_progress'
                        END
        END
WHERE
    category_id BETWEEN 1 AND 5;

INSERT INTO history (ticket_id, "timestamp", "action", user_id, description)
SELECT
    t.id AS ticket_id,
    t.created_on + interval '1 second' * FLOOR(RANDOM() * 86400) AS "timestamp", -- Adding random time
    'Заявка создана' AS "action",
    t.owner_id AS user_id, -- Setting user_id to owner_id from ticket
    'Заявка создана' AS description
FROM ticket t;

-- Step 1: Select 30% of tickets randomly
WITH selected_tickets AS (
    SELECT id
    FROM ticket
    ORDER BY RANDOM()
    LIMIT (SELECT COUNT(*) * 0.3 FROM ticket)
)
-- Step 2: Insert additional history entries for these selected tickets
INSERT INTO history (ticket_id, "timestamp", "action", user_id, description)
SELECT
    t.id AS ticket_id,
    h."timestamp" + interval '1 hour' + interval '1 second' * FLOOR(RANDOM() * 3600) AS "timestamp",
    'Заявка изменена' AS "action",
    t.owner_id AS user_id,
    'Заявка изменена' AS description
FROM ticket t
         JOIN selected_tickets st ON t.id = st.id
         JOIN history h ON h.ticket_id = t.id
WHERE h."action" = 'Заявка создана';


INSERT INTO history (ticket_id, "timestamp", "action", user_id, description)
SELECT
    t.id,
    t.created_on + interval '1 day' + interval '1 second' * FLOOR(RANDOM() * 86400) AS "timestamp",
    'Статус заявки изменён' AS "action",
    t.approver_id AS user_id,
    'Статус заявки изменён с ''Новая'' на ''Одобрена''' AS description
FROM ticket t
WHERE t.state IN ('Approved', 'In_progress', 'Cancelled', 'Done');

-- Insert for Declined state
INSERT INTO history (ticket_id, "timestamp", "action", user_id, description)
SELECT
    t.id,
    t.created_on + interval '1 day' + interval '1 second' * FLOOR(RANDOM() * 86400) AS "timestamp",
    'Статус заявки изменён' AS "action",
    t.approver_id AS user_id,
    'Статус заявки изменён с ''Новая'' на ''Отклонена''' AS description
FROM ticket t
WHERE t.state = 'Declined';

-- Insert for In_progress state (Approved + In Progress)


INSERT INTO history (ticket_id, "timestamp", "action", user_id, description)
SELECT
    t.id,
    t.created_on + interval '2 day' + interval '1 second' * FLOOR(RANDOM() * 86400) AS "timestamp",
    'Статус заявки изменён' AS "action",
    t.assignee_id AS user_id,
    'Статус заявки изменён с ''Одобрена'' на ''В процессе выполнения''' AS description
FROM ticket t
WHERE t.state IN ('In_progress', 'Done');

-- Insert for Cancelled state (Approved + Cancelled)

INSERT INTO history (ticket_id, "timestamp", "action", user_id, description)
SELECT
    t.id,
    t.created_on + interval '2 day' + interval '1 second' * FLOOR(RANDOM() * 86400) AS "timestamp",
    'Статус заявки изменён' AS "action",
    t.assignee_id AS user_id,
    'Статус заявки изменён с ''Одобрена'' на ''Отменена''' AS description
FROM ticket t
WHERE t.state = 'Cancelled';

INSERT INTO history (ticket_id, "timestamp", "action", user_id, description)
SELECT
    t.id,
    t.created_on + interval '3 day' + interval '1 day' * FLOOR(RANDOM() * 5) + interval '1 second' * FLOOR(RANDOM() * 86400) AS "timestamp",
    'Статус заявки изменён' AS "action",
    t.assignee_id AS user_id,
    'Статус заявки изменён с ''В процессе выполнения'' на ''Выполнена''' AS description
FROM ticket t
WHERE t.state = 'Done';

INSERT INTO history (ticket_id, "timestamp", "action", user_id, description)
SELECT
    t.id,
    t.created_on + interval '8 day' + interval '1 day' * FLOOR(RANDOM() * 5) + interval '1 second' * FLOOR(RANDOM() * 86400) AS "timestamp",
    'Выполнение заявки оценено' AS "action",
    t.owner_id AS user_id,
    'Выполнение заявки оценено с комментарием: ' AS description
FROM (
         SELECT
             t.*,
             ROW_NUMBER() OVER (ORDER BY random()) AS rn
         FROM
             ticket t
         WHERE
                 t.state = 'Done'
     ) t
WHERE t.rn % 2 = 0;

INSERT INTO feedback (user_id, rate, "timestamp", "text", ticket_id)
VALUES
    (2, 3, '2023-11-12 13:21:04.000000', 'Хорошо, но долго', 97),
    (3, 3, '2024-01-04 03:38:09.000000', 'Слишком долгое выполнение', 90),
    (3, 4, '2023-12-08 02:44:06.000000', 'Осталось пару неудобств, но в целом ок', 72),
    (4, 4, '2024-02-02 14:43:19.000000', 'Супер, но могло быть чуть получше', 86),
    (2, 5, '2024-03-11 08:01:20.000000', 'Отлично!', 75),
    (2, 5, '2024-01-12 10:58:36.000000', 'Быстро и эффективно, как и нужно!', 70),
    (4, 5, '2023-12-25 10:04:18.000000', 'Вопросов нет', 82),
    (4, 5, '2024-02-26 17:49:38.000000', 'Очень оперативно, спасибо', 91),
    (3, 5, '2024-01-24 10:40:48.000000', 'Отличная работа', 93);












