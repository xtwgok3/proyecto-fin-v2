package com.borrador.appservicios.servicios;

import com.borrador.appservicios.entidades.Imagen;
import com.borrador.appservicios.entidades.Usuario;
import com.borrador.appservicios.enumeradores.Rol;
import com.borrador.appservicios.excepciones.Excepciones;
import com.borrador.appservicios.repositorios.UsuarioRepositorio;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author facun
 */
@Service
public class UsuarioServicio implements UserDetailsService {

    Usuario usuario;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ImagenServicio imagenServicio;

    public Usuario crearUsuario(String nombre, String apellido, String email,
            String password, String password2, MultipartFile archivo) throws Excepciones {

        validar(nombre, apellido, email, password, password2);

        usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setFechaDeAlta(new Date());
        
        cargarImagen(archivo);

        usuario.setRol(Rol.USER);
        usuario.setActivo(true);

        return usuario;
    }
//carga imagen nula si no sube un archivo
    public boolean cargarImagen(MultipartFile archivo) throws Excepciones {
        if (!archivo.isEmpty()) {
            Imagen imagen = imagenServicio.guardar(archivo);
            usuario.setImagen(imagen);
            return true;
        }else
            //usuario.setImagen(null);
        return false;
    }

    @Transactional
    public void persistirUsuario(String nombre, String apellido, String email,
            String password, String password2, MultipartFile archivo) throws Excepciones {

        usuario = crearUsuario(nombre, apellido, email, password, password2, archivo);
        usuarioRepositorio.save(usuario);
        System.out.println("Usuario creado"+ usuario.getEmail());
    }

    /*public void modificarUsuario(String id, String nombre, String apellido, String email,
            String password, String password2, MultipartFile archivo) throws Excepciones {

        validar(nombre, apellido, email, password, password2);

        Optional<Usuario> resp = usuarioRepositorio.findById(id);
        if (resp.isPresent()) {
            Usuario usuario =resp.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setPassword(new BCryptPasswordEncoder().encode(password));
          
 
            String idImagen = null;
            if (usuario.getImagen() != null) {
                idImagen = usuario.getImagen().getId();
                Imagen imagen = imagenServicio.actualizar(archivo, idImagen);
                usuario.setImagen(imagen);
                usuarioRepositorio.save(usuario);
            }
        }
    }*/
    public Usuario getOne(String id) {
        return usuarioRepositorio.getOne(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString()); //ROLE_USER
            permisos.add(p);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void validar(String nombre, String apellido, String email,
            String password, String password2) throws Excepciones {
        if (nombre.isEmpty() || nombre == null) {
            throw new Excepciones("El nombre no puede ser nulo o estar vacio");
        }
        if (apellido.isEmpty() || apellido == null) {
            throw new Excepciones("El apellido no puede ser nulo o estar vacio");
        }

        //Tambien verificar que no haya 2 usuarios con el mismo email---!!!!
        if (email.isEmpty() || email == null) {
            throw new Excepciones("El email no puede ser nulo o estar vacio");
        }
        if (password.isEmpty() || password == null) {
            throw new Excepciones("El password no puede ser nulo o estar vacio");
        }
        if (password2.isEmpty() || password2 == null) {
            throw new Excepciones("El password2 no puede ser nulo o estar vacio");
        }

    }

    @Transactional
    public Usuario actualizar(MultipartFile archivo, String id, String nombre, String apellido, String email, String password, String password2) throws Exception {

        validar(nombre, apellido, email, password, password2);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);

            usuario.setPassword(new BCryptPasswordEncoder().encode(password));

   
            cargarImagen(archivo);

            
            usuarioRepositorio.save(usuario);
            System.out.println("Perfil Actualizado: " + usuario.getEmail());
            return usuario;
            
        }
        return null;

    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = new ArrayList();

        usuarios = usuarioRepositorio.findAll();

        return usuarios;
    }

}
