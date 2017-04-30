package BEANS;


import java.io.Serializable;
import java.util.ArrayList;

public class Grupo implements Serializable {

    private String alias;
    private String password;
    private ArrayList<Cliente> listaClientes;
    private int participacion; //el porcentaje de descarga ya asignado

    public Grupo(String alias, String password) {
        this.alias = alias;
        this.password = password;
        this.listaClientes = new ArrayList<>();

        asignarParticipacion();
    }

    //MÉTODO PARA CALCULAR LA PARTICIPACIÓN DE DESCARGA YA ASIGNADA ENTRE LOS CLIENTES
    //REPRESENTA EL PORCENTAJE DE LA DESCARGA YA ASIGNADO A LOS CLIENTES
    public void asignarParticipacion(){

        this.participacion = 0;

        for(Cliente c: listaClientes){
            this.participacion = this.participacion + c.getPorcentaje_descarga();
        }
    }

    //MÉTODO PARA AÑADIR UN CLIENTE A LA LISTA DE GRUPO
    public void aniadirCliente(Cliente c){
        this.listaClientes.add(c);
        asignarParticipacion();
    }

    //MÉTODO PARA CAMBIAR EL PORCENTAJE DE DESCARGA A UN CLIENTE UNA VEZ YA UNIDO AL GRUPO
    public boolean cambiarPorcentajeDescarga(Cliente cliente1, int nuevoPorcentaje){
        if(!this.listaClientes.isEmpty()){
            for(Cliente c:listaClientes){
                if(c.equals(cliente1)){
                    if(this.participacion - c.getPorcentaje_descarga() + nuevoPorcentaje <= 100){
                        c.setPorcentaje_descarga(nuevoPorcentaje);
                        asignarParticipacion();
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }


    public String getAlias() {
        return alias;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Cliente> getListaClientes() {
        return listaClientes;
    }

    public int getParticipacion() {
        return participacion;
    }
}
