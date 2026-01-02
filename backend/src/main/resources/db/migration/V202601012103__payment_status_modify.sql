ALTER TABLE payments MODIFY COLUMN status VARCHAR(20) NOT NULL;

ALTER TABLE payments ADD CONSTRAINT chk_payments_status
    CHECK (status IN ('READY', 'TIMEOUT', 'VERIFYING', 'PAID', 'ABORTED'));

ALTER TABLE payment_cancels MODIFY COLUMN status VARCHAR(20) NOT NULL;

ALTER TABLE payment_cancels ADD CONSTRAINT chk_payment_cancels_status
    CHECK (status IN ('REQUIRED', 'CANCELED', 'FAILED', 'SKIPPED'));