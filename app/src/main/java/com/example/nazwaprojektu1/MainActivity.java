package com.example.nazwaprojektu1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText edit1, edit2, edit3;
    private Button btnMarks, btnGood, btnNotGood;
    private TextView tvAverage;
    private double aktualnaSrednia = 0.0;
    private boolean czyPokazacSrednia = false;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit1 = findViewById(R.id.editImie);
        edit2 = findViewById(R.id.editNazwisko);
        edit3 = findViewById(R.id.editOceny);
        btnMarks = findViewById(R.id.btnMarks);
        btnGood = findViewById(R.id.btnGood);
        btnNotGood = findViewById(R.id.btnNotGood);
        tvAverage = findViewById(R.id.tvAverage);

        btnMarks.setVisibility(View.GONE);
        btnGood.setVisibility(View.GONE);
        btnNotGood.setVisibility(View.GONE);
        tvAverage.setVisibility(View.GONE);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String imie = data.getStringExtra("imie");
                        String nazwisko = data.getStringExtra("nazwisko");
                        String liczbaOcen = data.getStringExtra("liczbaOcen");
                        aktualnaSrednia = data.getDoubleExtra("wynikSrednia", 0.0);
                        czyPokazacSrednia = data.getBooleanExtra("pokazWynikSrednia", false);

                        if (imie != null) edit1.setText(imie);
                        if (nazwisko != null) edit2.setText(nazwisko);
                        if (liczbaOcen != null) edit3.setText(liczbaOcen);

                        updateAverageScoreAndButtons(aktualnaSrednia, czyPokazacSrednia);
                    }
                }
        );

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        TextWatcher ocenyTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString().trim();
                if (!str.isEmpty()) {
                    try {
                        int oceny = Integer.parseInt(str);
                        if (oceny < 5 || oceny > 15) {
                            edit3.setError("Liczba ocen musi należeć do przedziału 5-15");
                        } else {
                            edit3.setError(null);
                        }
                    } catch (NumberFormatException e) {
                        edit3.setError("Nieprawidłowa liczba");
                    }
                }
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edit1.addTextChangedListener(textWatcher);
        edit2.addTextChangedListener(textWatcher);
        edit3.addTextChangedListener(ocenyTextWatcher);

        edit1.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && edit1.getText().toString().trim().isEmpty()) {
                edit1.setError("Pole Imię jest puste");
            }
            checkFieldsForEmptyValues();
        });

        edit2.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && edit2.getText().toString().trim().isEmpty()) {
                edit2.setError("Pole Nazwisko jest puste");
            }
            checkFieldsForEmptyValues();
        });

        edit3.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && edit3.getText().toString().trim().isEmpty()) {
                edit3.setError("Pole Liczba Ocen jest puste");
            }
            checkFieldsForEmptyValues();
        });

        btnMarks.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("liczbaPrzedmiotow", edit3.getText().toString().trim());
            intent.putExtra("imie", edit1.getText().toString().trim());
            intent.putExtra("nazwisko", edit2.getText().toString().trim());
            activityResultLauncher.launch(intent);
        });

        btnGood.setOnClickListener(v -> showToast("Gratulacje! Otrzymujesz zaliczenie!"));
        btnNotGood.setOnClickListener(v -> showToast("Wysyłam podanie o zaliczenie warunkowe"));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imie", edit1.getText().toString());
        outState.putString("nazwisko", edit2.getText().toString());
        outState.putString("liczbaOcen", edit3.getText().toString());
        outState.putDouble("wynikSrednia", aktualnaSrednia);
        outState.putBoolean("pokazWynikSrednia", czyPokazacSrednia);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        edit1.setText(savedInstanceState.getString("imie", ""));
        edit2.setText(savedInstanceState.getString("nazwisko", ""));
        edit3.setText(savedInstanceState.getString("liczbaOcen", ""));
        aktualnaSrednia = savedInstanceState.getDouble("wynikSrednia", 0.0);
        czyPokazacSrednia = savedInstanceState.getBoolean("pokazWynikSrednia", false);

        if (czyPokazacSrednia) {
            updateAverageScoreAndButtons(aktualnaSrednia, true);
        }
    }

    private void checkFieldsForEmptyValues() {
        String s1 = edit1.getText().toString().trim();
        String s2 = edit2.getText().toString().trim();
        String s3 = edit3.getText().toString().trim();

        if (s1.isEmpty() || s2.isEmpty() || s3.isEmpty()) {
            btnMarks.setVisibility(View.GONE);
            return;
        }

        try {
            int oceny = Integer.parseInt(s3);
            if (oceny >= 5 && oceny <= 15) {
                btnMarks.setVisibility(View.VISIBLE);
            } else {
                btnMarks.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            btnMarks.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateAverageScoreAndButtons(double wynikSrednia, boolean pokazWynikSrednia) {
        if (!pokazWynikSrednia) return;

        String formatted = String.format(Locale.getDefault(), "%.2f", wynikSrednia);
        tvAverage.setText(String.format("Średnia wyników: %s", formatted));
        tvAverage.setVisibility(View.VISIBLE);

        btnGood.setVisibility(View.GONE);
        btnNotGood.setVisibility(View.GONE);

        if (wynikSrednia >= 3.0) {
            btnGood.setVisibility(View.VISIBLE);
        } else if (wynikSrednia > 0.0) {
            btnNotGood.setVisibility(View.VISIBLE);
        }
    }
}
