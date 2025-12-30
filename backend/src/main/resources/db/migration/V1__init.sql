CREATE TABLE `seats` (
                         `seat_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
                         `row_num`	INT	NOT NULL,
                         `col_num`	INT	NOT NULL,
                         `theater_id`	BIGINT	NOT NULL,
                         created_at datetime(6),
                         updated_at datetime(6),
                         deleted_at datetime(6)
);

CREATE TABLE `users` (
                         `user_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
                         `email`	VARCHAR(255)	NOT NULL,
                         `password`	varchar(255)	NOT NULL,
                         `name`	VARCHAR(255)	NOT NULL,
                         `role`	VARCHAR(255)	NOT NULL,
                         created_at datetime(6),
                         updated_at datetime(6),
                         deleted_at datetime(6)
);

CREATE TABLE `reservations` (
                                `reservation_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
                                `status`	enum('PENDING','CONFIRMED','CANCELED','COMPLETED')	NOT NULL,
                                `screening_id`	BIGINT	NOT NULL,
                                `user_id`	BIGINT	NOT NULL,
                                `total_amount` INTEGER,
                                created_at datetime(6),
                                updated_at datetime(6),
                                deleted_at datetime(6)
);

CREATE TABLE `screenings` (
                              `screening_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
                              `start_time`	datetime(6)	NOT NULL,
                              `end_time` datetime(6) NOT NULL,
                              `theater_id`	BIGINT	NOT NULL,
                              `movie_id`	BIGINT	NOT NULL,
                              created_at datetime(6),
                              updated_at datetime(6),
                              deleted_at datetime(6)
);

CREATE TABLE `movies` (
                          `movie_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
                          `title`	VARCHAR(255)	NOT NULL,
                          `duration`	INT	NOT NULL,
                          created_at datetime(6),
                          updated_at datetime(6),
                              deleted_at datetime(6)

);

CREATE TABLE `theaters` (
                            `theater_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
                            `name`	VARCHAR(255)	NOT NULL,
                            `col_size` INTEGER,
                            `row_size` INTEGER,
                            created_at datetime(6),
                            updated_at datetime(6),
                                deleted_at datetime(6)
);

-- 결제 관련
create table payments
(
    payment_id     bigint not null AUTO_INCREMENT,
    created_at     datetime(6),
    updated_at     datetime(6),
    deleted_at datetime(6),
        payment_uid    CHAR(13),
    reservation_id bigint,
    status         enum ('PAID', 'VERIFYING', 'READY', 'ABORTED', 'FAILED'),
    primary key (payment_id)
);

create table payment_cancels (
     payment_cancel_id bigint not null AUTO_INCREMENT,
     created_at datetime(6),
     updated_at datetime(6),
     deleted_at datetime(6),
     payment_uid CHAR(13),
     payment_id bigint,
     reason enum ('CUSTOMER_REQUEST','VALIDATION_FAILED'),
     status enum ('CANCELED','REQUIRED', 'FAILED'),
     primary key (payment_cancel_id)
);

CREATE TABLE `reservation_seats` (
                                     `reservation_seat_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY ,
                                     `reservation_id`	BIGINT	NOT NULL,
                                     `seat_id`	BIGINT	NOT NULL,
                                     created_at datetime(6),
                                     updated_at datetime(6),
                                     deleted_at datetime(6)
);

-- 외래키 매핑
ALTER TABLE `seats` ADD CONSTRAINT `FK_theaters_TO_seats_1` FOREIGN KEY (
                                                                         `theater_id`
    )
    REFERENCES `theaters` (
                           `theater_id`
        ) ON DELETE RESTRICT ;

ALTER TABLE `reservations` ADD CONSTRAINT `FK_screenings_TO_reservations_1` FOREIGN KEY (
                                                                                         `screening_id`
    )
    REFERENCES `screenings` (
                             `screening_id`
        ) ON DELETE RESTRICT ;

ALTER TABLE `reservations` ADD CONSTRAINT `FK_users_TO_reservations_1` FOREIGN KEY (
                                                                                    `user_id`
    )
    REFERENCES `users` (
                        `user_id`
        ) ON DELETE RESTRICT ;

ALTER TABLE `screenings` ADD CONSTRAINT `FK_theaters_TO_screenings_1` FOREIGN KEY (
                                                                                   `theater_id`
    )
    REFERENCES `theaters` (
                           `theater_id`
        ) ON DELETE RESTRICT ;

ALTER TABLE `screenings` ADD CONSTRAINT `FK_movies_TO_screenings_1` FOREIGN KEY (
                                                                                 `movie_id`
    )
    REFERENCES `movies` (
                         `movie_id`
        ) ON DELETE RESTRICT ;

ALTER TABLE `reservation_seats` ADD CONSTRAINT `FK_reservations_TO_reservation_seats_1` FOREIGN KEY (
                                                                                                     `reservation_id`
    )
    REFERENCES `reservations` (
                               `reservation_id`
        ) ON DELETE RESTRICT ;

ALTER TABLE `reservation_seats` ADD CONSTRAINT `FK_seats_TO_reservation_seats_1` FOREIGN KEY (
                                                                                              `seat_id`
    )
    REFERENCES `seats` (
                        `seat_id`
        ) ON DELETE RESTRICT ;

ALTER TABLE `reservation_seats` ADD CONSTRAINT `UNIQUE_reservation_seats` UNIQUE (
                                                                                  `reservation_id`, `seat_id`
    );

ALTER TABLE `seats` ADD CONSTRAINT `UNIQUE_seats` UNIQUE (
                                                          `row_num`, `col_num`, `theater_id`
    );

ALTER TABLE `payments` ADD CONSTRAINT `UNIQUE_payments` UNIQUE (
                                                                `payment_uid`
    );

ALTER TABLE `payment_cancels` ADD CONSTRAINT `UNIQUE_payment_cancels` UNIQUE (
                                                                              `payment_uid`
    );

ALTER TABLE `payments` ADD CONSTRAINT `FK_reservations_TO_payments` FOREIGN KEY (
                                                                                 `reservation_id`
    ) REFERENCES `reservations` (`reservation_id`) ON DELETE RESTRICT ;

ALTER TABLE `payment_cancels` ADD CONSTRAINT `FK_payment_TO_payment_cancels` FOREIGN KEY (
                                                                                          `payment_id`
    ) REFERENCES `payments` (`payment_id`) ON DELETE RESTRICT ;