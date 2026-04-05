DELETE FROM ent_order_sync_result WHERE 1=1;
DELETE FROM ent_order_sync WHERE 1=1;

-- Normal items
INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-001', 'Alice Kim', 15000.00, 'CONFIRMED');
INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-002', 'Bob Park', 23500.50, 'CONFIRMED');

-- Item 3: negative amount -> DataIntegrityViolationException in processor
INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-003', 'Charlie Lee', -500.00, 'CONFIRMED');

INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-004', 'Diana Choi', 8900.00, 'CONFIRMED');
INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-005', 'Eve Jung', 42000.00, 'CONFIRMED');

-- Item 6: empty string customer_name -> IllegalArgumentException in processor
INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-006', '', 12000.00, 'CONFIRMED');

INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-007', 'Grace Han', 67800.00, 'CONFIRMED');

-- Item 8: zero amount -> IllegalArgumentException in processor (skip)
INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-008', 'Henry Yoon', 0.00, 'CONFIRMED');

INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-009', 'Iris Shin', 31200.00, 'CONFIRMED');

-- Item 10: amount exceeds business limit -> DataIntegrityViolationException (4th skip, exceeds skipLimit(3))
INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-010', 'Jack Oh', 9999999.99, 'CONFIRMED');

INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-011', 'Kate Ryu', 18700.00, 'CONFIRMED');
INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-012', 'Leo Moon', 24300.00, 'CONFIRMED');

