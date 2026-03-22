package com.example.nazwaprojektu1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity {
    int liczba = 0;
    Adapter adapter;
    RecyclerView recyclerView;
    Button btn2; //gdy wszystkie oceny sa widoczne

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btn2 = findViewById(R.id.button2);
        btn2.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //odbieranie danych
        Intent intent = getIntent();
        String liczbaPrzedmiotow = intent.getStringExtra("liczbaPrzedmiotow");
        String imie = intent.getStringExtra("imie");
        String nazwisko = intent.getStringExtra("nazwisko");
        String[] wszystkieOceny = getResources().getStringArray(R.array.wszystkieOceny);


        liczba = Integer.parseInt(liczbaPrzedmiotow);
        //kopiowanie zakresu tablicy
        String[] wszystkieOcenyDoWyswietlenia = Arrays.copyOfRange(wszystkieOceny, 0, liczba);
        adapter = new Adapter(wszystkieOcenyDoWyswietlenia, this);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            Parcelable listState = savedInstanceState.getParcelable("recyclerViewState");
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);

            int[] wybraneOceny = savedInstanceState.getIntArray("wybraneOceny");
            adapter.setwybraneOceny(wybraneOceny);
        }

        btn2.setOnClickListener(view -> {
            int liczbaZaznaczonychOcen = 0;
            int sumaOcen = 0;

            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getScore(i) > 0) {
                    liczbaZaznaczonychOcen++;
                    sumaOcen += adapter.getScore(i);
                }
            }

            if (liczbaZaznaczonychOcen == liczba) {
                double wynikSrednia = (double) sumaOcen / liczba;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("wynikSrednia", wynikSrednia);
                resultIntent.putExtra("pokazWynikSrednia", true);
                resultIntent.putExtra("imie", imie);
                resultIntent.putExtra("nazwisko", nazwisko);
                resultIntent.putExtra("liczbaOcen", liczbaPrzedmiotow);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { //zapisuje oceny przed zniszcz.
        super.onSaveInstanceState(outState);
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("recyclerViewState", listState);

        int[] wybraneOceny = adapter.getwybraneOceny();
        outState.putIntArray("wybraneOceny", wybraneOceny);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Parcelable listState = savedInstanceState.getParcelable("recyclerViewState");
        recyclerView.getLayoutManager().onRestoreInstanceState(listState);

        int[] wybraneOceny = savedInstanceState.getIntArray("wybraneOceny");
        adapter.setwybraneOceny(wybraneOceny);

        checkButtonVisibility();
    }

    public void checkButtonVisibility() {
        int liczbaZaznaczonychOcen = 0;
        int[] wybraneOceny = adapter.getwybraneOceny();
        for (int score : wybraneOceny) {
            if (score > 0) {
                liczbaZaznaczonychOcen++;
            }
        }
        if (liczbaZaznaczonychOcen == liczba) {
            btn2.setVisibility(View.VISIBLE);
        } else {
            btn2.setVisibility(View.GONE);
        }
    }
}
