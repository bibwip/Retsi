package com.retsi.dabijhouder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class ImportOpdracht extends BaseActivity {

    Button add, other, save;
    TextView vak;
    Spinner vakken;
    DatabaseHelper myDb;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_opdracht);
        myDb = new DatabaseHelper(this);
        b = getIntent().getBundleExtra("extra");
        vak = findViewById(R.id.import_vak);
        vak.setText(b.getString(getString(R.string.vaknaam)));
        add = findViewById(R.id.import_toevoegen);
        other = findViewById(R.id.import_ander);
        save = findViewById(R.id.import_save);
        vakken = findViewById(R.id.import_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, myDb.getVakkenNamen());
        vakken.setAdapter(spinnerAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.insertData(vak.getText().toString(), "#FFF6F1");
                myDb.insertData(b.getString(getString(R.string.TypeOpdracht)),
                        b.getString(getString(R.string.vaknaam)),
                        b.getString(getString(R.string.Titel)),
                        b.getString(getString(R.string.datum)),
                        b.getString(getString(R.string.beschrijving)));
                finish();
                Intent intent = new Intent(ImportOpdracht.this, MainActivity.class);
                startActivity(intent);
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vakken.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.insertData(b.getString(getString(R.string.TypeOpdracht)),
                        vakken.getSelectedItem().toString(),
                        b.getString(getString(R.string.Titel)),
                        b.getString(getString(R.string.datum)),
                        b.getString(getString(R.string.beschrijving)));
                finish();
                Intent intent1 = new Intent(ImportOpdracht.this, MainActivity.class);
                startActivity(intent1);
            }
        });

    }
}
