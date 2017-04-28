package BEANS;

public class Servidor {

    private String nombreServidor;
    private String ipServidor;
    private String paisServidor;

    public Servidor(){}

    public Servidor(String nombreServidor, String ipServidor, String paisServidor) {
        this.nombreServidor = nombreServidor;
        this.ipServidor = ipServidor;
        this.paisServidor = paisServidor;
    }

    public String getNombreServidor() {
        return nombreServidor;
    }

    public void setNombreServidor(String nombreServidor) {
        this.nombreServidor = nombreServidor;
    }

    public String getIpServidor() {
        return ipServidor;
    }

    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    public String getPaisServidor() {
        return paisServidor;
    }

    public void setPaisServidor(String paisServidor) {
        this.paisServidor = paisServidor;
    }

    @Override
    public String toString() {
        return this.nombreServidor + " : " + this.ipServidor;
    }
}
