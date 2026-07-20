INSERT INTO ticket.tickets(id, seat_id, price, status, reserved_by_order_id)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb61', 'd215b5f8-0249-4dc5-89a3-51fd148cfb71', 50.00, 'AVAILABLE', NULL)
ON CONFLICT DO NOTHING;

INSERT INTO ticket.tickets(id, seat_id, price, status, reserved_by_order_id)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb62', 'd215b5f8-0249-4dc5-89a3-51fd148cfb72', 75.00, 'AVAILABLE', NULL)
ON CONFLICT DO NOTHING;

INSERT INTO ticket.tickets(id, seat_id, price, status, reserved_by_order_id)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb63', 'd215b5f8-0249-4dc5-89a3-51fd148cfb73', 100.00, 'AVAILABLE', NULL)
ON CONFLICT DO NOTHING;

INSERT INTO ticket.tickets(id, seat_id, price, status, reserved_by_order_id)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb64', 'd215b5f8-0249-4dc5-89a3-51fd148cfb74', 120.00, 'AVAILABLE', NULL)
ON CONFLICT DO NOTHING;
