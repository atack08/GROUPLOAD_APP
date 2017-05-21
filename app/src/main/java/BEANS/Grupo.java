package BEANS;

import java.io.Serializable;
import java.util.ArrayList;

public class Grupo implements Serializable {

    private String alias;
    private String password;
    private ArrayList<Cliente> listaClientes;
    private int participacion; //el porcentaje de descarga ya asignado
    private String recurso;
    private String nombreDescarga;
    static final long serialVersionUID =1L;
    
    //NUMERO DE DESCARGAS YA REALIZADAS EN EL GRUPO
    private int indice;

    //VARIABLES QUE NOS DICE SI EL RECURSO YA ESTA DESCARGADO EN SERVIDOR
    private int estado;

    //CONSTANTES
    public static final int PARADO = 0;
    public static final int DESCARGANDO = 1;
    public static final int DESCARGADO = 2;
    public static final int COMPLETADO = 3;

    public Grupo(String alias, String password, String recurso) {
    	
        this.alias = alias;
        this.password = password;
        this.listaClientes = new ArrayList<>();
        this.recurso = recurso;
        this.estado =  PARADO;
        this.indice = 0;

        asignarParticipacion();
    }

    //MÉTODO PARA CALCULAR LA PARTICIPACIÓN DE DESCARGA YA ASIGNADA ENTRE LOS CLIENTES
    //REPRESENTA EL PORCENTAJE DE LA DESCARGA YA ASIGNADO A LOS CLIENTES
    public synchronized void asignarParticipacion(){

        this.participacion = 0;

        for(Cliente c: listaClientes){
            this.participacion = this.participacion + c.getPorcentaje_descarga();
        }
        
        if(participacion == 100)
            this.setEstado(COMPLETADO);
    }

    //MÉTODO PARA AGREGAR UN CLIENTE A LA LISTA DE GRUPO
    public synchronized void aniadirCliente(Cliente c){
        this.listaClientes.add(c);
        asignarParticipacion();
    }
    
    //MÉTODO PARA BORRAR UN CLIENTE DEL GRUPO
    public synchronized void borrarCliente(Cliente c){
  
    	if(this.listaClientes.contains(c)){
    		
    		this.listaClientes.remove(c);
    		asignarParticipacion();
    	}
    }

    //MÉTODO PARA CAMBIAR EL PORCENTAJE DE DESCARGA A UN CLIENTE UNA VEZ YA UNIDO AL GRUPO
    public synchronized boolean cambiarPorcentajeDescarga(Cliente cliente1, int nuevoPorcentaje){
    	
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


    public synchronized String getAlias() {
        return alias;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized ArrayList<Cliente> getListaClientes() {
        return listaClientes;
    }

    public synchronized int getParticipacion() {
        return participacion;
    }

    public synchronized String getRecurso() {
        return this.recurso;
    }

    public synchronized void setRecuso(String recurso) {
        this.recurso = recurso;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public synchronized int getIndice() {
		return indice;
	}

	public synchronized void setIndice(int indice) {
		this.indice = indice;
	}

    public synchronized String getNombreDescarga() {
        return nombreDescarga;
    }

    public synchronized void setNombreDescarga(String nombreDescarga) {
        this.nombreDescarga = nombreDescarga;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Grupo)) return false;

        Grupo grupo = (Grupo) o;

        return alias != null ? alias.equals(grupo.alias) : grupo.alias == null;

    }

    @Override
    public int hashCode() {
        return alias != null ? alias.hashCode() : 0;
    }
    
  
}
