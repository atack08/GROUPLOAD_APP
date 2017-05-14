package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import HILOS_SERVICIOS.Tarea_Unir_Ficheros;

public class UnirFicheros extends AppCompatActivity {

    private Spinner spinnerFicheros;
    private File[] ficherosPorc;
    private ArrayList<String> rutasFicheros;
    private ListView vistaListaFicheros;
    private ArrayList<String> listaF;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unir_ficheros);

        //RESCATAMOS VARIABLES DE LA INTERFACE GRÁFICA
        listaF = new ArrayList<>();
        vistaListaFicheros = (ListView) findViewById(R.id.listaFicherosSeleccionados);
        spinnerFicheros = (Spinner)findViewById(R.id.spinnerJuntarFicheros);
        rellenarFicherosPorc();

    }


    //MÉTODO QUE LISTA LOS FICHEROS .PORC DE LA CARPETA DESCARGAS DE LA MEMORIA EXTERNA
    public void rellenarFicherosPorc(){

        //COMPROBAMOS EL ESTADO DE LA MEMORIA EXTERNA - SD
        String estado = Environment.getExternalStorageState();

        if(estado.equals(Environment.MEDIA_MOUNTED) || estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {


            File rutaDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            ficherosPorc = rutaDownloads.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".porc");
                }
            });

            rutasFicheros = new ArrayList<>();

            for(File file: ficherosPorc){
                rutasFicheros.add(file.getName());
            }

            ArrayAdapter<String> adaptador1 = new ArrayAdapter<String>(this,R.layout.spinner_ficheros_item,rutasFicheros);
            spinnerFicheros.setAdapter(adaptador1);

        }
        else
            mostrarPanelError("No se puede leer en la tarjeta SD");

    }

    //MÉTODO QUE LISTA LOS FICHEROS .PORC DE LA CARPETA DESCARGAS DE LA MEMORIA EXTERNA
    public void rellenarListaFicheros(){

        ArrayAdapter<String> adaptador2 = new ArrayAdapter<String>(this,R.layout.spinner_ficheros_item,listaF);
        vistaListaFicheros.setAdapter(adaptador2);

    }

    public void clear(View v){
        listaF = new ArrayList<>();
        rellenarListaFicheros();
    }

    public void add(View v){
        listaF.add((String) spinnerFicheros.getSelectedItem());
        rellenarListaFicheros();
    }

    //MÉTODO QUE REALIZA LA TAREA ASINCRONA PARA JUNTAR LOS FICHEROS
    public void lanzarTareaUnirFicheros(View v){

       Tarea_Unir_Ficheros tUf = new Tarea_Unir_Ficheros(listaF,this,pd);
        tUf.execute();
    }

    public void volver(View v){
        this.finish();
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
}
