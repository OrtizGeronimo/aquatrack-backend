create table cliente (id bigint not null, apellido varchar(255), fecha_creacion datetime(6), fecha_fin_vigencia datetime(6), nombre varchar(255), num_telefono varchar(255), usuario_id bigint, empresa_id bigint, estado_cliente_id bigint, dni integer, primary key (id)) engine=InnoDB;
create table cobertura (id bigint not null auto_increment, empresa_id bigint, primary key (id)) engine=InnoDB;
create table codigo_temporal (id bigint not null auto_increment, codigo varchar(255), fecha_expiracion datetime(6), empresa_id bigint, primary key (id)) engine=InnoDB;
create table deuda (id bigint not null auto_increment, fecha_ultima_actualizacion datetime(6), monto decimal not null, monto_maximo decimal not null, domicilio_id bigint, primary key (id)) engine=InnoDB;
create table deuda_pago (id bigint not null auto_increment, monto_adeudado_pago decimal not null, deuda_id bigint, pago_id bigint, primary key (id)) engine=InnoDB;
create table dia_domicilio (id bigint not null auto_increment, dia_ruta_id bigint, domicilio_id bigint, primary key (id)) engine=InnoDB;
create table dia_ruta (id bigint not null auto_increment, dia_semana_id bigint, ruta_id bigint, primary key (id)) engine=InnoDB;
create table dia_semana (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre varchar(255), primary key (id)) engine=InnoDB;
create table domicilio (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), calle varchar(255), numero integer, piso_departamento varchar(255), observaciones varchar(255), localidad varchar(255), cliente_id bigint, deuda_id bigint, ubicacion_id bigint, primary key (id)) engine=InnoDB;
create table domicilio_producto (id bigint not null auto_increment, cantidad integer, domicilio_id bigint, producto_id bigint, primary key (id)) engine=InnoDB;
create table domicilio_ruta (id bigint not null auto_increment, domicilio_id bigint, ruta_id bigint, primary key (id)) engine=InnoDB;
create table empleado (id bigint not null, apellido varchar(255), fecha_creacion datetime(6), fecha_fin_vigencia datetime(6), nombre varchar(255), num_telefono varchar(255), usuario_id bigint, fecha_fin_vacaciones datetime(6), fecha_ingreso datetime(6), fecha_inicio_vacaciones datetime(6), legajo integer, empresa_id bigint, tipo_id bigint, primary key (id)) engine=InnoDB;
create table estado_cliente (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre_estado_cliente varchar(255), primary key (id)) engine=InnoDB;
create table empresa (id bigint not null auto_increment, direccion varchar(255), email varchar(255), fecha_creacion datetime(6), fecha_fin_vigencia datetime(6), hora_generacion_reparto time(0), nombre varchar(255), num_telefono varchar(255), url varchar(255), ubicacion_id bigint, primary key (id)) engine=InnoDB;
create table entrega (id bigint not null auto_increment, fecha_hora_visita datetime(6), orden_visita  integer, domicilio_id bigint, estado_entrega_id bigint, pago_id bigint, reparto_id bigint, primary key (id)) engine=InnoDB;
create table entrega_detalle (id bigint not null auto_increment, cantidad_entregada integer, cantidad_recibida integer, entrega_id bigint, producto_id bigint, primary key (id)) engine=InnoDB;
create table estado_entrega (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre_estado_entrega varchar(255), primary key (id)) engine=InnoDB;
create table estado_pago (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre varchar(255), primary key (id)) engine=InnoDB;
create table estado_pedido (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre_estado_pedido varchar(255), primary key (id)) engine=InnoDB;
create table estado_reparto (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre varchar(255), primary key (id)) engine=InnoDB;
create table estado_usuario (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre_estado_usuario varchar(255), primary key (id)) engine=InnoDB;
create table hibernate_sequence (next_val bigint) engine=InnoDB;
insert into hibernate_sequence values ( 1 );
create table hibernate_sequences (sequence_name varchar(255) not null, next_val bigint, primary key (sequence_name)) engine=InnoDB;
insert into hibernate_sequences(sequence_name, next_val) values ('default',0);
create table medio_pago (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre varchar(255), primary key (id)) engine=InnoDB;
create table pago (id bigint not null auto_increment, fecha_pago datetime(6), total decimal not null, entrega_id bigint, estado_pago_id bigint, medio_pago_id bigint, primary key (id)) engine=InnoDB;
create table pedido (id bigint not null auto_increment, fecha_coordinada_entrega datetime(6), domicilio_id bigint, estado_pedido_id bigint, tipo_pedido_id bigint, primary key (id)) engine=InnoDB;
create table pedido_producto (id bigint not null auto_increment, cantidad integer, pedido_id bigint, producto_id bigint, primary key (id)) engine=InnoDB;
create table permiso (id bigint not null auto_increment, descripcion varchar(255), fecha_fin_vigencia datetime(6), primary key (id)) engine=InnoDB;
create table permiso_rol (id bigint not null auto_increment, permiso_id bigint, rol_id bigint, primary key (id)) engine=InnoDB;
create table precio (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), precio decimal, producto_id bigint, primary key (id)) engine=InnoDB;
create table producto (id bigint not null auto_increment, descripcion varchar(255), fecha_fin_vigencia datetime(6), nombre varchar(255), codigo varchar(255), imagen varchar(255), empresa_id bigint, primary key (id)) engine=InnoDB;
create table reparto (id bigint not null auto_increment,  fecha_ejecucion datetime(6), fecha_hora_inicio datetime(6), fecha_hora_fin datetime(6), observaciones varchar(255), estado_reparto_id bigint, repartidor_id bigint, ruta_id bigint, primary key (id)) engine=InnoDB;
create table rol (id bigint not null auto_increment, fecha_creacion datetime(6), fecha_fin_vigencia datetime(6), nombre varchar(255), empresa_id bigint, primary key (id)) engine=InnoDB;
create table rol_usuario (id bigint not null auto_increment, rol_id bigint, usuario_id bigint, primary key (id)) engine=InnoDB;
create table ruta (id bigint not null auto_increment, nombre varchar(255), fecha_creacion datetime(6), fecha_fin_vigencia datetime(6), empresa_id bigint, primary key (id)) engine=InnoDB;
create table tipo_empleado (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre varchar(255), primary key (id)) engine=InnoDB;
create table tipo_pedido (id bigint not null auto_increment, fecha_fin_vigencia datetime(6), nombre_tipo_pedido varchar(255), primary key (id)) engine=InnoDB;
create table ubicacion (id bigint not null auto_increment, latitud double precision, longitud double precision, cobertura_id bigint, primary key (id)) engine=InnoDB;
create table usuario (id bigint not null auto_increment, contraseña varchar(255), direccion_email varchar(255), fecha_creacion datetime(6), fecha_fin_vigencia datetime(6), validado bit, token_password varchar(255), token_email varchar(255), estado_usuario_id bigint, primary key (id)) engine=InnoDB;
create table usuario_codigo_validacion (id bigint not null auto_increment, codigo varchar(255), fecha_creacion datetime(6), fecha_fin_vigencia datetime(6), usuario_id bigint, primary key (id)) engine=InnoDB;
alter table cliente add constraint FK_id7jmosqg8hkqiqw4vf50xipm foreign key (usuario_id) references usuario (id);
alter table cliente add constraint FK_id7jmosqg8hkqiqw4vf52azfd foreign key (empresa_id) references empresa (id);
alter table cliente add constraint FKlq5bi6ct62v3aame1pixcaswq foreign key (estado_cliente_id) references estado_cliente (id);
alter table cobertura add constraint FKb27p91f7hiunb5hdih43dthp6 foreign key (empresa_id) references empresa (id);
alter table codigo_temporal add constraint FKc13itnhg09x7be6gawnwx5ya4 foreign key (empresa_id) references empresa (id);
alter table deuda add constraint FKf0o1s8woy0y0sy1yu8e4eh2w8 foreign key (domicilio_id) references domicilio (id);
alter table deuda_pago add constraint FKenwnrgy4htk1bgvh33ft5dn56 foreign key (deuda_id) references deuda (id);
alter table deuda_pago add constraint FK3mpxtei4x8da7aw3jpbm0f27w foreign key (pago_id) references pago (id);
alter table dia_domicilio add constraint FKau0qm7ramwjg335dwc02l3nq8 foreign key (dia_ruta_id) references dia_ruta (id);
alter table dia_domicilio add constraint FK75dj00igog9quvujy2dwbdbpd foreign key (domicilio_id) references domicilio (id);
alter table dia_ruta add constraint FKkmesfbmgtrepif5hmnpbsqogd foreign key (dia_semana_id) references dia_semana (id);
alter table dia_ruta add constraint FK605twsv4224x70me4gumio3co foreign key (ruta_id) references ruta (id);
alter table domicilio add constraint FKpj1i9rvwxyyjsdwcest6lxdn0 foreign key (cliente_id) references cliente (id);
alter table domicilio add constraint FKejfki045hwua9384u95vnj0d3 foreign key (deuda_id) references deuda (id);
alter table domicilio add constraint FKccjr0mpnbxbqwrooeen1c8roh foreign key (ubicacion_id) references ubicacion (id);
alter table domicilio_producto add constraint FKncnta3jlhmwiris5729ubwsx8 foreign key (domicilio_id) references domicilio (id);
alter table domicilio_producto add constraint FK8g00hfonijspg1iwv5haheeg9 foreign key (producto_id) references producto (id);
alter table domicilio_ruta add constraint FKjivot93hi8jyu72v0wop2diw6 foreign key (domicilio_id) references domicilio (id);
alter table domicilio_ruta add constraint FKgyqrygr5ektmv7ham26cuq0uv foreign key (ruta_id) references ruta (id);
alter table empleado add constraint FKhw0kus5ohyrrpf1xo13fjpuik foreign key (empresa_id) references empresa (id);
alter table empleado add constraint FKebgcoxq3trs1htrteotw9hbrs foreign key (tipo_id) references tipo_empleado (id);
alter table empleado add constraint FK_6ff36el6hfqwrtnvk0y9jd6sh foreign key (usuario_id) references usuario (id);
alter table empresa add constraint FK5t0tdt455qee1cjkmctb3nds9 foreign key (ubicacion_id) references ubicacion (id);
alter table entrega add constraint FKck82ob5xmq55mh6w1mqpeah0b foreign key (domicilio_id) references domicilio (id);
alter table entrega add constraint FKlq5bi6ct62v3aame1pixcmjtb foreign key (estado_entrega_id) references estado_entrega (id);
alter table entrega add constraint FK50y7nb267nyd6p383v6x0rhqa foreign key (pago_id) references pago (id);
alter table entrega add constraint FKca2s3evm3vyrpkuhyw74t2un3 foreign key (reparto_id) references reparto (id);
alter table entrega_detalle add constraint FK9x2bnw67d6dcskc148g9w643o foreign key (entrega_id) references entrega (id);
alter table entrega_detalle add constraint FKnodtqpti8qpdi3fphdxkpvade foreign key (producto_id) references producto (id);
alter table pago add constraint FK8usrvfpf7ijnrjptp48ymmti1 foreign key (entrega_id) references entrega (id);
alter table pago add constraint FKk1k8eu00dteugk3f0o7g7wlkn foreign key (estado_pago_id) references estado_pago (id);
alter table pago add constraint FK6ut8vtk46wlu3k0vwsv760jlh foreign key (medio_pago_id) references medio_pago (id);
alter table pedido add constraint FKauqt5ljerhslue4scdu1qexb foreign key (domicilio_id) references domicilio (id);
alter table pedido add constraint FKpapbin6tgk2j5es15ajwxfmv2 foreign key (estado_pedido_id) references estado_pedido (id);
alter table pedido add constraint FKkyrm947nmm29rpuibku5iihp6 foreign key (tipo_pedido_id) references tipo_pedido (id);
alter table pedido_producto add constraint FK7uyg0ynfe4wadl7ha9bmtynvm foreign key (pedido_id) references pedido (id);
alter table pedido_producto add constraint FKl9lfd6a7bi0o5qn2f3epfbpin foreign key (producto_id) references producto (id);
alter table permiso_rol add constraint FKh52qkt8liov8gf01mkq9ms926 foreign key (permiso_id) references permiso (id);
alter table permiso_rol add constraint FK4ngehdlselc6uwg9kh1xc45wq foreign key (rol_id) references rol (id);
alter table precio add constraint FK64w86n0folwdyjw5d154b5mq foreign key (producto_id) references producto (id);
alter table producto add constraint FK64w86n0folwdyjw5d154b5ds foreign key (empresa_id) references empresa (id);
alter table reparto add constraint FKtr3pnspdaqor2y5p7jyapq6a8 foreign key (estado_reparto_id) references estado_reparto (id);
alter table reparto add constraint FK8d3n9vm8d4wm0mtof1d9v2uk2 foreign key (repartidor_id) references empleado (id);
alter table reparto add constraint FKb3bqfs2hxwcwgmajnxn3wv9ri foreign key (ruta_id) references ruta (id);
alter table rol add constraint FKhqf7i4x8hnad5gfbsgdw6wp33 foreign key (empresa_id) references empresa (id);
alter table rol_usuario add constraint FKoc5d5rx86a1ba0lwq9dxcr4ia foreign key (rol_id) references rol (id);
alter table rol_usuario add constraint FK99qbb42lm96u3n0tjojmb6ktm foreign key (usuario_id) references usuario (id);
alter table ruta add constraint FKhqf7i4x8hnad5gfbsgdw3wsca foreign key (empresa_id) references empresa (id);
alter table ubicacion add constraint FK1ss12bcqh71c1gf98s9mi035b foreign key (cobertura_id) references cobertura (id);
alter table usuario add constraint FKlq5bi6ct62v3aame1pixchbqt foreign key (estado_usuario_id) references estado_usuario (id);
alter table usuario_codigo_validacion add constraint FK7wim923ngjhstxdgroodgtwck foreign key (usuario_id) references usuario (id);