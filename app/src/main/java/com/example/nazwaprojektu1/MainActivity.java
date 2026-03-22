package com.example.nazwaprojektu1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class MainActivity extends AppCompatActivity {
    EditText edit1, edit2, edit3;
    Button btnMarks, btnGood, btnNotGood;
    TextView tvAverage;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja pól
        edit1 = findViewById(R.id.editImie);
        edit2 = findViewById(R.id.editNazwisko);
        edit3 = findViewById(R.id.editOceny);
        btnMarks = findViewById(R.id.btnMarks);
        btnGood = findViewById(R.id.btnGood);
        btnNotGood = findViewById(R.id.btnNotGood);
        tvAverage = findViewById(R.id.tvAverage);

        // obsluga wynikow z innej aktywnosci
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String imie = data.getStringExtra("imie");
                        String nazwisko = data.getStringExtra("nazwisko");
                        String liczbaOcen = data.getStringExtra("liczbaOcen");
                        double wynikSrednia = data.getDoubleExtra("wynikSrednia", 0);
                        boolean pokazWynikSrednia = data.getBooleanExtra("pokazWynikSrednia", false);

                        edit1.setText(imie);
                        edit2.setText(nazwisko);
                        edit3.setText(liczbaOcen);

                        updateAverageScoreAndButtons(wynikSrednia, pokazWynikSrednia);
                    }
                }
        );

        btnMarks.setVisibility(View.GONE);
        //Wyklad strona 104
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        TextWatcher ocenyTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    int oceny = Integer.parseInt(s.toString());
                    if (oceny < 5 || oceny > 15) {
                        edit3.setError(getString(R.string.komunikat2));
                        showToast("Liczba ocen musi należeć do przedziału od 5 do 15");
                    } else {
                        edit3.setError(null);
                    }
                }
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edit1.addTextChangedListener(textWatcher);
        edit2.addTextChangedListener(textWatcher);
        edit3.addTextChangedListener(ocenyTextWatcher);

        edit1.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && edit1.getText().toString().isEmpty()) {
                edit1.setError(getString(R.string.komunikat));
                showToast("Pole Imię jest puste");
            }
            checkFieldsForEmptyValues();
        });

        edit2.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && edit2.getText().toString().isEmpty()) {
                edit2.setError(getString(R.string.komunikat));
                showToast("Pole Nazwisko jest puste");
            }
            checkFieldsForEmptyValues();
        });

        edit3.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && edit3.getText().toString().isEmpty()) {
                edit3.setError(getString(R.string.komunikat));
                showToast("Pole Liczba Ocen jest puste");
            }
            checkFieldsForEmptyValues();
        });

        btnMarks.setOnClickListener(view -> {
            Intent intent1 = new Intent(MainActivity.this, MainActivity2.class);
            intent1.putExtra("liczbaPrzedmiotow", edit3.getText().toString());
            intent1.putExtra("imie", edit1.getText().toString());
            intent1.putExtra("nazwisko", edit2.getText().toString());
            activityResultLauncher.launch(intent1);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Istotne przy obrocie
        outState.putString("imie", edit1.getText().toString());
        outState.putString("nazwisko", edit2.getText().toString());
        outState.putString("liczbaOcen", edit3.getText().toString());

        double wynikSrednia = 0;
        try {
            wynikSrednia = Double.parseDouble(tvAverage.getText().toString().replace("Średnia wyników: ", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        outState.putDouble("wynikSrednia", wynikSrednia);
        outState.putBoolean("pokazWynikSrednia", tvAverage.getVisibility() == View.VISIBLE);
        outState.putBoolean("showBtnGood", btnGood.getVisibility() == View.VISIBLE);
        outState.putBoolean("showBtnNotGood", btnNotGood.getVisibility() == View.VISIBLE);
    }

    //odtworzenie stanu aktywnosci
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        edit1.setText(savedInstanceState.getString("imie"));
        edit2.setText(savedInstanceState.getString("nazwisko"));
        edit3.setText(savedInstanceState.getString("liczbaOcen"));

        double wynikSrednia = savedInstanceState.getDouble("wynikSrednia", 0);
        boolean pokazWynikSrednia = savedInstanceState.getBoolean("pokazWynikSrednia", false);

        updateAverageScoreAndButtons(wynikSrednia, pokazWynikSrednia);
        showRelevantButton(wynikSrednia);

    }

    private void checkFieldsForEmptyValues() {
        //konwertuje i przypisuje tekst z edita do stringa
        String s1 = edit1.getText().toString();
        String s2 = edit2.getText().toString();
        String s3 = edit3.getText().toString();

        if (s1.isEmpty() || s2.isEmpty() || s3.isEmpty() || Integer.parseInt(s3) < 5 || Integer.parseInt(s3) > 15) {
            btnMarks.setVisibility(View.GONE);
        } else {
            btnMarks.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showRelevantButton(double wynikSrednia) {

        btnGood.setVisibility(View.GONE);
        btnNotGood.setVisibility(View.GONE);

        if (wynikSrednia >= 3) {
            btnGood.setVisibility(View.VISIBLE);
            btnGood.setOnClickListener(v -> showToast("Gratulacje! Otrzymujesz zaliczenie!"));
        } else if (wynikSrednia < 3 && wynikSrednia > 0) {
            btnNotGood.setVisibility(View.VISIBLE);
            btnNotGood.setOnClickListener(v -> showToast("Wysyłam podanie o zaliczenie warunkowe"));
        }
    }

    private void updateAverageScoreAndButtons(double wynikSrednia, boolean pokazWynikSrednia) {
        String formattedwynikSrednia = String.format("%.2f", wynikSrednia);
        tvAverage.setText("Średnia wyników: " + formattedwynikSrednia);
        tvAverage.setVisibility(View.VISIBLE);
        showRelevantButton(wynikSrednia);
    }
}
