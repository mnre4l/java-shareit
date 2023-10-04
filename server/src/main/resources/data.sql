DELETE
FROM comments;

DELETE
FROM bookings;

DELETE
FROM items;

DELETE
from requests;

DELETE
FROM users;

ALTER TABLE comments
    ALTER COLUMN id
        RESTART WITH 1;

ALTER TABLE users
    ALTER COLUMN id
        RESTART WITH 1;

ALTER TABLE items
    ALTER COLUMN id
        RESTART WITH 1;

ALTER TABLE bookings
    ALTER COLUMN id
        RESTART WITH 1;

ALTER TABLE requests
    ALTER COLUMN id
        RESTART WITH 1;