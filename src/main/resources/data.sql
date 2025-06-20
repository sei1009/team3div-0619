INSERT INTO users (id, username,password, email,category_id) VALUES 
(1, 'aaa', 'aaa', 'mail',0);

INSERT INTO request (id,paid,early,absence,late,paid_app,early_app, absence_app,late_app) VALUES 
(1,2,3,4,5,6,7,8,9);


INSERT INTO attendance (id,start_time,end_time,date,userid,requestid) VALUES 
(1,null,null,'2025-06-20',1,1);
