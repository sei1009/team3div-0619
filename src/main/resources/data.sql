INSERT INTO users (id, username,password, email,category_id) VALUES 
(1, 'aaa', 'aaa', 'mail',0);

INSERT INTO users (id, username,password, email,category_id) VALUES 
(2, 'bbb', 'bbb', 'email',2);

INSERT INTO request (id,paid,early,absence,late,paid_app,early_app, absence_app,late_app,user_id) VALUES 
(1,1,0,0,0,2,3,4,5,2);

INSERT INTO request (id,paid,early,absence,late,paid_app,early_app, absence_app,late_app,user_id) VALUES 
(2,0,0,1,1,1,2,3,4,1);

INSERT INTO attendance (id,start_time,end_time,date,user_id,request_id) VALUES 
(1,null,null,'2025-06-20',1,2);

INSERT INTO attendance (id,start_time,end_time,date,user_id,request_id) VALUES 
(2,null,null,'2025-06-20',2,1);