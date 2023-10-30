package com.borrador.appservicios.controladores;

import com.borrador.appservicios.entidades.Usuario;
import com.borrador.appservicios.enumeradores.Genero;
import com.borrador.appservicios.excepciones.Excepciones;
import com.borrador.appservicios.servicios.UsuarioServicio;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author kyouma
 */
@Controller
@RequestMapping("/")
public class IndexControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/")
    public String Index() {
        return "index.html";
    }

    @GetMapping("/registrar-usuario")
    public String registrarUsuario() {

        return "usuario_registro.html";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String nombre, @RequestParam String apellido, @RequestParam String email,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo,
            @RequestParam MultipartFile archivo) {

        try {
            usuarioServicio.persistirUsuario(nombre, apellido, email, password, password2, archivo);
            modelo.put("exito", "usuario registrado correctamente");
            return "redirect:/";

        } catch (Excepciones ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("email", email);
            return "redirect:/";
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {

        if (error != null) {
            modelo.put("error", "Usuario o contraseña invalidas!");
        }

        return "login.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PROVEEDOR', 'ROLE_ADMIN')")
    @GetMapping("/inicio")
    public String inicio(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().toString().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        return "inicio.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PROVEEDOR', 'ROLE_ADMIN')")
    @GetMapping("/perfil")
    public String perfilUsuario() {

        return "perfiles.html";
    }
    
    @GetMapping("/proveedor-registro/{id}")
    public String proveedorFormulario() {

        return "proveedor_registro.html";
    }
    
    @GetMapping("/servicios")
    public String servi() {

        return "servicios.html";
    }
    
    
    
    
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PROVEEDOR', 'ROLE_ADMIN')")
    @GetMapping("/perfils")
    public String perfil(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.put("usuario", usuario);
        return "modificar_cliente.html";
    }

    
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/perfils/{id}")
    public String actualizar(MultipartFile archivo, @PathVariable String id, @RequestParam String nombre,@RequestParam String apellido, @RequestParam String email,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo, HttpSession session) {

        try {
            Usuario usuarioactualizado = usuarioServicio.actualizar(archivo, id, nombre, apellido, email, password, password2);

            modelo.put("exito", "Usuario actualizado correctamente!");

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            if (logueado.getRol().toString().equals("ADMIN")) {
                session.setAttribute("usuariosession",usuarioactualizado);
                return "redirect:/admin/dashboard";
            } else {
                session.setAttribute("usuariosession", usuarioactualizado);
                return "redirect:/perfil";
            }
        } catch (Exception ex) {

            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("email", email);

            return "modificar_cliente.html";
        }

    }
    
    
    
    
}
