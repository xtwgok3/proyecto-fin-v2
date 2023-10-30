package com.borrador.appservicios.repositorios;

import com.borrador.appservicios.entidades.Proveedor;
import com.borrador.appservicios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author facun
 */
@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, String> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email") String email);
}
