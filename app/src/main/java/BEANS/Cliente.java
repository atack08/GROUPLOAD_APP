package BEANS;

import java.io.Serializable;

public class Cliente implements Serializable {

    private Usuario user;
    private String ip;
    private int porcentaje_descarga;
    static final long serialVersionUID =1L;

    public Cliente(Usuario user, String ip, int porcentaje_descarga) {
        this.user = user;
        this.ip = ip;
        this.porcentaje_descarga = porcentaje_descarga;
    }

    public Usuario getUser() {
        return user;
    }

    public String getIp() {
        return ip;
    }

    public int getPorcentaje_descarga() {
        return porcentaje_descarga;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;

        Cliente cliente = (Cliente) o;

        if(user.getNombre().equalsIgnoreCase(cliente.getUser().getNombre()))
            return true;

        return false;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPorcentaje_descarga(int porcentaje_descarga) {
        this.porcentaje_descarga = porcentaje_descarga;
    }
}

