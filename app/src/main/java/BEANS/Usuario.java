package BEANS;


import java.io.Serializable;

public class Usuario implements Serializable{

    private static final long serialVersionUID = 1L;
    private String user;
    private String password;
    private String nombre;

    public Usuario(String user, String password, String nombre) {
        this.user = user;
        this.password = password;
        this.nombre = nombre;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }
}
