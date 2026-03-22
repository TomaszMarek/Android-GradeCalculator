package com.example.nazwaprojektu1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.PrzedmiotViewHolder> {
    private String[] wszystkieOceny;
    private int[] wybraneOceny;
    private MainActivity2 mainActivity2;

    public Adapter(String[] wszystkieOceny, MainActivity2 mainActivity2) {
        this.wszystkieOceny = wszystkieOceny;
        this.wybraneOceny = new int[wszystkieOceny.length];
        this.mainActivity2 = mainActivity2;
    }

    @NonNull
    @Override
    public PrzedmiotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wiersz, parent, false);
        return new PrzedmiotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrzedmiotViewHolder holder, int position) {
        holder.bind(wszystkieOceny[position], wybraneOceny[position]);
    }

    @Override
    public int getItemCount() {
        return wszystkieOceny.length;
    }

    public void setwybraneOceny(int[] wybraneOceny) {
        this.wybraneOceny = wybraneOceny;
    }

    public int[] getwybraneOceny() {
        return wybraneOceny;
    }

    public int getScore(int position) {
        return wybraneOceny[position];
    }

    public class PrzedmiotViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RadioGroup radioGroup;
        RadioButton radioButton, radioButton2, radioButton3, radioButton4;

        public PrzedmiotViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView2);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioButton = itemView.findViewById(R.id.radioButton);
            radioButton2 = itemView.findViewById(R.id.radioButton2);
            radioButton3 = itemView.findViewById(R.id.radioButton3);
            radioButton4 = itemView.findViewById(R.id.radioButton4);
        }

        public void bind(String przedmiot, int selectedScore) {
            textView.setText(przedmiot);
            radioGroup.setOnCheckedChangeListener(null);

            //bez tego przy obrocie ginie
            switch (selectedScore) {
                case 2:
                    radioButton.setChecked(true);
                    break;
                case 3:
                    radioButton2.setChecked(true);
                    break;
                case 4:
                    radioButton3.setChecked(true);
                    break;
                case 5:
                    radioButton4.setChecked(true);
                    break;
                default:
                    radioGroup.clearCheck();
                    break;
            }

            //zmiana wyboru
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radioButton) {
                    wybraneOceny[getAdapterPosition()] = 2;
                } else if (checkedId == R.id.radioButton2) {
                    wybraneOceny[getAdapterPosition()] = 3;
                } else if (checkedId == R.id.radioButton3) {
                    wybraneOceny[getAdapterPosition()] = 4;
                } else if (checkedId == R.id.radioButton4) {
                    wybraneOceny[getAdapterPosition()] = 5;
                }
                mainActivity2.checkButtonVisibility();
            });
        }
    }
}
