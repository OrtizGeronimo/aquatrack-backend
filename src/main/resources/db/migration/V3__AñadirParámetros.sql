INSERT INTO ubicacion(id, latitud, longitud)
VALUES (1, -32.94558445730581, -68.81470070748412),
       (2, -32.907671633244725, -68.76276409641262),
       (3, -32.9252190419355, -68.72704073535638),
       (4, -32.866800337392995, -68.89017292046799);

INSERT INTO empresa(id, direccion, email, fecha_creacion, hora_generacion_reparto, nombre, num_telefono, url,
                    ubicacion_id)
VALUES (1, 'Severo del Castillo 1520', 'saura@gmail.com', CURRENT_DATE, '18:00:00.000000', 'Saura SA', '2614193591',
        'http://saurasa.com', 3),
       (2, 'Barrio Santa Ana', 'gero@gmail.com', CURRENT_DATE, '19:15:00.000000', 'Gerardo Soderos', '2613219806',
        'http://gersodas.com', 2),
       (3, 'Barrio Los Frutales', 'carrion@gmail.com', CURRENT_DATE, '16:00:00.000000', 'Carrión Aguas', '2613459654',
        'http://caraguas.com', 1),
       (4, 'Barrio Dalvian', 'juarroz@gmail.com', CURRENT_DATE, '15:00:00.000000', 'Distribuidora Juarroz',
        '2612314562', 'http://distjuarroz.com', 4);

INSERT INTO dia_semana(id, nombre)
VALUES (1, 'Lunes'),
       (2, 'Martes'),
       (3, 'Miercoles'),
       (4, 'Jueves'),
       (5, 'Viernes'),
       (6, 'Sabado'),
       (7, 'Domingo');

INSERT INTO estado_cliente(id, nombre_estado_cliente)
VALUES (1, 'En proceso de creación'),
       (2, 'Habilitado'),
       (3, 'Deshabilitado');

INSERT INTO estado_usuario(id, nombre_estado_usuario)
VALUES (1, 'En proceso de creación'),
       (2, 'Habilitado'),
       (3, 'Deshabilitado');

INSERT INTO estado_entrega(id, nombre_estado_entrega)
VALUES (1, 'Programada'),
       (2, 'Pendiente'),
       (3, 'Procesada'),
       (4, 'Ausente'),
       (5, 'Cancelada');

INSERT INTO estado_pago(id, nombre)
VALUES (1, 'Pendiente'),
       (2, 'Rechazado'),
       (3, 'Aprobado');

INSERT INTO estado_pedido(id, nombre_estado_pedido)
VALUES (1, 'Pendiente'),
       (2, 'Aprobado'),
       (3, 'Rechazado');

INSERT INTO estado_reparto(id, nombre)
VALUES (1, 'Pendiente de Asignación'),
       (2, 'Pendiente de Ejecución'),
       (3, 'En Ejecución'),
       (4, 'Incompleto'),
       (5, 'Cancelado'),
       (6, 'Finalizado');

INSERT INTO medio_pago(id, nombre)
VALUES (1, 'Efectivo'),
       (2, 'Tarjeta de débito'),
       (3, 'Tarjeta de crédito'),
       (4, 'Transferencia bancaria'),
       (5, 'Mercado Pago');


INSERT INTO tipo_empleado(id, nombre)
VALUES (1, 'Oficinista'),
       (2, 'Repartidor');

INSERT INTO tipo_pedido(id, nombre_tipo_pedido)
VALUES (1, 'Atípico');

INSERT INTO usuario (id, contraseña, direccion_email, fecha_creacion, fecha_fin_vigencia, validado)
VALUES (1, '$2a$12$QxJltIw5pIXkdc8LzTck4O.4nZsMf2WjM4jsDm99JbMidBLtlFbrS', 'geroortiz@gmail.com', CURRENT_DATE, NULL,
        b'1'),
       (2, '$2a$12$QxJltIw5pIXkdc8LzTck4O.4nZsMf2WjM4jsDm99JbMidBLtlFbrS', 'nicosaurina@gmail.com', CURRENT_DATE, NULL,
        b'1'),
       (3, '$2a$12$a/uNsJUletX9QpjDrT4tRe3hFQUWKpI6rp8nqe6uaPz9KxWDIqW6i', 'martijuarroz@gmail.com', CURRENT_DATE, NULL,
        b'1'),
       (4, '$2a$12$gcZud6yzQV3PcXE/CMUUD.VhWZunoYgPQNl7/k5LaVDZDX/rhSgh2', 'martincarrion@gmail.com', CURRENT_DATE,
        CURRENT_DATE, b'1');

INSERT INTO rol (id, fecha_creacion, fecha_fin_vigencia, nombre, empresa_id)
VALUES (1, CURRENT_DATE, NULL, 'Administrador', 1),
       (2, CURRENT_DATE, NULL, 'Cliente', null),
       (3, CURRENT_DATE, NULL, 'Repartidor', 1),
       (4, CURRENT_DATE, NULL, 'Oficinista', 1),
       (5, CURRENT_DATE, NULL, 'Administrador', 2),
       (7, CURRENT_DATE, NULL, 'Repartidor', 2),
       (8, CURRENT_DATE, NULL, 'Administrador', 3),
       (10, CURRENT_DATE, NULL, 'Repartidor', 3),
       (11, CURRENT_DATE, NULL, 'Administrador', 4),
       (13, CURRENT_DATE, NULL, 'Repartidor', 4),
       (14, CURRENT_DATE, NULL, 'Oficinista', 2),
       (15, CURRENT_DATE, NULL, 'Oficinista', 3),
       (16, CURRENT_DATE, NULL, 'Oficinista', 4);

INSERT INTO permiso_rol(id, permiso_id, rol_id)
values (1, 1, 1),
       (2, 2, 1),
       (3, 3, 1),
       (4, 4, 1),
       (5, 5, 1),
       (6, 6, 1),
       (7, 7, 1),
       (8, 8, 1),
       (9, 9, 1),
       (10, 10, 1),
       (11, 11, 1),
       (12, 12, 1),
       (13, 13, 1),
       (14, 14, 1),
       (15, 15, 1),
       (16, 16, 1),
       (17, 17, 1),
       (18, 18, 1),
       (19, 19, 1),
       (20, 20, 1),
       (21, 21, 1),
       (22, 22, 1),
       (23, 23, 1),
       (24, 24, 1),
       (25, 25, 1),
       (26, 26, 1),
       (27, 27, 1),
       (28, 28, 1),
       (29, 29, 1),
       (30, 30, 1),
       (31, 31, 1),
       (32, 32, 1),
       (33, 33, 1),
       (34, 34, 1),
       (35, 35, 1),
       (36, 36, 1),
       (37, 37, 1),
       (38, 38, 1),
       (39, 39, 1),
       (40, 40, 1),
       (41, 41, 1),
       (42, 42, 1),
       (43, 43, 1),
       (44, 44, 1),
       (45, 45, 1),
       (46, 46, 1),
       (47, 47, 1),
       (48, 48, 1),
       (49, 49, 1),
       (50, 50, 1),
       (51, 1, 5),
       (52, 2, 5),
       (53, 3, 5),
       (54, 4, 5),
       (55, 5, 5),
       (56, 6, 5),
       (57, 7, 5),
       (58, 8, 5),
       (59, 9, 5),
       (60, 10, 5),
       (61, 11, 5),
       (62, 12, 5),
       (63, 13, 5),
       (64, 14, 5),
       (65, 15, 5),
       (66, 16, 5),
       (67, 17, 5),
       (68, 18, 5),
       (69, 19, 5),
       (70, 20, 5),
       (71, 21, 5),
       (72, 22, 5),
       (73, 23, 5),
       (74, 24, 5),
       (75, 25, 5),
       (76, 26, 5),
       (77, 27, 5),
       (78, 28, 5),
       (79, 29, 5),
       (80, 30, 5),
       (81, 31, 5),
       (82, 32, 5),
       (83, 33, 5),
       (84, 34, 5),
       (85, 35, 5),
       (86, 36, 5),
       (87, 37, 5),
       (88, 38, 5),
       (89, 39, 5),
       (90, 40, 5),
       (91, 41, 5),
       (92, 42, 5),
       (93, 43, 5),
       (94, 44, 5),
       (95, 45, 5),
       (96, 46, 5),
       (97, 47, 5),
       (98, 48, 5),
       (99, 49, 5),
       (100, 50, 5),
       (101, 1, 8),
       (102, 2, 8),
       (103, 3, 8),
       (104, 4, 8),
       (105, 5, 8),
       (106, 6, 8),
       (107, 7, 8),
       (108, 8, 8),
       (109, 9, 8),
       (110, 10, 8),
       (111, 11, 8),
       (112, 12, 8),
       (113, 13, 8),
       (114, 14, 8),
       (115, 15, 8),
       (116, 16, 8),
       (117, 17, 8),
       (118, 18, 8),
       (119, 19, 8),
       (120, 20, 8),
       (121, 21, 8),
       (122, 22, 8),
       (123, 23, 8),
       (124, 24, 8),
       (125, 25, 8),
       (126, 26, 8),
       (127, 27, 8),
       (128, 28, 8),
       (129, 29, 8),
       (130, 30, 8),
       (131, 31, 8),
       (132, 32, 8),
       (133, 33, 8),
       (134, 34, 8),
       (135, 35, 8),
       (136, 36, 8),
       (137, 37, 8),
       (138, 38, 8),
       (139, 39, 8),
       (140, 40, 8),
       (141, 41, 8),
       (142, 42, 8),
       (143, 43, 8),
       (144, 44, 8),
       (145, 45, 8),
       (146, 46, 8),
       (147, 47, 8),
       (148, 48, 8),
       (149, 49, 8),
       (150, 50, 8),
       (151, 1, 11),
       (152, 2, 11),
       (153, 3, 11),
       (154, 4, 11),
       (155, 5, 11),
       (156, 6, 11),
       (157, 7, 11),
       (158, 8, 11),
       (159, 9, 11),
       (160, 10, 11),
       (161, 11, 11),
       (162, 12, 11),
       (163, 13, 11),
       (164, 14, 11),
       (165, 15, 11),
       (166, 16, 11),
       (167, 17, 11),
       (168, 18, 11),
       (169, 19, 11),
       (170, 20, 11),
       (171, 21, 11),
       (172, 22, 11),
       (173, 23, 11),
       (174, 24, 11),
       (175, 25, 11),
       (176, 26, 11),
       (177, 27, 11),
       (178, 28, 11),
       (179, 29, 11),
       (180, 30, 11),
       (181, 31, 11),
       (182, 32, 11),
       (183, 33, 11),
       (184, 34, 11),
       (185, 35, 11),
       (186, 36, 11),
       (187, 37, 11),
       (188, 38, 11),
       (189, 39, 11),
       (190, 40, 11),
       (191, 41, 11),
       (192, 42, 11),
       (193, 43, 11),
       (194, 44, 11),
       (195, 45, 11),
       (196, 46, 11),
       (197, 47, 11),
       (198, 48, 11),
       (199, 49, 11),
       (200, 50, 11),
       (201, 5, 14),
       (202, 6, 14),
       (203, 7, 14),
       (204, 8, 14),
       (205, 13, 14),
       (206, 14, 14),
       (207, 15, 14),
       (208, 16, 14),
       (209, 17, 14),
       (210, 18, 14),
       (211, 19, 14),
       (212, 20, 14),
       (213, 21, 14),
       (214, 22, 14),
       (215, 23, 14),
       (216, 24, 14),
       (217, 25, 14),
       (218, 26, 14),
       (219, 27, 14),
       (220, 28, 14),
       (221, 29, 14),
       (222, 30, 14),
       (223, 31, 14),
       (224, 32, 14),
       (225, 5, 15),
       (226, 6, 15),
       (227, 7, 15),
       (228, 8, 15),
       (229, 13, 15),
       (230, 14, 15),
       (231, 15, 15),
       (232, 16, 15),
       (233, 17, 15),
       (234, 18, 15),
       (235, 19, 15),
       (236, 20, 15),
       (237, 21, 15),
       (238, 22, 15),
       (239, 23, 15),
       (240, 24, 15),
       (241, 25, 15),
       (242, 26, 15),
       (243, 27, 15),
       (244, 28, 15),
       (245, 29, 15),
       (246, 30, 15),
       (247, 31, 15),
       (248, 32, 15),
       (249, 5, 16),
       (250, 6, 16),
       (251, 7, 16),
       (252, 8, 16),
       (253, 13, 16),
       (254, 14, 16),
       (255, 15, 16),
       (256, 16, 16),
       (257, 17, 16),
       (258, 18, 16),
       (259, 19, 16),
       (260, 20, 16),
       (261, 21, 16),
       (262, 22, 16),
       (263, 23, 16),
       (264, 24, 16),
       (265, 25, 16),
       (266, 26, 16),
       (267, 27, 16),
       (268, 28, 16),
       (269, 29, 16),
       (270, 30, 16),
       (271, 31, 16),
       (272, 32, 16);

insert into rol_usuario(id, rol_id, usuario_id)
values (1, 14, 1),
       (2, 5, 1),
       (3, 1, 2),
       (4, 3, 2),
       (5, 8, 3),
       (6, 10, 3),
       (7, 11, 4),
       (8, 16, 4);

INSERT INTO empleado (id, apellido, fecha_creacion, fecha_fin_vigencia, nombre, num_telefono, usuario_id,
                      fecha_fin_vacaciones, fecha_ingreso, fecha_inicio_vacaciones, legajo, empresa_id, tipo_id)
VALUES (1, 'Ortiz', CURRENT_DATE, NULL, 'Gerónimo', NULL, 1, NULL, NULL, NULL, NULL, 2, 1),
       (2, 'Saurina', CURRENT_DATE, NULL, 'Nicolás', NULL, 2, NULL, NULL, NULL, NULL, 1, 1),
       (3, 'Juarroz', CURRENT_DATE, NULL, 'Martiniano', NULL, 3, NULL, NULL, NULL, NULL, 4, 1),
       (4, 'Carrión', CURRENT_DATE, NULL, 'Martín', NULL, 4, NULL, NULL, NULL, NULL, 3, 1);