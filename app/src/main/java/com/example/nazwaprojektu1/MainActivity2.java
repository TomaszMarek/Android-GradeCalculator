package com.example.nazwaprojektu1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity implements Adapter.OnGradeSelectedListener {
    private int liczba = 0;
    private Adapter adapter;
    private RecyclerView recyclerView;
    private Button btn2;
    private String imie;
    private String nazwisko;
    private String liczbaPrzedmiotow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btn2 = findViewById(R.id.button2);
        btn2.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null) {
            liczbaPrzedmiotow = intent.getStringExtra("liczbaPrzedmiotow");
            imie = intent.getStringExtra("imie");
            nazwisko = intent.getStringExtra("nazwisko");
        }

        try {
            liczba = Integer.parseInt(liczbaPrzedmiotow);
        } catch (NumberFormatException e) {
            liczba = 5;
        }

        String[] wszystkieOcenyZasob = getResources().getStringArray(R.array.wszystkieOceny);
        int maxDostepnych = Math.min(liczba, wszystkieOcenyZasob.length);
        String[] przedmiotyDoWyswietlenia = Arrays.copyOfRange(wszystkieOcenyZasob, 0, maxDostepnych);

        adapter = new Adapter(przedmiotyDoWyswietlenia, this);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            int[] zapisaneOceny = savedInstanceState.getIntArray("wybraneOceny");
            if (zapisaneOceny != null) {
                adapter.setWybraneOceny(zapisaneOceny);
            }
            Parcelable listState = savedInstanceState.getParcelable("recyclerViewState");
            if (listState != null && recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }
            checkButtonVisibility();
        }

        btn2.setOnClickListener(view -> {
            int liczbaZaznaczonych = 0;
            int sumaOcen = 0;

            for (int i = 0; i < adapter.getItemCount(); i++) {
                int score = adapter.getScore(i);
                if (score > 0) {
                    liczbaZaznaczonych++;
                    sumaOcen += score;
                }
            }

            if (liczbaZaznaczonych == liczba && liczba > 0) {
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recyclerView.getLayoutManager() != null) {
            outState.putParcelable("recyclerViewState", recyclerView.getLayoutManager().onSaveInstanceState());
        }
        if (adapter != null) {
            outState.putIntArray("wybraneOceny", adapter.getWybraneOceny());
        }
    }

    @Override
    public void onGradeSelected() {
        checkButtonVisibility();
    }

    private void checkButtonVisibility() {
        if (adapter == null) return;

        int liczbaZaznaczonych = 0;
        int[] oceny = adapter.getWybraneOceny();
        for (int score : oceny) {
            if (score > 0) {
                liczbaZaznaczonych++;
            }
        }

        if (liczbaZaznaczonych == liczba && liczba > 0) {
            btn2.setVisibility(View.VISIBLE);
        } else {
            btn2.setVisibility(View.GONE);
        }
    }
}
