package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.*;
import com.example.aquatrack_backend.helpers.UbicacionHelper;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import com.example.aquatrack_backend.validators.ClientValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClienteServicio extends ServicioBaseImpl<Cliente> {

    @Autowired
    private ClienteRepo clienteRepo;
    @Autowired
    private CodigoTemporalServicio codigoTemporalServicio;
    @Autowired
    private EmpresaRepo empresaRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private ProductoRepo productoRepo;
    @Autowired
    private EntregaRepo entregaRepo;
    @Autowired
    private EstadoUsuarioRepo estadoUsuarioRepo;
    @Autowired
    private EstadoClienteRepo estadoClienteRepo;
    @Autowired
    private TipoPedidoRepo tipoPedidoRepo;
    @Autowired
    private PedidoRepo pedidoRepo;
    @Autowired
    private DomicilioProductoRepo domicilioProductoRepo;
    @Autowired
    private EstadoEntregaRepo estadoEntregaRepo;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ClientValidator clientValidator;

    public ClienteServicio(RepoBase<Cliente> repoBase) {
        super(repoBase);
    }

    public Page<ClienteListDTO> findAll(int page, int size, String texto, boolean mostrarInactivos) {
        Empresa empresa = getUsuarioFromContext().getPersona().getEmpresa();
        Pageable paging = PageRequest.of(page, size);
        Page<Cliente> clientes = clienteRepo.findAllByEmpresaPaged(empresa.getId(), texto, mostrarInactivos, paging);
        return clienteRepo.findAllByEmpresaPaged(empresa.getId(), texto, mostrarInactivos, paging).map(cliente -> ClienteListDTO.builder().id(cliente.getId()).nombreCompleto(cliente.getNombre() + " " + cliente.getApellido()).fechaCreacion(cliente.getFechaCreacion()).fechaFinVigencia(cliente.getFechaFinVigencia()).dni(cliente.getDni().toString()).direccionEmail(cliente.getUsuario() == null ? "" : cliente.getUsuario().getDireccionEmail()).numTelefono(cliente.getNumTelefono()).domicilio(cliente.getDomicilio() == null ? "" : cliente.getDomicilio().getCalle() + " " + nullableToEmptyString(cliente.getDomicilio().getNumero()) + " " + nullableToEmptyString(cliente.getDomicilio().getPisoDepartamento()) + ", " + nullableToEmptyString(cliente.getDomicilio().getLocalidad())).build());
    }

    public Cliente findClientById(Long idCliente) throws RecordNotFoundException {
        return clienteRepo.findById(idCliente).orElseThrow(() -> new RecordNotFoundException("No se encontro el cliente"));
    }

    @Transactional
    public void disableCliente(Long id) throws Exception {
        Cliente clienteDeshabilitado = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
        clienteDeshabilitado.setFechaFinVigencia(LocalDateTime.now());
        clienteRepo.save(clienteDeshabilitado);
    }

    @Transactional
    public ClienteListDTO enableCliente(Long id) throws RecordNotFoundException {
        Cliente cliente = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
        cliente.setFechaFinVigencia(null);
        clienteRepo.save(cliente);
        return ClienteListDTO.builder().id(cliente.getId()).dni(cliente.getDni().toString()).nombreCompleto(cliente.getNombre() + " " + cliente.getApellido()).fechaCreacion(cliente.getFechaCreacion()).numTelefono(cliente.getNumTelefono()).domicilio(cliente.getDomicilio() == null ? "" : cliente.getDomicilio().getCalle() + " " + nullableToEmptyString(cliente.getDomicilio().getNumero()) + " " + nullableToEmptyString(cliente.getDomicilio().getPisoDepartamento())).build();
    }

    @Transactional
    public EmpresaDTO altaEmpresa(CodigoDTO codigo) throws RecordNotFoundException {

        Long empresa_id = codigoTemporalServicio.obtenerEmpresaPorCodigo(codigo.getCodigo());
        Empresa empresa = empresaRepo.findById(empresa_id).orElseThrow(() -> new RecordNotFoundException("La empresa solicitado no fue encontrado"));

        codigoTemporalServicio.eliminarCodigoUtilizado(codigo.getCodigo());

        EmpresaDTO empresaDTO = new EmpresaDTO();
        empresaDTO.setId(empresa_id);
        empresaDTO.setNombreEmpresa(empresa.getNombre());
        return empresaDTO;
    }

    @Transactional
    public ClienteDTO validarDni(ValidarDniDTO validacion) throws RecordNotFoundException {
        ClienteDTO clienteDTO = new ClienteDTO();
        Cliente cliente = clienteRepo.findByDni(validacion.getDni());
        if (cliente != null) {
            clienteDTO = modelMapper.map(cliente, ClienteDTO.class);
            Domicilio domicilio = cliente.getDomicilio();
            clienteDTO.setCalle(domicilio.getCalle());
            clienteDTO.setNumero(domicilio.getNumero());
            clienteDTO.setPisoDepto(domicilio.getPisoDepartamento());
            clienteDTO.setObservaciones(domicilio.getObservaciones());
        } else {
            clienteDTO.setDni(validacion.getDni());
        }
        clienteDTO.setEmpresaId(validacion.getEmpresaId());
        clienteDTO.setUsuarioId(validacion.getUsuarioId());
        return clienteDTO;
    }

    @Transactional
    public UbicacionDTO createClientFromApp(GuardarClienteDTO cliente) throws RecordNotFoundException {
        Empresa empresa = empresaRepo.findById(cliente.getEmpresaId()).orElseThrow(() -> new RecordNotFoundException("No se encontro la empresa"));
        Usuario usuario = usuarioRepo.findById(cliente.getUsuarioId()).orElseThrow(() -> new RecordNotFoundException("No se encontro el usuario"));
        Cliente clienteNuevo = new ModelMapper().map(cliente, Cliente.class);
        clienteNuevo.setEmpresa(empresa);
        clienteNuevo.setUsuario(usuario);
        Domicilio domicilio = new Domicilio();
        UbicacionDTO ubicacionDTO = new UbicacionDTO();
        if (cliente.getId() != null) {
            Cliente clienteExist = clienteRepo.findById(cliente.getId()).orElseThrow(() -> new RecordNotFoundException("No se encontro el cliente"));
            domicilio = clienteExist.getDomicilio();
            Ubicacion ubicacion = domicilio.getUbicacion();
            ubicacionDTO = modelMapper.map(ubicacion, UbicacionDTO.class);
        } else {
            Deuda deuda = new Deuda();
            deuda.setMonto(BigDecimal.ZERO);
            deuda.setMontoMaximo(BigDecimal.valueOf(200000));
            deuda.setDomicilio(domicilio);
            domicilio.setDeuda(deuda);
            clienteNuevo.setEstadoCliente(estadoClienteRepo.findByNombreEstadoCliente("En proceso de creación").orElseThrow(() -> new RecordNotFoundException("El estado no fue encontrado.")));
        }
        domicilio.setCalle(cliente.getCalle());
        domicilio.setNumero(cliente.getNumero());
        domicilio.setPisoDepartamento(cliente.getPisoDepartamento());
        domicilio.setObservaciones(cliente.getObservaciones());
        clienteNuevo.setDomicilio(domicilio);
        domicilio.setCliente(clienteNuevo);
        Cliente client = clienteRepo.save(clienteNuevo);
        ubicacionDTO.setIdCliente(client.getId());
        return ubicacionDTO;
    }

    @Transactional
    public ClienteListDTO createFromWeb(GuardarClienteWebDTO cliente) throws ClienteNoValidoException {
        Empresa empresa = getUsuarioFromContext().getPersona().getEmpresa();
        clientValidator.validateWebClient(cliente, empresa);

        Cliente clienteNuevo = new Cliente();
        clienteNuevo.setNombre(cliente.getNombre());
        clienteNuevo.setApellido(cliente.getApellido());
        clienteNuevo.setDni(cliente.getDni());
        clienteNuevo.setNumTelefono(cliente.getNumTelefono());
        clienteNuevo.setEmpresa(empresa);

        Domicilio domicilio = new Domicilio();
        domicilio.setCalle(cliente.getCalle());
        domicilio.setNumero(cliente.getNumero());
        domicilio.setPisoDepartamento(cliente.getPisoDepartamento());
        domicilio.setObservaciones(cliente.getObservaciones());
        domicilio.setLocalidad(cliente.getLocalidad());

        Deuda deuda = new Deuda();
        deuda.setMonto(BigDecimal.ZERO);
        deuda.setMontoMaximo(BigDecimal.valueOf(200000));
        deuda.setDomicilio(domicilio);
        domicilio.setDeuda(deuda);

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(cliente.getLatitud());
        ubicacion.setLongitud(cliente.getLongitud());
        domicilio.setUbicacion(ubicacion);

        clienteNuevo.setDomicilio(domicilio);
        domicilio.setCliente(clienteNuevo);

        TipoPedido pedidoHabitual = tipoPedidoRepo.getTipoPedidoById(1L);
        Pedido pedido = new Pedido();
        pedido.setPedidoProductos(new ArrayList<>());
        pedido.setTipoPedido(pedidoHabitual);
        for (GuardarClienteWebProductoDTO producto : cliente.getProductos()) {
            PedidoProducto pedidoProducto = new PedidoProducto();
            Producto productoToAdd = productoRepo.findProductoById(producto.getIdProducto());
            pedidoProducto.setProducto(productoToAdd);
            pedidoProducto.setCantidad(producto.getCantidad());
            pedidoProducto.setPedido(pedido);
            pedido.getPedidoProductos().add(pedidoProducto);
        }

        pedido.setDomicilio(domicilio);
        domicilio.setPedidos(new ArrayList<>());
        domicilio.getPedidos().add(pedido);

        clienteRepo.save(clienteNuevo);
        return ClienteListDTO.builder().id(clienteNuevo.getId()).dni(clienteNuevo.getDni().toString()).nombreCompleto(clienteNuevo.getNombre() + " " + clienteNuevo.getApellido()).fechaCreacion(clienteNuevo.getFechaCreacion()).numTelefono(clienteNuevo.getNumTelefono()).domicilio(clienteNuevo.getDomicilio() == null ? "" : clienteNuevo.getDomicilio().getCalle() + " " + nullableToEmptyString(clienteNuevo.getDomicilio().getNumero()) + " " + nullableToEmptyString(clienteNuevo.getDomicilio().getPisoDepartamento()) + ", " + nullableToEmptyString(clienteNuevo.getDomicilio().getLocalidad())).build();
    }

    @Transactional
    public ClienteListDTO updateFromWeb(Long id, GuardarClienteWebDTO cliente) throws RecordNotFoundException, EntidadNoValidaException {
        Empresa empresa = getUsuarioFromContext().getPersona().getEmpresa();
        Cliente clienteUpdate = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
        clientValidator.validateWebClientUpdate(cliente, empresa);
        clienteUpdate.setNombre(cliente.getNombre());
        clienteUpdate.setApellido(cliente.getApellido());
        clienteUpdate.setDni(cliente.getDni());
        clienteUpdate.setNumTelefono(cliente.getNumTelefono());
        clienteUpdate.setEmpresa(empresa);

        clienteUpdate.getDomicilio().setCalle(cliente.getCalle());
        clienteUpdate.getDomicilio().setNumero(cliente.getNumero());
        clienteUpdate.getDomicilio().setPisoDepartamento(cliente.getPisoDepartamento());
        clienteUpdate.getDomicilio().setObservaciones(cliente.getObservaciones());
        clienteUpdate.getDomicilio().setLocalidad(cliente.getLocalidad());

        clienteUpdate.getDomicilio().getUbicacion().setLatitud(cliente.getLatitud());
        clienteUpdate.getDomicilio().getUbicacion().setLongitud(cliente.getLongitud());

        Pedido pedidoHabitual = clienteUpdate.getDomicilio().getPedidos().stream().filter(p -> p.getTipoPedido().getId() == 1L && p.getFechaFinVigencia() == null).collect(Collectors.toList()).get(0);
        List<Long> productosActuales = pedidoHabitual.getPedidoProductos().stream().map(p -> p.getProducto().getId()).collect(Collectors.toList());
        List<Long> productosNuevos = cliente.getProductos().stream().map(p -> p.getIdProducto()).filter(p -> !productosActuales.contains(p)).collect(Collectors.toList());
        List<Long> productosRemovidos = pedidoHabitual.getPedidoProductos().stream().filter(p -> !cliente.getProductos().stream().map(pp -> pp.getIdProducto()).collect(Collectors.toList()).contains(p.getProducto().getId())).map(p -> p.getProducto().getId()).collect(Collectors.toList());
        if (!productosRemovidos.isEmpty() || !productosNuevos.isEmpty()) {
            pedidoHabitual.setFechaFinVigencia(LocalDateTime.now());
            TipoPedido pedidoHabitualTipo = tipoPedidoRepo.getTipoPedidoById(1L);
            Pedido pedido = new Pedido();
            pedido.setPedidoProductos(new ArrayList<>());
            pedido.setTipoPedido(pedidoHabitualTipo);
            for (GuardarClienteWebProductoDTO producto : cliente.getProductos()) {
                PedidoProducto pedidoProducto = new PedidoProducto();
                Producto productoToAdd = productoRepo.findProductoById(producto.getIdProducto());
                pedidoProducto.setProducto(productoToAdd);
                pedidoProducto.setCantidad(producto.getCantidad());
                pedidoProducto.setPedido(pedido);
                pedido.getPedidoProductos().add(pedidoProducto);
                pedido.setDomicilio(clienteUpdate.getDomicilio());
                clienteUpdate.getDomicilio().getPedidos().add(pedido);
            }
        } else {
            boolean oneChanged = false;
            for (PedidoProducto pp : pedidoHabitual.getPedidoProductos()) {
                GuardarClienteWebProductoDTO pdto = cliente.getProductos().stream().filter(p -> p.getIdProducto() == pp.getProducto().getId()).findFirst().get();
                if (pdto.getCantidad() != pp.getCantidad()) {
                    oneChanged = true;
                    break;
                }
            }

            if (oneChanged) {
                pedidoHabitual.setFechaFinVigencia(LocalDateTime.now());
                TipoPedido pedidoHabitualTipo = tipoPedidoRepo.getTipoPedidoById(1L);
                Pedido pedido = new Pedido();
                pedido.setPedidoProductos(new ArrayList<>());
                pedido.setTipoPedido(pedidoHabitualTipo);
                for (GuardarClienteWebProductoDTO producto : cliente.getProductos()) {
                    Optional<DomicilioProducto> dp = clienteUpdate.getDomicilio().getProductoDomicilios().stream().filter(pd -> pd.getProducto().getId() == producto.getIdProducto()).findFirst();
                    if (dp.isPresent()) {
                        if (dp.get().getCantidadDevolver() != null && dp.get().getCantidadDevolver() > 0 && dp.get().getCantidad() >= producto.getCantidad()) {
                            dp.get().setCantidadDevolver(dp.get().getCantidad() - producto.getCantidad());
                            domicilioProductoRepo.save(dp.get());
                        }
                    }
                    PedidoProducto pedidoProducto = new PedidoProducto();
                    Producto productoToAdd = productoRepo.findProductoById(producto.getIdProducto());
                    pedidoProducto.setProducto(productoToAdd);
                    pedidoProducto.setCantidad(producto.getCantidad());
                    pedidoProducto.setPedido(pedido);
                    pedido.getPedidoProductos().add(pedidoProducto);
                    pedido.setDomicilio(clienteUpdate.getDomicilio());
                    clienteUpdate.getDomicilio().getPedidos().add(pedido);
                }
            }
        }

        clienteRepo.save(clienteUpdate);
        return ClienteListDTO.builder().id(clienteUpdate.getId()).dni(clienteUpdate.getDni().toString()).nombreCompleto(clienteUpdate.getNombre() + " " + clienteUpdate.getApellido()).fechaCreacion(clienteUpdate.getFechaCreacion()).numTelefono(clienteUpdate.getNumTelefono()).domicilio(clienteUpdate.getDomicilio() == null ? "" : clienteUpdate.getDomicilio().getCalle() + " " + nullableToEmptyString(clienteUpdate.getDomicilio().getNumero()) + " " + nullableToEmptyString(clienteUpdate.getDomicilio().getPisoDepartamento()) + ", " + nullableToEmptyString(clienteUpdate.getDomicilio().getLocalidad())).build();
    }

    @Transactional
    public GuardarClienteWebDTO clienteForEdit(Long id) throws RecordNotFoundException {
        Cliente cliente = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontro el cliente"));
        TipoPedido pedidoHabitual = tipoPedidoRepo.getTipoPedidoById(1L);
        return GuardarClienteWebDTO.builder().id(cliente.getId()).dni(cliente.getDni()).nombre(cliente.getNombre()).apellido(cliente.getApellido()).numTelefono(cliente.getNumTelefono()).calle(cliente.getDomicilio().getCalle()).numero(cliente.getDomicilio().getNumero()).pisoDepartamento(cliente.getDomicilio().getPisoDepartamento()).observaciones(cliente.getDomicilio().getObservaciones()).latitud(cliente.getDomicilio().getUbicacion().getLatitud()).longitud(cliente.getDomicilio().getUbicacion().getLongitud()).localidad(cliente.getDomicilio().getLocalidad()).productos(cliente.getDomicilio().getPedidos().stream().filter(p -> p.getTipoPedido().equals(pedidoHabitual) && p.getFechaFinVigencia() == null).collect(Collectors.toList()).get(0).getPedidoProductos().stream().map(pp -> GuardarClienteWebProductoDTO.builder().idProducto(pp.getProducto().getId()).cantidad(pp.getCantidad()).build()).collect(Collectors.toList())).build();
    }

    @Transactional
    public DetalleClienteMobileDTO getPersonalInfo() throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        Cliente cliente = (Cliente) persona;
        if (persona instanceof Empleado || cliente.getUsuario() == null) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        return DetalleClienteMobileDTO.builder().nombre(cliente.getNombre()).apellido(cliente.getApellido()).dni(cliente.getDni()).numTelefono(cliente.getNumTelefono()).calle(cliente.getDomicilio().getCalle()).numero(nullableToEmptyString(cliente.getDomicilio().getNumero())).pisoDepto(nullableToEmptyString(cliente.getDomicilio().getPisoDepartamento())).observaciones(nullableToEmptyString(cliente.getDomicilio().getObservaciones())).empresa(cliente.getEmpresa().getNombre()).direccionEmail(cliente.getUsuario().getDireccionEmail()).localidad(cliente.getDomicilio().getLocalidad()).latitud(cliente.getDomicilio().getUbicacion().getLatitud()).longitud(cliente.getDomicilio().getUbicacion().getLongitud()).build();

    }

    @Transactional
    public EditarClienteMobileDTO editarClienteMobile(EditarClienteMobileDTO atributos) throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        Cliente cliente = (Cliente) persona;
        if (persona instanceof Empleado || cliente.getUsuario() == null) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        cliente.setNombre(atributos.getNombre());
        cliente.setApellido(atributos.getApellido());
        cliente.setNumTelefono(atributos.getNumTelefono());
        clienteRepo.save(cliente);

        EditarClienteMobileDTO respuesta = new EditarClienteMobileDTO();
        respuesta.setNombre(cliente.getNombre());
        respuesta.setApellido(cliente.getApellido());
        respuesta.setNumTelefono(cliente.getNumTelefono());
        return respuesta;
    }


    @Transactional
    public EditarDomicilioMobileDTO editarDomicilioMobile(EditarDomicilioMobileDTO domicilio) throws UserUnauthorizedException, EntidadNoValidaException {
        Persona persona = getUsuarioFromContext().getPersona();
        Cliente cliente = (Cliente) persona;
        if (persona instanceof Empleado || cliente.getUsuario() == null) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        UbicacionDTO ubicacion = UbicacionDTO.builder().latitud(domicilio.getLatitud()).longitud(domicilio.getLongitud()).build();
        if (!new UbicacionHelper().estaContenida(ubicacion, cliente.getEmpresa().getCobertura())) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("root", "La ubicación ingresada no se encuentra dentro de la cobertura de la empresa.");
            throw new EntidadNoValidaException(errors);
        }

        cliente.getDomicilio().setCalle(domicilio.getCalle());
        cliente.getDomicilio().setNumero(domicilio.getNumero());
        cliente.getDomicilio().setPisoDepartamento(domicilio.getPisoDepto());
        cliente.getDomicilio().setObservaciones(domicilio.getObservaciones());
        cliente.getDomicilio().setLocalidad(domicilio.getLocalidad());

        cliente.getDomicilio().getUbicacion().setLatitud(domicilio.getLatitud());
        cliente.getDomicilio().getUbicacion().setLongitud(domicilio.getLongitud());

        clienteRepo.save(cliente);

        EditarDomicilioMobileDTO respuesta = new EditarDomicilioMobileDTO();
        respuesta.setCalle(cliente.getDomicilio().getCalle());
        respuesta.setNumero(cliente.getDomicilio().getNumero());
        respuesta.setObservaciones(cliente.getDomicilio().getObservaciones());
        respuesta.setPisoDepto(cliente.getDomicilio().getPisoDepartamento());
        respuesta.setLocalidad(cliente.getDomicilio().getLocalidad());
        respuesta.setLatitud(cliente.getDomicilio().getUbicacion().getLatitud());
        respuesta.setLongitud(cliente.getDomicilio().getUbicacion().getLongitud());

        return respuesta;
    }


    @Transactional
    public Map<String, String> getProximaEntregaCliente() throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }
        String message = null;
        Map<String, String> response = new HashMap<>();

        if (((Cliente) persona).getDomicilio().getDiaDomicilios().isEmpty()) {
            message = "Su domicilio no está asignado a ninguna ruta. Contáctese con el gerente de " + persona.getEmpresa().getNombre();
            response.put("message", message);
            return response;
        }
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        int idDia = dayOfWeek.ordinal() + 1;

        // filtro primero los dias de la semana que viene
        List<Long> diasSemanaProxima = ((Cliente) persona).getDomicilio().getDiaDomicilios().stream().map(dia -> dia.getDiaRuta().getDiaSemana().getId()).filter(d -> (d - idDia) < 0).sorted(Comparator.naturalOrder()).collect(Collectors.toList());

        // filtro despues los dias de esta semana pasado el día de hoy
        List<Long> diasSemanaActual = ((Cliente) persona).getDomicilio().getDiaDomicilios().stream().map(dia -> dia.getDiaRuta().getDiaSemana().getId()).filter(d -> (d - idDia) >= 0).sorted(Comparator.naturalOrder()).collect(Collectors.toList());


        if (!diasSemanaActual.isEmpty()) {
            Long proximoDia = diasSemanaActual.get(0);
            if (proximoDia == idDia) {
                message = "El repartidor pasará hoy por tu casa.";
            } else {
                message = "El repartidor pasará por tu casa el " + nextDateMessage(proximoDia, (long) idDia) + ".";
            }
            response.put("message", message);
            return response;
        }

        message = "El repartidor pasará por tu casa el " + nextDateMessage(diasSemanaProxima.get(0), (long) idDia) + ".";
        response.put("message", message);
        return response;
    }

    private String nextDateMessage(Long proximo, Long hoy) {
        Long daysToAdd = (proximo - hoy + 7) % 7;
        LocalDate proximaFecha = LocalDate.now().plusDays(daysToAdd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", new Locale("es", "ES"));
        return proximaFecha.format(formatter);
    }

    private String nullableToEmptyString(Object value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    public PedidoHabitualDTO getPedidoHabitual() throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Optional<Pedido> pedido = pedidoRepo.getClientPedido(((Cliente) persona).getDomicilio().getId());
        return pedido.map(value -> PedidoHabitualDTO.builder().id(value.getId()).productos(value.getPedidoProductos().stream().map(pp -> PedidoHabitualProductoDTO.builder().idProducto(pp.getProducto().getId()).nombre(pp.getProducto().getNombre()).cantidad(pp.getCantidad()).precioUnitario(pp.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio()).build()).collect(Collectors.toList())).build()).orElse(null);
    }

    @Transactional
    public PedidoHabitualDTO crearPedidoHabitual(GuardarPedidoHabitualMobileDTO pedido) throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        TipoPedido pedidoHabitual = tipoPedidoRepo.getTipoPedidoById(1L);
        Pedido pedidoNuevo = new Pedido();
        pedidoNuevo.setPedidoProductos(new ArrayList<>());
        pedidoNuevo.setTipoPedido(pedidoHabitual);
        for (GuardarClienteWebProductoDTO producto : pedido.getProductos()) {
            PedidoProducto pedidoProducto = new PedidoProducto();
            Producto productoToAdd = productoRepo.findProductoById(producto.getIdProducto());
            pedidoProducto.setProducto(productoToAdd);
            pedidoProducto.setCantidad(producto.getCantidad());
            pedidoProducto.setPedido(pedidoNuevo);
            pedidoNuevo.getPedidoProductos().add(pedidoProducto);
        }
        pedidoNuevo.setDomicilio(cliente.getDomicilio());
        cliente.getDomicilio().setPedidos(new ArrayList<>());
        cliente.getDomicilio().getPedidos().add(pedidoNuevo);

        clienteRepo.save(cliente);
        return PedidoHabitualDTO.builder().id(pedidoNuevo.getId()).productos(pedidoNuevo.getPedidoProductos().stream().map(pp -> PedidoHabitualProductoDTO.builder().idProducto(pp.getProducto().getId()).nombre(pp.getProducto().getNombre()).cantidad(pp.getCantidad()).precioUnitario(pp.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio()).build()).collect(Collectors.toList())).build();
    }

    @Transactional
    public PedidoHabitualDTO updatePedidoHabitual(GuardarPedidoHabitualMobileDTO pedido) throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }
        Cliente cliente = clienteRepo.findById(persona.getId()).get();

        Optional<Pedido> pedidoExistente = pedidoRepo.getClientPedido(cliente.getDomicilio().getId());
        if (pedidoExistente.isPresent()) {
            pedidoExistente.get().setFechaFinVigencia(LocalDateTime.now());
            pedidoRepo.save(pedidoExistente.get());
        }

        TipoPedido pedidoHabitual = tipoPedidoRepo.getTipoPedidoById(1L);
        Pedido pedidoNuevo = new Pedido();
        pedidoNuevo.setPedidoProductos(new ArrayList<>());
        pedidoNuevo.setTipoPedido(pedidoHabitual);
        for (GuardarClienteWebProductoDTO producto : pedido.getProductos()) {
            Optional<DomicilioProducto> dp = cliente.getDomicilio().getProductoDomicilios().stream().filter(pd -> pd.getProducto().getId() == producto.getIdProducto()).findFirst();
            if (dp.isPresent()) {
                if (dp.get().getCantidadDevolver() != null && dp.get().getCantidadDevolver() > 0 && dp.get().getCantidad() >= producto.getCantidad()) {
                    dp.get().setCantidadDevolver(dp.get().getCantidad() - producto.getCantidad());
                    domicilioProductoRepo.save(dp.get());
                }
            }
            PedidoProducto pedidoProducto = new PedidoProducto();
            Producto productoToAdd = productoRepo.findProductoById(producto.getIdProducto());
            pedidoProducto.setProducto(productoToAdd);
            pedidoProducto.setCantidad(producto.getCantidad());
            pedidoProducto.setPedido(pedidoNuevo);
            pedidoNuevo.getPedidoProductos().add(pedidoProducto);
        }
        pedidoNuevo.setDomicilio(cliente.getDomicilio());
        cliente.getDomicilio().getPedidos().add(pedidoNuevo);

        clienteRepo.save(cliente);
        pedidoNuevo = pedidoRepo.getClientPedido(cliente.getDomicilio().getId()).get();
        return PedidoHabitualDTO.builder().id(pedidoNuevo.getId()).productos(pedidoNuevo.getPedidoProductos().stream().map(pp -> PedidoHabitualProductoDTO.builder().idProducto(pp.getProducto().getId()).nombre(pp.getProducto().getNombre()).cantidad(pp.getCantidad()).precioUnitario(pp.getProducto().getPrecios().stream().filter(pr -> pr.getFechaFinVigencia() == null).findFirst().get().getPrecio()).build()).collect(Collectors.toList())).build();
    }

    public GuardarPedidoHabitualMobileDTO editPedidoHabitual() throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        Pedido pedido = pedidoRepo.getClientPedido(((Cliente) persona).getDomicilio().getId()).get();

        return GuardarPedidoHabitualMobileDTO.builder().productos(pedido.getPedidoProductos().stream().map(pp -> GuardarClienteWebProductoDTO.builder().idProducto(pp.getProducto().getId()).cantidad(pp.getCantidad()).build()).collect(Collectors.toList())).build();
    }

    public ClienteEntregaPendienteDTO getEntregaPendienteCliente() throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        Entrega entrega = entregaRepo.entregaActualCliente(cliente.getDomicilio().getId());
        if (entrega == null) {
            return ClienteEntregaPendienteDTO.builder().tieneEntrega(false).build();
        } else {
            return ClienteEntregaPendienteDTO.builder().tieneEntrega(true).idReparto(entrega.getReparto().getId()).repartidor(entrega.getReparto().getRepartidor().getNombre()).build();
        }
    }

    public GoogleDirectionsDTO getUbicacionRepartidor() throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        Entrega entrega = entregaRepo.entregaActualCliente(cliente.getDomicilio().getId());
        if (entrega != null) {
            return GoogleDirectionsDTO.builder().latitude(entrega.getReparto().getUbicacion().getLatitud()).longitude(entrega.getReparto().getUbicacion().getLongitud()).build();
        } else {
            return null;
        }
    }

    public GoogleDirectionsDTO getUbicacionCliente() throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        return GoogleDirectionsDTO.builder().latitude(cliente.getDomicilio().getUbicacion().getLatitud()).longitude(cliente.getDomicilio().getUbicacion().getLongitud()).build();
    }

    public List<EntregaMobileClienteDTO> getEntregasMobile(LocalDate fechaVisitaDesde, LocalDate fechaVisitaHasta, boolean sinPagar) throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        List<Entrega> entregas = entregaRepo.getEntregasProcesadasCliente(cliente.getDomicilio().getId(), fechaVisitaDesde, fechaVisitaHasta, sinPagar);
        return entregas.stream().map(entrega -> {
            EntregaMobileClienteDTO response = new EntregaMobileClienteDTO();
            response.setId(entrega.getId());
            response.setMontoEntrega(entrega.getMonto());
            response.setRepartidor(entrega.getReparto().getRepartidor().getNombre() + " " + entrega.getReparto().getRepartidor().getApellido());
            response.setFechaHoraVisita(entrega.getFechaHoraVisita());
            response.setProductosEntregados(entrega.getEntregaDetalles().stream().map(ed -> PedidoProductoDTO.builder().idProducto(ed.getProducto().getId()).nombreProducto(ed.getProducto().getNombre()).cantidad(ed.getCantidadEntregada()).build()).collect(Collectors.toList()));
            if (!entrega.getPago().getTotal().equals(BigDecimal.ZERO)) {
                response.setPago(EntregaMobileClientePagoDTO.builder().monto(entrega.getPago().getTotal()).medioPago(entrega.getPago().getMedioPago().getNombre()).build());
            } else {
                response.setPago(null);
            }
            return response;
        }).collect(Collectors.toList());
    }

    public void cancelarEntregaPendiente(CancelarEntregaClienteDTO observaciones) throws UserUnauthorizedException, ValidacionException {
        Persona persona = getUsuarioFromContext().getPersona();
        if (persona instanceof Empleado) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para clientes de Aquatrack.");
        }

        Cliente cliente = (Cliente) persona;
        Entrega entrega = entregaRepo.entregaActualCliente(cliente.getDomicilio().getId());
        if (entrega == null) {
            throw new ValidacionException("No tiene ninguna entrega pendiente para cancelar.");
        }

        EstadoEntrega cancelada = estadoEntregaRepo.findByNombreEstadoEntrega("Cancelada");
        entrega.setEstadoEntrega(cancelada);
        entrega.setObservaciones(observaciones.getObservaciones());
        entregaRepo.save(entrega);
    }
}