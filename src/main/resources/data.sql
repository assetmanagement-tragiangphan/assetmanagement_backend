INSERT INTO ROLE(ID,STATUS,NAME) VALUES(1, 'ACTIVE', 'ADMIN');
INSERT INTO ROLE(ID,STATUS,NAME) VALUES(2, 'ACTIVE', 'STAFF');

INSERT INTO _USER
(ID,STATUS, CREATED_ON, UPDATED_ON, DATE_OF_BIRTH, FIRST_NAME, GENDER, IS_CHANGE_PASSWORD, JOINED_DATE, LAST_NAME, LOCATION, PASSWORD, STAFF_CODE, USERNAME, CREATED_BY, UPDATED_BY, ROLE_ID)
VALUES(1,'ACTIVE', '2024-01-01 00:00:00.000', '2024-06-17 15:42:35.762', '2002-06-15', 'Thanh', 'MALE', true, '2024-06-17', 'Pham', 'HCM', '$2a$10$fOUx8r8ZunnqGDE.ASWV4epMH.z/5APxmJ.gmdF3YXCtf47IC2QjG', 'SD0001', 'thanhpt', NULL, 1, 1);

INSERT INTO _USER (created_by, date_of_birth, id, is_change_password, joined_date, role_id, updated_by, created_on, updated_on, first_name, gender, last_name, location, password, staff_code, status, username)
VALUES (1, '2000-01-01', 7, true, '2024-07-01', 1, 7, '2024-07-01 03:08:47.402056', '2024-07-01 04:25:21.155645', 'Nguyen', 'MALE', 'Pham', 'HCM', '$2a$10$4wgOZVdDMV9PSXxGC1RKieyHhc2sL2Vx.zJTa34M7DujckEBavHBK', 'SD0007', 'ACTIVE', 'nguyenp');


