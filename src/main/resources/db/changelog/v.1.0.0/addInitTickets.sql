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

