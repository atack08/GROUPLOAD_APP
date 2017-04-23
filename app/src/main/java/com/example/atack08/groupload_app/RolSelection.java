package com.example.atack08.groupload_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class RolSelection extends AppCompatActivity {

    private Spinner spinnerPorcentajeDescarga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rol_selection);

        //RECUPERAMOS EL SPINNER PARA EL PORCENTAJE DE DESCARGA
        Integer[] nums =new Integer[9];
        rellenarNumerosSpinner(nums);

        ArrayAdapter<Integer> porcentajes = new ArrayAdapter<Integer>(this,R.layout.support_simple_spinner_dropdown_item,nums);
        spinnerPorcentajeDescarga = (Spinner) findViewById(R.id.spinnerPorcentajeDescarga);
        spinnerPorcentajeDescarga.setAdapter(porcentajes);


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
}
