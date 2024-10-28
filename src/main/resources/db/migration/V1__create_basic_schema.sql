CREATE TABLE order_batches
(
    id          BIGINT PRIMARY KEY,
    external_id VARCHAR(32),
    name        VARCHAR(50)
);

CREATE TABLE orders
(
    id             BIGINT PRIMARY KEY,
    external_id    VARCHAR(32),
    order_batch_id BIGINT REFERENCES order_batches (id),
    first_name     VARCHAR(50),
    last_name      VARCHAR(50),
    mail           VARCHAR(50)
);

CREATE TABLE herbs
(
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(50) UNIQUE
);

CREATE TABLE herb_quantities
(
    order_id BIGINT REFERENCES orders (id),
    herb_id  BIGINT REFERENCES herbs (id),
    quantity INT,
    PRIMARY KEY (order_id, herb_id)
);

CREATE TABLE bills
(
    id             BIGINT PRIMARY KEY,
    order_batch_id BIGINT REFERENCES order_batches (id),
    date           VARCHAR(32),
    vat            INT
);

CREATE TABLE bill_herb_items
(
    bill_id    BIGINT REFERENCES bills (id),
    herb_id    BIGINT REFERENCES herbs (id),
    quantity   INT,
    unit_price INT,
    PRIMARY KEY (bill_id, herb_id)
);

CREATE TABLE shipments
(
    id             BIGINT PRIMARY KEY,
    order_batch_id BIGINT REFERENCES order_batches (id),
    date           VARCHAR(32)
);

CREATE TABLE shipment_herb_items
(
    shipment_id BIGINT REFERENCES shipments (id),
    herb_id     BIGINT REFERENCES herbs (id),
    quantity    INT,
    PRIMARY KEY (shipment_id, herb_id)
);

CREATE SEQUENCE herbs_seq;
CREATE SEQUENCE order_batches_seq;
CREATE SEQUENCE orders_seq;
CREATE SEQUENCE bills_seq;
CREATE SEQUENCE shipments_seq;

CREATE INDEX orders_order_batches_foreign_key_index ON orders (order_batch_id);
CREATE INDEX bills_order_batches_foreign_key_index ON bills (order_batch_id);
CREATE INDEX shipments_order_batches_foreign_key_index ON shipments (order_batch_id);

INSERT INTO herbs VALUES (1, 'Aampachak (Toxigo)');
INSERT INTO herbs VALUES (2, 'Amrutras');
INSERT INTO herbs VALUES (3, 'Anulom');
INSERT INTO herbs VALUES (4, 'Arshna');
INSERT INTO herbs VALUES (5, 'Ashwagandha');
INSERT INTO herbs VALUES (6, 'Asthaloc');
INSERT INTO herbs VALUES (7, 'Ayurcid (Gulkacid)');
INSERT INTO herbs VALUES (8, 'Bliss');
INSERT INTO herbs VALUES (9, 'Brainto');
INSERT INTO herbs VALUES (10, 'Chandraprabha');
INSERT INTO herbs VALUES (11, 'Chandrikaras');
INSERT INTO herbs VALUES (12, 'Cholestrin granules');
INSERT INTO herbs VALUES (13, 'D-Vyro Tablets (Virofight)');
INSERT INTO herbs VALUES (14, 'Diabhar / Madhunil');
INSERT INTO herbs VALUES (15, 'Divyaswasjivan');
INSERT INTO herbs VALUES (16, 'Drustivardhak');
INSERT INTO herbs VALUES (17, 'Easy Detox Tablets / Amrutadi Tablets');
INSERT INTO herbs VALUES (18, 'Energy');
INSERT INTO herbs VALUES (19, 'Gasmukti');
INSERT INTO herbs VALUES (20, 'Granthihar (Fibron)');
INSERT INTO herbs VALUES (21, 'Hartone (Jivan Rakshak)');
INSERT INTO herbs VALUES (22, 'Immuno (Ojas)');
INSERT INTO herbs VALUES (23, 'Jivanshakti');
INSERT INTO herbs VALUES (24, 'Jivanyog');
INSERT INTO herbs VALUES (25, 'K-Tone');
INSERT INTO herbs VALUES (26, 'Kanthasudhar');
INSERT INTO herbs VALUES (27, 'Kaphano Syrup');
INSERT INTO herbs VALUES (28, 'Kumarika');
INSERT INTO herbs VALUES (29, 'Kumariyog');
INSERT INTO herbs VALUES (30, 'Livtone');
INSERT INTO herbs VALUES (31, 'Mahavatnashak');
INSERT INTO herbs VALUES (32, 'Metaboost (Suhruday)');
INSERT INTO herbs VALUES (33, 'Mind Power');
INSERT INTO herbs VALUES (34, 'Navjivan');
INSERT INTO herbs VALUES (35, 'Navkesh Hair oil');
INSERT INTO herbs VALUES (36, 'Niramay');
INSERT INTO herbs VALUES (37, 'Painmukti MJ');
INSERT INTO herbs VALUES (38, 'Painmukti Sandhical');
INSERT INTO herbs VALUES (39, 'Pittashamak');
INSERT INTO herbs VALUES (40, 'Pratishakti');
INSERT INTO herbs VALUES (41, 'Prosto');
INSERT INTO herbs VALUES (42, 'Raktashanti');
INSERT INTO herbs VALUES (43, 'Rasnadi Guggul');
INSERT INTO herbs VALUES (44, 'Rejuliv');
INSERT INTO herbs VALUES (45, 'Sandhiyog');
INSERT INTO herbs VALUES (46, 'Shakti Rasayana');
INSERT INTO herbs VALUES (47, 'Skin Tonic');
INSERT INTO herbs VALUES (48, 'Sugarid');
INSERT INTO herbs VALUES (49, 'Sukanti Freedom Cream');
INSERT INTO herbs VALUES (50, 'Sukesha (Keshiya) Tablets');
INSERT INTO herbs VALUES (51, 'Suniram');
INSERT INTO herbs VALUES (52, 'Supachak Tablets');
INSERT INTO herbs VALUES (53, 'Triphala tab');
INSERT INTO herbs VALUES (54, 'U-Tone (Uritone)');
INSERT INTO herbs VALUES (55, 'Virechan');
