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

public class UnirseGrupo extends AppCompatActivity {

    //VARIABLES DE SESIÓN
    private Servidor servidor;
    private Usuario usuario;

    //LISTA CON LOS GRUPOS ACTIVOS EN EL SERVIDOR CONECTADO
    private ArrayList<Grupo> listaG;
    private ListView listaGrupos;

    //GRUPO SELECCIONADO DE LA LISTA
    private Grupo grupoSeleccionado;
    private TextView labelGrupoSeleccionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unirse_grupo);

        /*//RECUPERAMOS DEL INTENT EL USUARIO Y EL SERVIDOR LOGEADOS
        Intent intent = getIntent();
        servidor = (Servidor)intent.getExtras().getSerializable("servidor");
        usuario = (Usuario)intent.getExtras().getSerializable("usuario");*/

        //RESCATAMOS EL LABEL DEL GRUPO SELECCIONADO
        labelGrupoSeleccionado = (TextView)findViewById(R.id.labelGrupoSeleccionado);

        //CARGAMOS LA LISTA DE GRUPOS DISPONIBLES EN EL SERVIDOR
        pedirListaGruposServidor();
        //CARGAMOS LA LISTA EN EL LISTVIEW
        AdaptadorGrupos adaptador = new AdaptadorGrupos(this,listaG);
        listaGrupos = (ListView) findViewById(R.id.listViewGrupos);
        listaGrupos.setAdapter(adaptador);

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
    public void pedirListaGruposServidor(){

        listaG = new ArrayList<>();
        Grupo g1 = new Grupo("Metero 25 alpha", "esteropa");
        Grupo g2 = new Grupo("Farsantes Madrid ","dfddfdf");
        Grupo g3 = new Grupo("Farsantes Barcelona ","dfddfdf");
        Grupo g4 = new Grupo("Gruo Bichos 22 ","dfddfdf");
        Grupo g5 = new Grupo("Gruo Bichos 22 ","dfddfdf");
        Grupo g6 = new Grupo("Gruo Bichos 22 ","dfddfdf");
        Grupo g7 = new Grupo("Gruo Bichos 22 ","dfddfdf");

        listaG.add(g1); listaG.add(g2); listaG.add(g3); listaG.add(g4);


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
            lblNumUsuarios.setText("Número de usuarios: 4");// + listaG.get(position).getListaClientes().size());

            TextView lblPorcentaje = (TextView)item.findViewById(R.id.listaLabelPorcentajeLibre);
            lblPorcentaje.setText("Porcentaje asignado: 80%"); //+ listaG.get(position).getParticipacion() + " %");


            return (item);
        }
    }
}
