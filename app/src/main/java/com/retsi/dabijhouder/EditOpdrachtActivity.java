package com.retsi.dabijhouder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditOpdrachtActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    int opId;
    OpdrachtItem editItem;

    Spinner vakkenSpinner, opdrachtSpinner;
    EditText edtTitel, edtBeschtijving;
    DatePicker mDatePicker;
    TextView tv_datum, tv_vak, tv_Type, tv_titel, tv_beschrijving, tv_datum_label;
    Button btnSave, btnPickDate, btnOk;

    String vak, titel, type, beschrijving, datum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_opdracht);
        myDb = new DatabaseHelper(this);
        if (getIntent().hasExtra("ID")){
            opId = getIntent().getIntExtra("ID", -1);
            if (opId != -1){
                editItem = myDb.getOpdracht(opId);
            }
        }


        vak = editItem.getVakNaam();
        titel = editItem.getTitel();
        type = editItem.getTypeOpdracht();
        beschrijving = editItem.getBeschrijving();
        datum = editItem.getDatum();
        initViews();
        setupButtons();

    }

    private void initViews() {
        vakkenSpinner = findViewById(R.id.editOpVakSpinner);
        opdrachtSpinner = findViewById(R.id.editOpTypeopdracht);
        edtTitel = findViewById(R.id.editOpTitel);
        edtBeschtijving = findViewById(R.id.editOpBeschrijving);
        mDatePicker = findViewById(R.id.editOpDatePicker);
        tv_datum = findViewById(R.id.editOpDatumtv);
        btnPickDate = findViewById(R.id.editOpbtnKiesDate);
        btnSave = findViewById(R.id.editOpSave);
        btnOk = findViewById(R.id.editOpok);

        tv_vak = findViewById(R.id.tv_edit_vak);
        tv_Type = findViewById(R.id.tv_edit_type);
        tv_titel = findViewById(R.id.tv_edit_titel);
        tv_beschrijving = findViewById(R.id.textView6);
        tv_datum_label = findViewById(R.id.textView7);

        edtTitel.setText(editItem.getTitel());
        edtBeschtijving.setText(editItem.getBeschrijving());
        tv_datum.setText(editItem.getDatum());

        ArrayAdapter<CharSequence> vakSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.typeOpdrachten, android.R.layout.simple_spinner_item);
        opdrachtSpinner.setAdapter(vakSpinnerAdapter);

        ArrayList<String> list = myDb.getVakkenNamen();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_vak_item, R.id.tvsimplevaknaamitemfrancaishierook, list);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_vak_item);
        vakkenSpinner.setAdapter(spinnerAdapter);
        int index = -1;
        if (editItem.getTypeOpdracht().equals(getString(R.string.Toets_key))) index = 1;
        else if (editItem.getTypeOpdracht().equals(getString(R.string.Eindopdracht_key))) index = 2;
        else if (editItem.getTypeOpdracht().equals(getString(R.string.Huiswerk_key))) index =0;

        opdrachtSpinner.setSelection(index);
        vakkenSpinner.setSelection(getIndex(list, editItem.getVakNaam()));

        String[] dateSplit = editItem.getDatum().split("-");
        mDatePicker.init(Integer.parseInt(dateSplit[2]), Integer.parseInt(dateSplit[1]) -1, Integer.parseInt(dateSplit[0]), null);
    }

    private int getIndex(ArrayList<String> list, String item) {
        int index = -1;
        for (int i=0;i<list.size();i++) {
            if (list.get(i).equals(item)) {
                index = i;
                return index;
            }
        }
        return index;
    }

    private void setupButtons() {
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vakkenSpinner.setVisibility(View.GONE);
                opdrachtSpinner.setVisibility(View.GONE);
                edtTitel.setVisibility(View.GONE);
                edtBeschtijving.setVisibility(View.GONE);
                tv_datum.setVisibility(View.GONE);
                btnPickDate.setVisibility(View.GONE);
                mDatePicker.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                tv_vak.setVisibility(View.GONE);
                tv_Type.setVisibility(View.GONE);
                tv_titel.setVisibility(View.GONE);
                tv_beschrijving.setVisibility(View.GONE);
                tv_datum_label.setVisibility(View.GONE);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vakkenSpinner.setVisibility(View.VISIBLE);
                opdrachtSpinner.setVisibility(View.VISIBLE);
                edtTitel.setVisibility(View.VISIBLE);
                edtBeschtijving.setVisibility(View.VISIBLE);
                tv_datum.setVisibility(View.VISIBLE);
                btnPickDate.setVisibility(View.VISIBLE);
                mDatePicker.setVisibility(View.GONE);
                btnOk.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                tv_vak.setVisibility(View.VISIBLE);
                tv_Type.setVisibility(View.VISIBLE);
                tv_titel.setVisibility(View.VISIBLE);
                tv_beschrijving.setVisibility(View.VISIBLE);
                tv_datum_label.setVisibility(View.VISIBLE);

                String dag = String.valueOf(mDatePicker.getDayOfMonth());
                String maand = String.valueOf(mDatePicker.getMonth());

                if (dag.length() == 1) dag = "0"+ String.valueOf(mDatePicker.getDayOfMonth());
                if (maand.length() == 1) maand = "0"+ String.valueOf(mDatePicker.getMonth()+1);

                datum = String.valueOf(dag+"-"+maand+"-"+ String.valueOf(mDatePicker.getYear()));
                tv_datum.setText(datum);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] keys = getResources().getStringArray(R.array.opdrachtkeys);

                type = keys[opdrachtSpinner.getSelectedItemPosition()];
                vak = vakkenSpinner.getSelectedItem().toString();
                titel = edtTitel.getText().toString();
                beschrijving = edtBeschtijving.getText().toString();

                myDb.updateOpdracht(String.valueOf(opId), type, vak, titel, datum, beschrijving);

                Intent intent = new Intent(EditOpdrachtActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}