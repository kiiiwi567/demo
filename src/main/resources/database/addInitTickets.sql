insert into ticket values
(1,'InitTicket1','Description1','2023-01-01','2024-01-01',1,2,'New',1,'Low',3),
(2,'InitTicket2','Description2','2023-01-01','2024-02-02',1,2,'New',1,'Low',3),
(3,'InitTicket3','Description3','2023-01-01','2024-03-03',1,2,'New',1,'Low',3);

insert into ticket values
(4,'forEmployee1','Description1','2023-01-01','2024-04-01',1,1,'Done',1,'Low',1),
(5,'forManager1','Description1','2023-01-01','2024-05-01',1,3,'In_progress',3,'High',1),
(6,'forManager1','Description1','2023-01-01','2024-06-01',1,1,'New',3,'High',3),
(7,'forManager1','Description1','2023-01-01','2024-07-01',1,1,'Approved',3,'High',3),
(8,'forEngineer1','Description1','2023-01-01','2024-08-01',1,2,'Approved',3,'High',1),
(9,'forEngineer1','Description1','2023-01-01','2024-09-01',5,3,'Done',3,'High',1);

UPDATE ticket
SET urgency = CASE
                                  WHEN id = 1 THEN 'Low'
                                  WHEN id = 2 THEN 'Average'
                                  WHEN id = 3 THEN 'High'
                                  WHEN id = 4 THEN 'Critical'
                                  WHEN id = 5 THEN 'Low'
                                  WHEN id = 6 THEN 'Average'
                                  WHEN id = 7 THEN 'High'
                                  WHEN id = 8 THEN 'Critical'
                                  ELSE urgency
    END;