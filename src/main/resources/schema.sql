-- role table
CREATE TABLE IF NOT EXISTS ROLE
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(255) NULL,
    status VARCHAR(255) NULL,
    CONSTRAINT role_name_check CHECK (name IN ('ADMIN', 'STAFF')),
    CONSTRAINT role_status_check CHECK (
            status IN (
                       'ACTIVE', 'INACTIVE', 'AVAILABLE', 'NOT_AVAILABLE',
                       'ASSIGNED', 'WAITING_FOR_RECYCLING', 'RECYCLED',
                       'ACCEPTED', 'WAITING_FOR_ACCEPTANCE', 'DECLINED',
                       'COMPLETED', 'WAITING_FOR_RETURNING'
            )
        )
);


-- _user table
CREATE TABLE IF NOT EXISTS _USER
(
    created_by         INT,
    date_of_birth      DATE         NULL,
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    is_change_password BOOLEAN      NULL,
    joined_date        DATE         NULL,
    role_id            INT,
    updated_by         INT,
    created_on         TIMESTAMP    NOT NULL,
    updated_on         TIMESTAMP    NULL,
    first_name         VARCHAR(255) NULL,
    gender             VARCHAR(255) NULL,
    last_name          VARCHAR(255) NULL,
    location           VARCHAR(255) NULL,
    password           VARCHAR(255) NULL,
    username           VARCHAR(255) NULL,
    staff_code         VARCHAR(255) NULL,
    status             VARCHAR(255) NULL,
    CONSTRAINT _user_gender_check CHECK (gender IN ('MALE', 'FEMALE')),
    CONSTRAINT _user_location_check CHECK (location IN ('HCM', 'DN', 'HN')),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES _USER (id),
    CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES ROLE (id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES _USER (id)
);

-- category table
CREATE TABLE IF NOT EXISTS CATEGORY
(
    created_by INT,
    id         INT AUTO_INCREMENT PRIMARY KEY,
    updated_by INT,
    created_on TIMESTAMP    NOT NULL,
    updated_on TIMESTAMP    NULL,
    name       VARCHAR(255) NULL,
    prefix     VARCHAR(255) NULL,
    status     VARCHAR(255) NULL,
    CONSTRAINT category_name_key UNIQUE (name),
    CONSTRAINT category_prefix_key UNIQUE (prefix),
    CONSTRAINT category_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'AVAILABLE', 'NOT_AVAILABLE', 'ASSIGNED',
                                                       'WAITING_FOR_RECYCLING', 'RECYCLED', 'ACCEPTED',
                                                       'WAITING_FOR_ACCEPTANCE', 'DECLINED', 'COMPLETED',
                                                       'WAITING_FOR_RETURNING')),
    CONSTRAINT fk_created_by_category FOREIGN KEY (created_by) REFERENCES _USER (id),
    CONSTRAINT fk_updated_by_category FOREIGN KEY (updated_by) REFERENCES _USER (id)
);

-- token table
CREATE TABLE IF NOT EXISTS TOKEN
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    status  VARCHAR(255) NULL,
    token   VARCHAR(255) NULL,
    CONSTRAINT token_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'AVAILABLE', 'NOT_AVAILABLE', 'ASSIGNED',
                                                    'WAITING_FOR_RECYCLING', 'RECYCLED', 'ACCEPTED',
                                                    'WAITING_FOR_ACCEPTANCE', 'DECLINED', 'COMPLETED',
                                                    'WAITING_FOR_RETURNING')),
    CONSTRAINT fk_user_id_token FOREIGN KEY (user_id) REFERENCES _USER (id)
);

-- asset table
CREATE TABLE IF NOT EXISTS ASSET
(
    category_id    INT,
    created_by     INT,
    id             INT AUTO_INCREMENT PRIMARY KEY,
    installed_date DATE         NULL,
    updated_by     INT,
    created_on     TIMESTAMP    NOT NULL,
    updated_on     TIMESTAMP    NULL,
    specification  VARCHAR(300) NULL,
    asset_code     VARCHAR(255) NULL,
    location       VARCHAR(255) NULL,
    name           VARCHAR(255) NULL,
    status         VARCHAR(255) NULL,
    CONSTRAINT asset_asset_code_key UNIQUE (asset_code),
    CONSTRAINT asset_location_check CHECK (location IN ('HCM', 'DN', 'HN')),
    CONSTRAINT asset_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'AVAILABLE', 'NOT_AVAILABLE', 'ASSIGNED',
                                                    'WAITING_FOR_RECYCLING', 'RECYCLED', 'ACCEPTED',
                                                    'WAITING_FOR_ACCEPTANCE', 'DECLINED', 'COMPLETED',
                                                    'WAITING_FOR_RETURNING')),
    CONSTRAINT fk_category_id_asset FOREIGN KEY (category_id) REFERENCES CATEGORY (id),
    CONSTRAINT fk_created_by_asset FOREIGN KEY (created_by) REFERENCES _USER (id),
    CONSTRAINT fk_updated_by_asset FOREIGN KEY (updated_by) REFERENCES _USER (id)
);

-- assignment table
CREATE TABLE IF NOT EXISTS ASSIGNMENT
(
    asset_id      INT,
    assigned_date DATE         NULL,
    assignee_id   INT,
    created_by    INT,
    id            INT AUTO_INCREMENT PRIMARY KEY,
    updated_by    INT,
    created_on    TIMESTAMP    NOT NULL,
    updated_on    TIMESTAMP    NULL,
    note          VARCHAR(300) NULL,
    status        VARCHAR(255) NULL,
    CONSTRAINT assignment_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'AVAILABLE', 'NOT_AVAILABLE', 'ASSIGNED',
                                                         'WAITING_FOR_RECYCLING', 'RECYCLED', 'ACCEPTED',
                                                         'WAITING_FOR_ACCEPTANCE', 'DECLINED', 'COMPLETED',
                                                         'WAITING_FOR_RETURNING')),
    CONSTRAINT fk_assignee_id_assignment FOREIGN KEY (assignee_id) REFERENCES _USER (id),
    CONSTRAINT fk_created_by_assignment FOREIGN KEY (created_by) REFERENCES _USER (id),
    CONSTRAINT fk_updated_by_assignment FOREIGN KEY (updated_by) REFERENCES _USER (id),
    CONSTRAINT fk_asset_id_assignment FOREIGN KEY (asset_id) REFERENCES ASSET (id)
);

-- return_request table
CREATE TABLE IF NOT EXISTS RETURN_REQUEST
(
    accepted_by   INT,
    assignment_id INT UNIQUE,
    id            INT AUTO_INCREMENT PRIMARY KEY,
    returned_date DATE         NULL,
    status        VARCHAR(255) NULL,
    requested_by  INT,
    updated_on    TIMESTAMP    NULL,
    created_by    INT,
    updated_by    INT,
    created_on    TIMESTAMP    NOT NULL,
    CONSTRAINT return_request_status_check CHECK (status IN
                                                  ('ACTIVE', 'INACTIVE', 'AVAILABLE', 'NOT_AVAILABLE', 'ASSIGNED',
                                                   'WAITING_FOR_RECYCLING', 'RECYCLED', 'ACCEPTED',
                                                   'WAITING_FOR_ACCEPTANCE', 'DECLINED', 'COMPLETED',
                                                   'WAITING_FOR_RETURNING')),
    CONSTRAINT fk_accepted_by_return_request FOREIGN KEY (accepted_by) REFERENCES _USER (id),
    CONSTRAINT fk_assignment_id_return_request FOREIGN KEY (assignment_id) REFERENCES ASSIGNMENT (id),
    CONSTRAINT fk_updated_by_return_request FOREIGN KEY (updated_by) REFERENCES _USER (id),
    CONSTRAINT fk_created_by_return_request FOREIGN KEY (created_by) REFERENCES _USER (id),
    CONSTRAINT fk_requested_by_return_request FOREIGN KEY (requested_by) REFERENCES _USER (id)
);