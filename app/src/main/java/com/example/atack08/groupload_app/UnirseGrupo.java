package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.acl.Group;
import java.util.ArrayList;

import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;
import HILOS_SERVICIOS.Lista_Grupos_Servidor;

public class UnirseGrupo extends AppCompatActivity {

    //VARIABLES DE SESIÓN
    private Servidor servidor;
    private Usuario usuario;
    private OperacionesInternet oi;

    //LISTA CON LOS GRUPOS ACTIVOS EN EL SERVIDOR CONECTADO
    private ArrayList<Grupo> listaG;
    private ListView listaGrupos;
    private AdaptadorGrupos adaptadorLista;

    //GRUPO SELECCIONADO DE LA LISTA
    private Grupo grupoSeleccionado;
    private TextView labelGrupoSeleccionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unirse_grupo);

        //RECUPERAMOS DEL INTENT EL USUARIO Y EL SERVIDOR LOGEADOS
        Intent intent = getIntent();
        servidor = (Servidor)intent.getExtras().getSerializable("servidor");
        usuario = (Usuario)intent.getExtras().getSerializable("usuario");

        //RESCATAMOS EL LABEL DEL GRUPO SELECCIONADO
        labelGrupoSeleccionado = (TextView)findViewById(R.id.labelGrupoSeleccionado);

        //CARGAMOS LA LISTA DE GRUPOS DISPONIBLES EN EL SERVIDOR
        listaGrupos = (ListView) findViewById(R.id.listViewGrupos);
        pedirListaGruposServidor(null);

        //AÑADIMOS EL ESCUCHADOR A LA LISTVIEW
        listaGrupos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                grupoSeleccionado = (Grupo) parent.getItemAtPosition(position);
                labelGrupoSeleccionado.setText(grupoSeleccionado.getAlias());

            }
        });

    }


    //MÉTODO QUE PEDIRÁ AL SERVIDOR LA LISTA DE GRUPOS
    public void pedirListaGruposServidor(View v){

        //EJECUTAMOS TAREA ASINCRONA PARA PEDIR AL SERVIDOR LA LISTA DE GRUPOS
        Lista_Grupos_Servidor  tareaGrupos= new Lista_Grupos_Servidor(usuario,servidor,this);
        tareaGrupos.execute();

    }

    //METODO PARA CONFECCIONAR LA VISTA DE LA LISTA
    public void configurarVistaLista(ArrayList<Grupo> listaG){

        Toast.makeText(this, "LISTA CONTIENE " + String.valueOf(listaG.size()) + " GRUPOS", Toast.LENGTH_LONG).show();
        this.listaG = listaG;
        adaptadorLista =  new AdaptadorGrupos(this,listaG);
        listaGrupos.setAdapter(adaptadorLista);

    }


    //MÉTODO PARA MOSTRAR LOS DIALOGS DE ERROR
    public void mostrarPanelError(String msg){

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(msg);
        dialog.setTitle("ERROR");

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        dialog.show();

    }

    //MÉTODO PARA MOSTRAR INFO
    public void mostrarPanelInfo(String msg){

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(msg);
        dialog.setTitle("INFO");

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        dialog.show();

    }

    //CLASE INTERNA PARA MANEJAR LA LISTA DE GRUPOS
    class AdaptadorGrupos extends ArrayAdapter<Grupo> {

        public AdaptadorGrupos(Context context, ArrayList<Grupo> listaG) {
            super(context, R.layout.listitem_grupo, listaG);
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View item = inflater.inflate(R.layout.listitem_grupo, null);

            TextView lblNombre = (TextView)item.findViewById(R.id.listaLabelNombreGrupo);
            lblNombre.setText(listaG.get(position).getAlias());

            TextView lblNumUsuarios = (TextView)item.findViewById(R.id.listaLabelNumeroUsuario);
            lblNumUsuarios.setText("Número de usuarios: " + listaG.get(position).getListaClientes().size());

            TextView lblPorcentaje = (TextView)item.findViewById(R.id.listaLabelPorcentajeLibre);
            lblPorcentaje.setText("Porcentaje asignado: " + listaG.get(position).getParticipacion() + " %");


            return (item);
        }
    }

}
