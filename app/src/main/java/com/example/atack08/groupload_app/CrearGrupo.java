package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;
import HILOS_SERVICIOS.Creacion_Grupo;
import HILOS_SERVICIOS.Subida_Recurso;

public class CrearGrupo extends AppCompatActivity {

    private Servidor servidor;
    private Usuario usuario;
    private ArrayList<String> nombresTorrents;
    private File[] listaTorrents;
    private Spinner spinnerRecursos;
    private EditText editNombreGrupo, editPassGrupo;
    private File torrentSeleccionado;
    private Button botonSubirRecurso;
    private Grupo nuevoGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_grupo);

        //RECUPERAMOS DEL INTENT EL USUARIO Y EL SERVIDOR LOGEADOS
        Intent intent = getIntent();
        servidor = (Servidor)intent.getExtras().getSerializable("servidor");
        usuario = (Usuario)intent.getExtras().getSerializable("usuario");

        //RECUPERAMOS COMPONENTES DEL FORMULARIO
        editNombreGrupo = (EditText) findViewById(R.id.editNombreCreacionGrupo);
        editPassGrupo = (EditText)findViewById(R.id.editPassCreacionGrupo);
        botonSubirRecurso = (Button) findViewById(R.id.botonSubirRecurso);

        //RECUPERAMOS EL SPINNER DE RECURSO
        //RELLENAMOS LA LISTA CON LOS FICHEROS TORRENT SITUADOS EN LA CARPETA DESCARGAS
        spinnerRecursos = (Spinner)findViewById(R.id.spinnerRecursos);
        rellenarFicherosTorrent();

    }


    //MÉTODO QUE LISTA LOS FICHEROS TORRENT DE LA CARPETA DESCARGAS DE LA MEMORIA EXTERNA
    public void rellenarFicherosTorrent(){

        //COMPROBAMOS EL ESTADO DE LA MEMORIA EXTERNA - SD
        String estado = Environment.getExternalStorageState();

        if(estado.equals(Environment.MEDIA_MOUNTED) || estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {


            File rutaFichero = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            listaTorrents = rutaFichero.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".torrent");
                }
            });


            nombresTorrents = new ArrayList<>();

            for(File file: listaTorrents){
                nombresTorrents.add(file.getName());
            }

            ArrayAdapter<String> adaptadorRecursos = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,nombresTorrents);
            spinnerRecursos.setAdapter(adaptadorRecursos);

        }
        else
            mostrarPanelError("No se puede leer en la tarjeta SD");

    }

    //MÉTODO PARA CREAR EL GRUPO A PARTIR DE LOS SELECCIONADO EN LOS FORMULARIOS
    public void crearGrupo(View v){

        String nombreGrupo = editNombreGrupo.getText().toString();
        String passGrupo = editPassGrupo.getText().toString();
        String nombreR = (String)spinnerRecursos.getSelectedItem();

        if(nombreGrupo.equals("") || passGrupo.equals(""))
            mostrarPanelError("Rellene todo los campos del formulario");
        else{
            //Si no se selecciono recurso
            if(nombreR.equals(""))
                mostrarPanelError("No seleccionó ningún recurso");
            else{

                //SI SE RELLENARON CAMPOS Y SE SELECCIONO RECURSO
                //RESCATAMOS EL FILE A PARTIR DEL NOMBRE

                for(File f: listaTorrents){
                    if(f.getName().equalsIgnoreCase(nombreR)){
                        torrentSeleccionado = f;
                        break;
                    }
                }

                //CREAMOS LA INSTANCIA DE GRUPO QUE ENVIAREMOS AL SERVIDOR
                nuevoGrupo = new Grupo(nombreGrupo,passGrupo,nombreR);

                //INICIAMOS LA TAREA PARA ENVIAR EL GRUPO AL SERVIDOR
                Creacion_Grupo tarea_creacion= new Creacion_Grupo(servidor,nuevoGrupo,torrentSeleccionado,this);
                tarea_creacion.execute();
            }
        }


    }

    //MÉTODO PARA SUBIR EL RECURSO DEL GRUPO
    public void subirRecurso(View v){

        Subida_Recurso tarea_subida = new Subida_Recurso(servidor,nuevoGrupo, torrentSeleccionado,this);
        tarea_subida.execute();
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

    //MÉTODO PARA EL BOTON QUE ACTUALIZA LA LISTA DE FICHEROS
    public void actualizarListaRecursos(View v){

        rellenarFicherosTorrent();
    }

    public void volver(View v){
        finish();

    }

    public Button getBotonSubirRecurso() {
        return botonSubirRecurso;
    }
}
