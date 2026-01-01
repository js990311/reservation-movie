ALTER TABLE `reservation_seats`
    DROP FOREIGN KEY `FK_seats_TO_reservation_seats_1`;

ALTER TABLE `reservation_seats`
    DROP FOREIGN KEY `FK_reservations_TO_reservation_seats_1`;

ALTER TABLE `reservation_seats`
    DROP INDEX `UNIQUE_reservation_seats`;

ALTER TABLE `reservation_seats`
    DROP INDEX `FK_seats_TO_reservation_seats_1`;

ALTER TABLE `reservation_seats`
    DROP COLUMN `seat_id`;

ALTER TABLE `reservation_seats`
    ADD CONSTRAINT `FK_reservations_TO_reservation_seats_1`
        FOREIGN KEY (`reservation_id`)
            REFERENCES `reservations` (`reservation_id`)
            ON DELETE RESTRICT;

ALTER TABLE `reservation_seats`
    ADD COLUMN `screening_seat_id` BIGINT NOT NULL;

ALTER TABLE `reservation_seats`
    ADD CONSTRAINT `FK_screening_seats_TO_reservation_seats_1`
        FOREIGN KEY (`screening_seat_id`)
            REFERENCES `screening_seats` (`screening_seat_id`)
