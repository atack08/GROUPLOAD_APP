package BEANS;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Grupo implements Serializable {

    private String alias;
    private String password;
    private ArrayList<Cliente> listaClientes;
    private int participacion; //el porcentaje de descarga ya asignado
    private String recurso;
    static final long serialVersionUID =1L;

    //VARIABLE QUE NOS DICE SI EL RECURSO YA ESTA DESCARGADO EN SERVIDOR
    private boolean recursoDescargado;

    public Grupo(String alias, String password, String recurso) {
    	
        this.alias = alias;
        this.password = password;
        this.listaClientes = new ArrayList<>();
        this.recurso = recurso;
        this.recursoDescargado =  false;

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

    public String getRecurso() {
        return this.recurso;
    }

    public void setRecuso(String recurso) {
        this.recurso = recurso;
    }
    
    

    public boolean isRecursoDescargado() {
		return recursoDescargado;
	}

	public void setRecursoDescargado(boolean recursoDescargado) {
		this.recursoDescargado = recursoDescargado;
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
