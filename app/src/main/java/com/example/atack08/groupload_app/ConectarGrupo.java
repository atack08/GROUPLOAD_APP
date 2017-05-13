package com.example.atack08.groupload_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import BEANS.Cliente;
import BEANS.Grupo;
import BEANS.Servidor;
import BEANS.Usuario;
import HILOS_SERVICIOS.Descarga_partes;

public class ConectarGrupo extends AppCompatActivity {

    private TableLayout tablaGrupo;
    private Usuario usuario;
    private Grupo grupoConectado;
    private Servidor servidor;
    private TextView labelNombreGrupo, labelNombreDescarga;
    private int porcentajeDescarga;
    private boolean desconectar;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectar_grupo);

        this.desconectar = false;

        //RECUPERAMOS DEL INTENT EL USUARIO Y EL SERVIDOR LOGEADOS
        Intent intent = getIntent();
        servidor = (Servidor)intent.getExtras().getSerializable("servidor");
        usuario = (Usuario)intent.getExtras().getSerializable("usuario");
        grupoConectado = (Grupo) intent.getExtras().getSerializable("grupoConectado");
        porcentajeDescarga = intent.getExtras().getInt("porcentaje");

        //RESCATAMOS VARIABLES DE LA INTERFACE GRÁFICA
        tablaGrupo = (TableLayout)findViewById(R.id.tablaGrupo);
        labelNombreGrupo = (TextView)findViewById(R.id.labelNombreGrupoConectado);
        labelNombreGrupo.setText(grupoConectado.getAlias());
        labelNombreDescarga = (TextView)findViewById(R.id.labelNombreDescarga);
        labelNombreDescarga.setText(grupoConectado.getRecurso());

        //CREAMOS LA TABLA
        construirTablaGrupo();

        //COMENZAMOS LA TAREA DE DESCARGA CON EL PORCENTAJE SELECCIONADO
        Descarga_partes tarea_Descarga_Partes = new Descarga_partes(usuario,servidor,grupoConectado,porcentajeDescarga,this,pd);
        tarea_Descarga_Partes.execute();


    }


    //MÉTODO QUE CONSTRUE LA TABLA PARA MOSTRAR LOS INTEGRANTES DEL GUPO AL QUE ESTÁS CONECTADO
    public void construirTablaGrupo(){

        System.out.println("REPINTANDO");

        //LIMPIAMOS LA TABLA
        tablaGrupo.removeAllViews();

        ImageView imagenSmart;

        ArrayList<Cliente> listaC = grupoConectado.getListaClientes();

        TableRow r1 = new TableRow(getApplicationContext());
        r1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT ));

        ImageView[] imagenes = new ImageView[listaC.size()];

        for(int x=0; x<imagenes.length; x++){

            imagenSmart = new ImageView(this);
            imagenSmart.setImageResource(R.mipmap.smartphone);
            imagenSmart.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT ));

            r1.addView(imagenSmart);
       }

        tablaGrupo.addView(r1);

        TableRow r2 = new TableRow(getApplicationContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        r2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        r2.setLayoutParams(params);

        TextView textTemp;

        for(Cliente c: listaC){

            textTemp = new TextView(this);
            TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);

            textTemp.setText(c.getUser().getUser());
            textTemp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textTemp.setTypeface(Typeface.SERIF, Typeface.BOLD);
            params2.setMargins(0,0,15,0);

            textTemp.setLayoutParams(params2);

            r2.addView(textTemp);
        }


        tablaGrupo.addView(r2);

        TableRow r3 = new TableRow(getApplicationContext());
        TableRow.LayoutParams params3 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params3.gravity = Gravity.CENTER;

        r3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        r3.setLayoutParams(params);


        for(Cliente c: listaC){

            textTemp = new TextView(this);
            TableRow.LayoutParams params4 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);

            textTemp.setText(String.valueOf(c.getPorcentaje_descarga() + "%"));
            textTemp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textTemp.setTypeface(Typeface.SERIF, Typeface.BOLD);
            textTemp.setTextColor(Color.BLACK);

            textTemp.setLayoutParams(params4);

            r3.addView(textTemp);
        }


        tablaGrupo.addView(r3);

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

    //GETTER SETTER

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Grupo getGrupoConectado() {
        return grupoConectado;
    }

    public void setGrupoConectado(Grupo grupoConectado) {
        this.grupoConectado = grupoConectado;
    }

    public Servidor getServidor() {
        return servidor;
    }

    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    public int getPorcentajeDescarga() {
        return porcentajeDescarga;
    }

    public boolean isDesconectar() {
        return desconectar;
    }
}
