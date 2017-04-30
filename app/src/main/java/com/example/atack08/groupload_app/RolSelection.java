package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;

public class RolSelection extends AppCompatActivity {

    private Spinner spinnerPorcentajeDescarga;
    private Servidor servidor;
    private Usuario usuario;

    //LISTA CON LOS GRUPOS ACTIVOS EN EL SERVIDOR CONECTADO
    private ArrayList<Grupo> listaG;
    private ListView listaGrupos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rol_selection);

        //RECUPERAMOS DEL INTENT EL USUARIO Y EL SERVIDOR LOGEADOS
        Intent intent = getIntent();
        servidor = (Servidor)intent.getExtras().getSerializable("servidor");
        usuario = (Usuario)intent.getExtras().getSerializable("usuario");

        mostrarPanelInfo(usuario.getNombre() + " conectado correctamente al servidor "
                + servidor.getNombreServidor());
        

        //RECUPERAMOS EL SPINNER PARA EL PORCENTAJE DE DESCARGA
        Integer[] nums =new Integer[9];
        rellenarNumerosSpinner(nums);

        ArrayAdapter<Integer> porcentajes = new ArrayAdapter<Integer>(this,R.layout.support_simple_spinner_dropdown_item,nums);
        spinnerPorcentajeDescarga = (Spinner) findViewById(R.id.spinnerPorcentajeDescarga);
        spinnerPorcentajeDescarga.setAdapter(porcentajes);

        //CARGAMOS LA LISTA DE GRUPOS DISPONIBLES EN EL SERVIDOR
        pedirListaGruposServidor();
        //CARGAMOS LA LISTA EN EL LISTVIEW
        AdaptadorGrupos adaptador = new AdaptadorGrupos(this,listaG);
        listaGrupos = (ListView) findViewById(R.id.listaGrupos);
        listaGrupos.setAdapter(adaptador);



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

        listaG.add(g1); listaG.add(g2); listaG.add(g3); listaG.add(g4);listaG.add(g4);listaG.add(g4);listaG.add(g4);


    }


    //MÉTODO QUE MUESTRA EL FORMULARIO CREACION DE GRUPO
    public void formularioCreacionGrupo(View v){

        //Hacemos visible el formulario creación y ocultamos el formulario unión
        ((LinearLayout)findViewById(R.id.formularioCreacion)).setVisibility(View.VISIBLE);
        ((LinearLayout)findViewById(R.id.formularioUnion)).setVisibility(View.INVISIBLE);
    }

    //MÉTODO QUE MUESTRA EL FORMULARIO UNIÓN A UN GRUPO
    public void formularioUnionGrupo(View v){

        //Hacemos visible el formulario union y ocultamos el formulario creación
        ((LinearLayout)findViewById(R.id.formularioCreacion)).setVisibility(View.INVISIBLE);
        ((LinearLayout)findViewById(R.id.formularioUnion)).setVisibility(View.VISIBLE);
    }

    //MÉTODO QUE CONFIGURA UN ARRAY CON LOS DIFERENTES PORCENTAJES DE DESCARGA
    public void rellenarNumerosSpinner(Integer[] nums){
        int n = 10;
        for(int i=0; i<nums.length; i++){
            nums[i]=n;
            n = n+10;
        }
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
    class AdaptadorGrupos extends ArrayAdapter<Grupo>{


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
