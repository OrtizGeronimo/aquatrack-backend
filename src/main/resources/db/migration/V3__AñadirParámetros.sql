INSERT INTO empresa(id, direccion, fecha_creacion, nombre, num_telefono, url)
VALUES
    (1, 'Severo del Castillo 1520', CURRENT_DATE, 'Saura SA', '2614193591', 'http://saurasa.com'),
    (2, 'Barrio Santa Ana', CURRENT_DATE, 'Gerardo Soderos', '2613219806', 'http://gersodas.com'),
    (3, 'Barrio Los Frutales', CURRENT_DATE, 'Carrión Aguas', '2613459654', 'http://caraguas.com'),
    (4, 'Barrio Dalvian', CURRENT_DATE, 'Distribuidora Juarroz', '2612314562', 'http://distjuarroz.com');

INSERT INTO dia_semana(id, nombre)
VALUES
    (1, 'Lunes'),
    (2, 'Martes'),
    (3, 'Miercoles'),
    (4, 'Jueves'),
    (5, 'Viernes'),
    (6, 'Sabado'),
    (7, 'Domingo');

INSERT INTO estado_entrega(id, nombre_estado_entrega)
VALUES
    (1, 'Programada'),
    (2, 'Pendiente'),
    (3, 'Procesada'),
    (4, 'Ausente'),
    (5, 'Cancelada');

INSERT INTO estado_pago(id, nombre)
VALUES
    (1, 'Pendiente'),
    (2, 'Rechazado'),
    (3, 'Aprobado');

INSERT INTO estado_pedido(id, nombre_estado_pedido)
VALUES
    (1, 'Pendiente'),
    (2, 'Aprobado'),
    (3, 'Rechazado');

INSERT INTO estado_reparto(id, nombre)
VALUES
    (1, 'Pendiente de Asignación'),
    (2, 'Pendiente de Ejecución'),
    (3, 'En Ejecución'),
    (4, 'Incompleto'),
    (5, 'Cancelado'),
    (6, 'Finalizado');

INSERT INTO medio_pago(id, nombre)
VALUES
    (1, 'Efectivo'),
    (2, 'Mercado Pago');

INSERT INTO tipo_empleado(id, nombre)
VALUES
    (1, 'Oficinista'),
    (2, 'Repartidor');

INSERT INTO tipo_pedido(id, nombre_tipo_pedido)
VALUES
    (1, 'Atípico');

INSERT INTO usuario (id,contraseña,direccion_email,fecha_creacion,fecha_fin_vigencia)
VALUES (1,'$2a$12$QxJltIw5pIXkdc8LzTck4O.4nZsMf2WjM4jsDm99JbMidBLtlFbrS','example@gmail.com',CURRENT_DATE,NULL);

INSERT INTO usuario(id,contraseña,direccion_email,fecha_creacion,fecha_fin_vigencia)
VALUES (2,'$2a$12$QxJltIw5pIXkdc8LzTck4O.4nZsMf2WjM4jsDm99JbMidBLtlFbrS','test@gmail.com',CURRENT_DATE,CURRENT_DATE);