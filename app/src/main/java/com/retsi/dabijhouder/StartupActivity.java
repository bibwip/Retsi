package com.retsi.dabijhouder;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.Locale;

public class StartupActivity extends BaseActivity implements VakkenListAdapter.ItemClickListener {

    Spinner spinner_talen;
    Button btn_save, btn_add;
    EditText mEditText_vak;
    RecyclerView mRecycler_Vakken;

    SharedPreferences pref;
    DatabaseHelper myDb;

    String[] talen;

    String vaknaam;
    String vakKleur;

    VakkenListAdapter adapter;
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        initViews();
        talen = new String[]{getString(R.string.engels), getString(R.string.nederlands)};
        myDb = new DatabaseHelper(this);
        pref = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);

        mRecycler_Vakken.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VakkenListAdapter(myDb.getAllData2());
        adapter.setClickListener(this);
        mRecycler_Vakken.setAdapter(adapter);

        spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, talen);
        spinner_talen.setAdapter(spinnerAdapter);

        spinner_talen.setSelection(getLanguage());

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaknaam = mEditText_vak.getText().toString().substring(0, 1).toUpperCase()
                        + mEditText_vak.getText().toString().substring(1);
                myDb.insertData(vaknaam, getResources().getString(0 + R.color.background));
                adapter.updateData(myDb.getAllData2());
                mEditText_vak.setText("");

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner_talen.getSelectedItem().toString().equals(getString(R.string.nederlands)))
                    setLocale("nl");
                else if (spinner_talen.getSelectedItem().toString().equals(getString(R.string.engels)))
                    setLocale("en");
                Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                intent.putExtra("refresh", true);
                finish();
                startActivity(intent);
            }
        });

    }


    public void initViews() {
        spinner_talen = findViewById(R.id.spinner_talen);
        btn_save = findViewById(R.id.btn_save_startup_options);
        btn_add = findViewById(R.id.btn_add_vak);
        mEditText_vak = findViewById(R.id.edtTxt_vaknaam);
        mRecycler_Vakken = findViewById(R.id.Recycler_vakken);

    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(LANGUAGE_KEY, lang).apply();
    }

    public int getLanguage() {
        Resources res = getResources();
        String lang = res.getConfiguration().locale.toString();
        if (lang.equals("en") || lang.equals("en_US")) return 0;
        else if (lang.equals("nl")) return 1;
        else return 0;
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        if (id == R.id.tv_color_picker || id == R.id.image_color_picker) {
            ColorPickerDialogBuilder
                    .with(StartupActivity.this)
                    .setTitle("Choose color")
                    .initialColor(R.color.white)
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int selectedColor) {
                        }
                    })
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            myDb.updateVakkenData(adapter.getItem(position).getVaknaam(), "#" + Integer.toHexString(selectedColor));
                            adapter.updateData(myDb.getAllData2());
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .build()
                    .show();
        } else if (id == R.id.tv_delete_vak || id == R.id.image_delete_vak) {
            myDb.deleteVak(adapter.getItem(position).getVaknaam(), adapter.getItem(position).getVakColor());
            adapter.updateData(myDb.getAllData2());
        }
    }
}