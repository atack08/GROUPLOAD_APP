package HILOS_SERVICIOS;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import com.example.atack08.groupload_app.UnirFicheros;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by atack08 on 14/5/17.
 */

public class Tarea_Unir_Ficheros extends AsyncTask {

    private ArrayList<String> listaRutas;
    private ArrayList<File> listaFicheros;
    private UnirFicheros uf;
    private String nombreRecurso;
    private long sizeDescarga;
    private ProgressDialog pd;

    private final File CARPETA_PUBLICA_DESCARGAS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    private final int RECURSO_NO_CONCORDANTE = 0;
    private final int UNION_CORRECTA = 1;
    private final int STORAGE_FAIL = 2;
    private int result;


    public Tarea_Unir_Ficheros(ArrayList<String> listaRutas, UnirFicheros uf, ProgressDialog pd) {
        this.listaRutas = listaRutas;
        this.uf = uf;
        this.pd = pd;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        //CREAMOS LISTA FICHEROS
        crearListaFiles();

        //COMPROBAMOS EL ESTADO DE LA MEMORIA EXTERNA - SD
        String estado = Environment.getExternalStorageState();

        if(estado.equals(Environment.MEDIA_MOUNTED) || estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {

            //COMPROBAMOS QUE TODOS LOS FICHEROS SEAN DEL MISMO RECURSO
            if(comprobarFicheros()){

                //CALCULAMOS PORCENTAJE
                float porcentaje = (1024f * 100f) / sizeDescarga;
                float progreso = 0;

                //ABRIMOS STREAMS
                FileInputStream inFile;
                byte[] buffer = new byte[1024];

                try {
                    File ficheroResultante = new File(CARPETA_PUBLICA_DESCARGAS.getAbsolutePath() + "/" + nombreRecurso);
                    FileOutputStream outFile = new FileOutputStream(new File(CARPETA_PUBLICA_DESCARGAS.getAbsolutePath() + "/" + nombreRecurso));
                    int len;

                    for(File file: listaFicheros){
                        inFile = new FileInputStream(file);

                        System.out.println("FICHERO " + listaFicheros.indexOf(file) + ": " + file.getAbsolutePath());

                        while((len = inFile.read(buffer)) > 0){
                            outFile.write(buffer,0,len);

                            progreso = progreso + porcentaje;
                            publishProgress(progreso);
                        }

                        inFile.close();
                    }
                    publishProgress(100f);
                    Thread.sleep(1000);
                    outFile.close();

                    if(ficheroResultante.exists()){
                        System.out.println("RUTA FICHERO RESULTANTE: " + ficheroResultante.getAbsolutePath() );
                        result = UNION_CORRECTA;
                    }
                    else
                        result = -1;


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            else
                result = RECURSO_NO_CONCORDANTE;

        }
        else
            result = STORAGE_FAIL;

        return null;

    }

    //MÉTODO QUE COMPRUEBA QUE TODOS LOS PARTES SEAN DEL MISMO RECURSO
    public boolean comprobarFicheros(){

        String[] r = listaRutas.get(0).split(".part");
        nombreRecurso = r[0];

        for(String ruta: listaRutas){
            if(!ruta.contains(nombreRecurso))
                return false;
        }

        return true;
    }

    //MÉTODO QUE CREAR UN ARRAYLIST CON LOS FICHEROS
    //CALCULA EL TAMAÑO DE EL TOTAL DE LOS FICHEROS
    public void crearListaFiles(){

        //ORDENAMOS LA LISTA POR NOMBRE
        Collections.sort(listaRutas);

        listaFicheros = new ArrayList<>();
        sizeDescarga = 0;
        File fTemp;

        for(String ruta: listaRutas){
            fTemp = new File(CARPETA_PUBLICA_DESCARGAS + "/" + ruta);
            sizeDescarga = sizeDescarga + fTemp.length();
            listaFicheros.add(fTemp);

        }
    }

    @Override
    protected void onProgressUpdate(Object[] values) {

        int progreso = (int)((float)values[0]);
        pd.setProgress(progreso);

        if(progreso == 100)
            pd.cancel();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //CONFIGURAMOS EL PROGRESS DIALOG
        pd =  new ProgressDialog(uf);
        pd.setCancelable(false);
        pd.setTitle("Uniendo ficheros...");
        pd.setMessage("Uniendo");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        pd.setProgress(0);
        pd.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        switch (result){
            case RECURSO_NO_CONCORDANTE:
                uf.mostrarPanelError("Los ficheros deben ser del mismo recurso.");
                break;
            case STORAGE_FAIL:
                uf.mostrarPanelError("El medio de almacenamiento externo no está disponible.");
                break;
            case UNION_CORRECTA:
                uf.mostrarPanelInfo("La unión se realizó correctamente, el fichero resultante se ha almacenado en su carpeta pública de DESCARGAS.");
                break;
            default:
                uf.mostrarPanelError("No se pudo completar la unión de ficheros.");
        }
    }
}
