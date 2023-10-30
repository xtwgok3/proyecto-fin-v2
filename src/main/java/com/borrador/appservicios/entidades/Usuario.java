package com.borrador.appservicios.entidades;

import com.borrador.appservicios.enumeradores.Rol;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author facun
 */
@Entity
@Getter
@Setter
@ToString
public class Usuario {
    
    @Id
    @GeneratedValue(generator = "uuid")// Generar id alfanumerico unico
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String nombre;
    private String apellido;

    private String email;
    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaDeAlta;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private Boolean activo;

    @OneToOne
    private Imagen imagen;


}
