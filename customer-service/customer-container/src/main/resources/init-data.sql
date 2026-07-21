INSERT INTO customer.customers(id, username, password, first_name, last_name, role)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb41',
        'user_1',
        '$2a$10$XIrEyY6Hu9AhSkfvRa7C2.Pg1Z2yoyRETpNd0Fy7jeG1Jj4WCOarm',
        'First',
        'User',
        'CUSTOMER')
ON CONFLICT DO NOTHING;

INSERT INTO customer.customers(id, username, password, first_name, last_name, role)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb42',
        'user_2',
        '$2a$10$XIrEyY6Hu9AhSkfvRa7C2.Pg1Z2yoyRETpNd0Fy7jeG1Jj4WCOarm',
        'Second',
        'User',
        'CUSTOMER')
ON CONFLICT DO NOTHING;

REFRESH MATERIALIZED VIEW customer.order_customer_m_view;
