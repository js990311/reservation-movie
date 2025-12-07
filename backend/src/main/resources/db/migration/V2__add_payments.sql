CREATE TABLE `payment_logs`
(
    `payment_log_id` BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `payment_id`     VARCHAR(255) NOT NULL,
    `reservation_id` BIGINT       NULL,
    `status`         VARCHAR(255) NOT NULL
);

ALTER TABLE `payment_logs`
    ADD CONSTRAINT `FK_reservations_TO_payments`
    FOREIGN KEY (`reservation_id`)
    REFERENCES `reservations` (`reservation_id`);