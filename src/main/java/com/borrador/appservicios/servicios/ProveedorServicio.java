package com.borrador.appservicios.servicios;

import com.borrador.appservicios.entidades.Imagen;
import com.borrador.appservicios.entidades.Proveedor;
import com.borrador.appservicios.entidades.Usuario;
import com.borrador.appservicios.enumeradores.Rol;
import com.borrador.appservicios.excepciones.Excepciones;
import com.borrador.appservicios.repositorios.ProveedorRepositorio;
import com.borrador.appservicios.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author facun
 */
@Service
public class ProveedorServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private ProveedorRepositorio proveedorRepositorio;

    @Autowired
    private ImagenServicio imagenServicio;

    
    @Transactional
    public void crearProveedor(String idUsuario, String tipoServicio, String descripcionServicio, MultipartFile archivo) throws Excepciones {

        Optional<Usuario> resp = usuarioRepositorio.findById(idUsuario);
        if (resp.isPresent()) {

            Usuario usuario = resp.get();
            Proveedor proveedor = new Proveedor();

            proveedor.setNombre(usuario.getNombre());
            proveedor.setApellido(usuario.getApellido());
            proveedor.setEmail(usuario.getEmail());
            proveedor.setPassword(usuario.getPassword());
            proveedor.setFechaDeAlta(usuario.getFechaDeAlta());

            Imagen imagen = imagenServicio.guardar(archivo);
            proveedor.setImagen(imagen);
            
            proveedor.setRol(Rol.PROVEEDOR);
            proveedor.setActivo(true);

            proveedor.setServicioTipo(tipoServicio);
            proveedor.setServicioDescripcion(descripcionServicio);
            proveedor.setClientes(new ArrayList());
            proveedor.setCalificacion(0);
            
            proveedorRepositorio.save(proveedor);
            
        }
    }
}
