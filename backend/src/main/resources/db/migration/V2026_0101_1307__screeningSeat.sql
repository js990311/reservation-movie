create table screening_seats (
    screening_seat_id bigint not null auto_increment,
    price integer,
    screening_id bigint,
    seat_id bigint,
    status enum('AVAILABLE', 'RESERVED'),
    created_at datetime(6),
    updated_at datetime(6),
    deleted_at datetime(6),
    primary key (screening_seat_id)
);

alter table screening_seats add constraint FK_screening_TO_screening_seat
    foreign key (screening_id)
        references screenings (screening_id);

alter table screening_seats add constraint FK_seat_screening_seat
    foreign key (seat_id)
        references seats (seat_id);