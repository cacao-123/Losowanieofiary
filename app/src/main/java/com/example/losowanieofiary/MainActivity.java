package com.example.losowanieofiary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText nazwa, liczba_uczniow;
    TextView klasa_label,ilosc_osob_do_wylosowania_label;
    Button dodaj_klase, losuj;

    Spinner klasa, ilosc_osob_do_wylosowania;
    ListView listView;

    TableLayout tableLayout;

    int numberOfStudents;
    int numberOfStudentsToBeDrawn;
    Set<Integer> selectedStudents;
    String selectedClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        dodaj_klase = (Button) findViewById(R.id.dodaj_klase);
        losuj = (Button) findViewById(R.id.losuj);

        klasa = (Spinner) findViewById(R.id.klasa);
        klasa_label = (TextView) findViewById(R.id.klasa_label);

        ilosc_osob_do_wylosowania = (Spinner) findViewById(R.id.ilosc_osob_do_wylosowania);
        ilosc_osob_do_wylosowania_label = (TextView) findViewById(R.id.ilosc_osob_do_wylosowania_label);

        listView = (ListView) findViewById(R.id.wylosowani);

        ilosc_osob_do_wylosowania.setVisibility(View.GONE);
        ilosc_osob_do_wylosowania_label.setVisibility(View.GONE);

        listView.setVisibility(View.GONE);

        klasa_label.setVisibility(View.GONE);
        klasa.setVisibility(View.GONE);


        this.showClasses();
        this.setInitialData(this);
        dodaj_klase.setOnClickListener(view -> onAddClassHandler());
        losuj.setOnClickListener(view -> onDrawClickHandler());

        klasa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                selectedClass = selectedItem;

                if (!selectedItem.equals("Select an item")) {
                    List<String> classesData = getNumberOfStudents(selectedItem);
                    ilosc_osob_do_wylosowania.setVisibility(View.VISIBLE);
                    MainActivity.this.fillSpinner(MainActivity.this, ilosc_osob_do_wylosowania, classesData);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ilosc_osob_do_wylosowania.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                numberOfStudentsToBeDrawn = Integer.parseInt(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @NonNull
    private List<String> getNumberOfStudents(String selectedItem) {
        Cursor res = db.getSelectedClass(selectedItem);
        List<String> classesData = new ArrayList<>();
        if (res.getCount() != 0) {

            if (res.moveToFirst()) {
                do {
                    int classesIndex = res.getColumnIndex("liczba_uczniow");
                    numberOfStudents = Integer.parseInt(res.getString(classesIndex));
                    for (int i = 0; i < numberOfStudents; i++) {
                        classesData.add(String.valueOf(i+1));
                    }
                } while (res.moveToNext());
            }
        }
        return classesData;
    }

    private void onDrawClickHandler() {
        if (numberOfStudentsToBeDrawn != 0) {
            selectedStudents = MainActivity.this.getRandomNumbers(numberOfStudents,numberOfStudentsToBeDrawn);
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, new ArrayList<>(selectedStudents));
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(MainActivity.this, "Wybierz ilość osób do losowania", Toast.LENGTH_SHORT).show();
        }
    }

    private void setInitialData(Context context) {
        Cursor res = db.getAllClasses();
        if (res.getCount() == 0) {
            Toast.makeText(MainActivity.this, "Brak klas do losowania", Toast.LENGTH_SHORT).show();
        } else {
            List<String> classes = this.getClassesList();
            this.fillSpinner(context, klasa, classes);
            klasa.setVisibility(View.VISIBLE);
            klasa_label.setVisibility(View.VISIBLE);

            ilosc_osob_do_wylosowania.setVisibility(View.VISIBLE);
            ilosc_osob_do_wylosowania_label.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    protected void showClasses() {
        tableLayout = findViewById(R.id.lista_klas);
        TableRow.LayoutParams textViewLayout = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1
        );

        Cursor data = db.getAllClasses();
        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                TableRow tableRow = new TableRow(this);

                for (int i = 0; i < data.getColumnCount(); i++) {
                    TextView textView = new TextView(this);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setLayoutParams(textViewLayout);
                    textView.setText(data.getString(i));
                    tableRow.addView(textView);
                }
                tableLayout.addView(tableRow);
            }
        }
    }

    protected List<String> getClassesList() {
        Cursor res = db.getAllClasses();
        List<String> classesData = new ArrayList<>();
        if (res.moveToFirst()) {
            classesData.add("Select an item");
            do {
                int classesIndex = res.getColumnIndex("nazwa");
                String item = res.getString(classesIndex);
                classesData.add(item);
            } while (res.moveToNext());
        }
        return classesData;
    }

    private void fillSpinner(Context context, Spinner spinner, List<String> data) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, data) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;  // Disable the first item
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private Set<Integer> getRandomNumbers(int scope, int numberOfStudents) {
        Random random = new Random();
        Set<Integer> selectedStudents = new LinkedHashSet<>(numberOfStudents);
        while (selectedStudents.size() < numberOfStudents) {
            selectedStudents.add(random.nextInt(scope) + 1);
        }
        return selectedStudents;
    }

    private void onAddClassHandler () {
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);

        Button btnPositive = dialogView.findViewById(R.id.dialog_positive_btn);
        Button btnNegative = dialogView.findViewById(R.id.dialog_negative_btn);
        nazwa = (EditText) dialogView.findViewById(R.id.nazwa);
        liczba_uczniow = (EditText) dialogView.findViewById(R.id.liczba_uczniow);

        AlertDialog dialog = builder.create();

        btnPositive.setOnClickListener(v -> onAddClassClickHandler(dialog));

        btnNegative.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void onAddClassClickHandler(AlertDialog dialog) {
        Boolean checkInsertData = db.insertData(nazwa.getText().toString(), Integer.parseInt(liczba_uczniow.getText().toString()));
        if (checkInsertData) {
            Toast.makeText(MainActivity.this, "Dodano nową klase", Toast.LENGTH_SHORT).show();
            setInitialData(MainActivity.this);
        } else {
            Toast.makeText(MainActivity.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }
}