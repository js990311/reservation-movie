export type PaymentStatus =
    'READY' |
    'VERIFYING' |
    'PAID' |
    'ABORTED' |
    'TIMEOUT'
;

export type PaymentCancelStatus = 'REQUIRED' | 'CANCELED' | 'FAILED' | 'SKIPPED';