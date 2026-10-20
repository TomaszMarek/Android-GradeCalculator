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

    public interface OnGradeSelectedListener {
        void onGradeSelected();
    }

    private final String[] wszystkieOceny;
    private int[] wybraneOceny;
    private final OnGradeSelectedListener gradeListener;

    public Adapter(String[] wszystkieOceny, OnGradeSelectedListener gradeListener) {
        this.wszystkieOceny = wszystkieOceny;
        this.wybraneOceny = new int[wszystkieOceny.length];
        this.gradeListener = gradeListener;
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
        return wszystkieOceny != null ? wszystkieOceny.length : 0;
    }

    public void setWybraneOceny(int[] wybraneOceny) {
        this.wybraneOceny = wybraneOceny;
        notifyDataSetChanged();
    }

    public int[] getWybraneOceny() {
        return wybraneOceny;
    }

    public int getScore(int position) {
        if (position >= 0 && position < wybraneOceny.length) {
            return wybraneOceny[position];
        }
        return 0;
    }

    public class PrzedmiotViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final RadioGroup radioGroup;
        private final RadioButton radioButton, radioButton2, radioButton3, radioButton4;

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

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int adapterPos = getBindingAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    if (checkedId == R.id.radioButton) {
                        wybraneOceny[adapterPos] = 2;
                    } else if (checkedId == R.id.radioButton2) {
                        wybraneOceny[adapterPos] = 3;
                    } else if (checkedId == R.id.radioButton3) {
                        wybraneOceny[adapterPos] = 4;
                    } else if (checkedId == R.id.radioButton4) {
                        wybraneOceny[adapterPos] = 5;
                    }

                    if (gradeListener != null) {
                        gradeListener.onGradeSelected();
                    }
                }
            });
        }
    }
}
