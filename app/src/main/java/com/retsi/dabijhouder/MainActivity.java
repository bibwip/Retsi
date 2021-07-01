package com.retsi.dabijhouder;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.BuildConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends BaseActivity{

    public static final String apkURl = "https://drive.google.com/drive/folders/1lVLj9Ucl-RdRX9ABggtomy_8Hi8nsf1y?usp=sharing";
    Intent shareIntent;

    DatabaseHelper myDb;
    FloatingActionButton btnGotoAddAssignment;

    RecyclerAdapter mAdapter;

    SharedPreferences prefs = null;

    OpdrachtItem opdrachtbackup;
    boolean clicked;

    CheckBox chAll, chHuiswerk, chEindopdracht, chToets, chOverig;
    ImageButton imgBtnClose, imgBtnOpen;
    ImageView lijn, lijn2;
    ConstraintLayout filtermenu;
    ArrayList<String> chosenFilters = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstRun();
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
        prefs = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);

        btnGotoAddAssignment = findViewById(R.id.btn_goto_add_assignment);

        btnGotoAddAssignment.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddAssignmentAcitvity.class);
            startActivity(intent);
        });

        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.deelText) + apkURl);
        shareIntent.setType("text/plain");

        RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerAdapter(SetData(), myDb);
        recyclerView.setAdapter(mAdapter);

        if (getIntent().hasExtra("refresh")) {
            if (getIntent().getBooleanExtra("refresh", false)) mAdapter.notifyDataSetChanged();
        }

        initViews();
        CheckBoxesSetup();

        mAdapter.setClickListener(new RecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (v.getId() == R.id.img_btn_check_opdracht){
                    opdrachtbackup = mAdapter.getItem(position);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayoutForMain),
                            R.string.deleted_opdracht, BaseTransientBottomBar.LENGTH_LONG);
                    snackbar.setAction(android.R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                             clicked = true;
                             mAdapter.InsertItem(opdrachtbackup, position);
                        }
                    });
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (clicked){
                                clicked = false;
                            } else {
                                myDb.deleteOpdracht(opdrachtbackup.getVakNaam(),
                                        opdrachtbackup.getTitel());
                                Intent updateWidgetIntent = new Intent();
                                updateWidgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                                sendBroadcast(updateWidgetIntent);
                            }
                        }
                    });
                    snackbar.show();
                    mAdapter.RemoveItem(position);
                } else {
                    if (!mAdapter.getItem(position).getBeschrijving().equals("")) {
                        mAdapter.getItem(position).setExpanded(!mAdapter.getItem(position).isExpanded());
                        mAdapter.notifyItemChanged(position);
                    }
                }
            }
        });

        mAdapter.setLongClickListener(new RecyclerAdapter.LongItemClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.opdracht_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_edit_opdracht:
                                Intent intent = new Intent(MainActivity.this, EditOpdrachtActivity.class);
                                OpdrachtItem item = mAdapter.getItem(position);
                                intent.putExtra("ID", myDb.getId(item.getTitel(), item.getVakNaam(), item.getDatum()));
                                startActivity(intent);
                                return true;
                            case R.id.action_share_opdracht:
                                Uri.Builder builder = new Uri.Builder();
                                builder.scheme("https")
                                        .authority("www.github.com")
                                        .path("/winkelkar/Retsi")
                                        .appendQueryParameter(getString(R.string.TypeOpdracht),
                                                mAdapter.getItem(position).getTypeOpdracht_key())
                                        .appendQueryParameter(getString(R.string.Titel),
                                            mAdapter.getItem(position).getTitel())
                                        .appendQueryParameter(getString(R.string.vaknaam),
                                            mAdapter.getItem(position).getVakNaam())
                                        .appendQueryParameter(getString(R.string.datum),
                                                mAdapter.getItem(position).getDatum())
                                        .appendQueryParameter(getString(R.string.beschrijving),
                                                mAdapter.getItem(position).getBeschrijving());
                                String url = builder.build().toString();
                                Intent shareItem = new Intent();
                                shareItem.setAction(Intent.ACTION_SEND)
                                        .putExtra(Intent.EXTRA_TEXT, url)
                                        .setType("text/plain");
                                Intent sendIntent = Intent.createChooser(shareItem, null);
                                startActivity(sendIntent);
                                return true;
                            default:
                                return true;
                        }

                    }
                });
                popup.show();
            }
        });

        Uri data = getIntent().getData();
        if (data != null) {
            String vak = data.getQueryParameter(getString(R.string.vaknaam));
            String type = data.getQueryParameter(getString(R.string.TypeOpdracht));
            String titel = data.getQueryParameter(getString(R.string.Titel));
            String datum = data.getQueryParameter(getString(R.string.datum));
            String bes = data.getQueryParameter(getString(R.string.beschrijving));
            boolean inlist = false;
            for (VakItem item: myDb.getAllData2()) {
                if (vak.equals(item.getVaknaam())) {
                    inlist = true;
                    myDb.insertData(type, vak, titel, datum, bes);
                    mAdapter.UpdateItems(SetData());
                }
            }

            if (!inlist) {
                Toast.makeText(this, "ja", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.TypeOpdracht), type);
                bundle.putString(getString(R.string.Titel), titel);
                bundle.putString(getString(R.string.vaknaam), vak);
                bundle.putString(getString(R.string.datum), datum);
                bundle.putString(getString(R.string.beschrijving), bes);
                Intent intent = new Intent(this, ImportOpdracht.class)
                        .putExtra("extra", bundle);
                startActivity(intent);
            }
        }

    }



    public void initViews() {
        chAll = findViewById(R.id.checkbox_all);
        chHuiswerk =findViewById(R.id.checkBox_huiswerk);
        chEindopdracht =findViewById(R.id.checkBox_eindopdracht);
        chToets = findViewById(R.id.checkBox_toets);
        chOverig = findViewById(R.id.checkBox_overig);
        imgBtnClose = findViewById(R.id.img_btn_close_menu);
        filtermenu = findViewById(R.id.main_filters);
        lijn = findViewById(R.id._____lijn2);
        lijn2 = findViewById(R.id._____lijn3);
        imgBtnOpen = findViewById(R.id.img_btn_open_menu);
    }


    public ArrayList<OpdrachtItem> SetData() {

        ArrayList<String> filters = chosenFilters;

        if (filters.size() == 0) {
            filters.add(getString(R.string.Eindopdracht_key));
            filters.add(getString(R.string.Huiswerk_key));
            filters.add(getString(R.string.Toets_key));
            filters.add(getString(R.string.overig_key));
        }

        ArrayList<OpdrachtItem> items = new ArrayList<>();
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            return items;
        }

        while (res.moveToNext()) {
            String typeOpdracht = res.getString(1);

            if (filters.contains(typeOpdracht)) {
                String vak = res.getString(2);
                String titel = res.getString(3);
                String datum = res.getString(4);
                String bescrhijving = res.getString(5);
                String TypeKey = res.getString(1);

                switch (typeOpdracht) {
                    case "Toets_key":
                        typeOpdracht = getString(R.string.Toets);
                        break;
                    case "eindopdracht_key":
                        typeOpdracht = getString(R.string.Eindopdracht);
                        break;
                    case "Huiswerk_key":
                        typeOpdracht = getString(R.string.Huiswerk);
                        break;
                    case "overig_key":
                        typeOpdracht = getString(R.string.overig);
                    default:
                        break;
                    }

                    String[] SList = datum.split("-");
                    int DatumKey = Integer.parseInt(SList[2] + SList[1] + SList[0]);
                    OpdrachtItem opdracht = new OpdrachtItem(typeOpdracht, vak, titel, datum, bescrhijving, DatumKey, TypeKey);
                    items.add(opdracht);

                }
            }

        Collections.sort(items, new Comparator<OpdrachtItem>() {
            @Override
            public int compare(OpdrachtItem opdrachtItem, OpdrachtItem t1) {
                return opdrachtItem.getDatumTagSorter().compareTo(t1.getDatumTagSorter());
            }
        });
        return items;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, StartupActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_share:
                Intent sendIntent = Intent.createChooser(shareIntent, null);
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            Intent intent = new Intent(this, StartupActivity.class);
            finish();
            startActivity(intent);

        } else if (currentVersionCode > savedVersionCode) {

            Intent intent = new Intent(this, StartupActivity.class);
            finish();
            startActivity(intent);
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    public void notifyDataAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    public void CheckBoxesSetup() {
        chAll.setChecked(true);
        chAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chEindopdracht.setChecked(false);
                    chToets.setChecked(false);
                    chHuiswerk.setChecked(false);
                    chOverig.setChecked(false);
                    chosenFilters.clear();
                    mAdapter.UpdateItems(SetData());
                } else {
                    chosenFilters.clear();
                }
            }
        });
        chEindopdracht.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (chAll.isChecked()) chAll.setChecked(false);
                    chosenFilters.add(getString(R.string.Eindopdracht_key));
                    mAdapter.UpdateItems(SetData());
                } else {
                    chosenFilters.remove(getString(R.string.Eindopdracht_key));
                    if (chosenFilters.size() == 0) {
                        chAll.setChecked(true);
                    }
                    mAdapter.UpdateItems(SetData());
                }
            }
        });
        chToets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (chAll.isChecked()) chAll.setChecked(false);
                    chosenFilters.add(getString(R.string.Toets_key));
                    mAdapter.UpdateItems(SetData());
                } else {
                    chosenFilters.remove(getString(R.string.Toets_key));
                    if (chosenFilters.size() == 0) {
                        chAll.setChecked(true);
                    }
                    mAdapter.UpdateItems(SetData());
                }
            }
        });
        chHuiswerk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (chAll.isChecked()) chAll.setChecked(false);
                    chosenFilters.add(getString(R.string.Huiswerk_key));
                    mAdapter.UpdateItems(SetData());
                } else {
                    chosenFilters.remove(getString(R.string.Huiswerk_key));
                    if (chosenFilters.size() == 0) {
                        chAll.setChecked(true);
                    }
                    mAdapter.UpdateItems(SetData());
                }
            }
        });
        chOverig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (chAll.isChecked()) chAll.setChecked(false);
                    chosenFilters.add(getString(R.string.overig_key));
                    mAdapter.UpdateItems(SetData());
                } else {
                    chosenFilters.remove(getString(R.string.overig_key));
                    if (chosenFilters.size() == 0) {
                        chAll.setChecked(true);
                    }
                    mAdapter.UpdateItems(SetData());
                }
            }
        });
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtermenu.setVisibility(View.GONE);
                imgBtnOpen.setVisibility(View.VISIBLE);
                lijn.setVisibility(View.GONE);
                lijn2.setVisibility(View.VISIBLE);
            }
        });
        imgBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtermenu.setVisibility(View.VISIBLE);
                imgBtnOpen.setVisibility(View.GONE);
                lijn.setVisibility(View.VISIBLE);
                lijn2.setVisibility(View.GONE);
            }
        });
    }
}