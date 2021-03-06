DROP TABLE IF EXISTS comments;

CREATE TABLE comments
(
    id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    date TIMESTAMP                                               NOT NULL,
    text VARCHAR                                                 NOT NULL,
    name VARCHAR                                                 NOT NULL
);

INSERT INTO comments
VALUES (DEFAULT,
        '2013-08-04',
        'The worst experience in my life! The color they gave me nothing to do with what I wanted. </br>Bad customer service, I had to go back so they could try to fix what they had done and the owner didn''t even deign to ask me what had happened.',
        'Marco R.');

INSERT INTO comments
VALUES (DEFAULT,
        '16.04.2018',
        'Excellent work and attention, as always. Thank you <3',
        'Janet Lisovsky');

