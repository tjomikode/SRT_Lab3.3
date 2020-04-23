package com.example.srtlab3_3;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final static int POP = 8;
    private static int resultGenotype;
    private final static Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressLint("SetTextI18n")
    public void findRoots(View view) {
        String inputA = ((EditText) findViewById(R.id.editText2)).getText().toString();
        String inputB = ((EditText) findViewById(R.id.editText3)).getText().toString();
        String inputC = ((EditText) findViewById(R.id.editText4)).getText().toString();
        String inputD = ((EditText) findViewById(R.id.editText5)).getText().toString();
        String inputY = ((EditText) findViewById(R.id.editText6)).getText().toString();

        if (inputA.isEmpty() || inputB.isEmpty() || inputC.isEmpty()
                || inputD.isEmpty() || inputY.isEmpty()) {
            Toast toast = Toast.makeText(this, "Enter all values!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        int[][] population = getPopulation();

        int a = Integer.parseInt(inputA);
        int b = Integer.parseInt(inputB);
        int c = Integer.parseInt(inputC);
        int d = Integer.parseInt(inputD);
        int y = Integer.parseInt(inputY);
        int f;
        int[] deltas = new int[POP];

        while (true) {
            for (int i = 0; i < POP; i++) {
                f = a * population[i][0] + b * population[i][1] + c * population[i][2] + d * population[i][3];
                deltas[i] = Math.abs(y - f);
            }

            if (checkDeltas(deltas)) break;

            population = newGeneration(population, getProbabilities(deltas));
        }

        TextView textView = findViewById(R.id.textView17);
        textView.setText("Results: " + Arrays.toString(population[resultGenotype]));

    }

    private int[][] getPopulation() {
        int[][] population = new int[POP][POP];
        for (int i = 0; i < POP; i++) {
            for (int j = 0; j < POP; j++) {
                population[i][j] = RANDOM.nextInt(10);
            }
        }
        return population;
    }

    private boolean checkDeltas(int[] deltas) {
        for (int i = 0; i < POP; i++) {
            if (deltas[i] == 0) {
                resultGenotype = i;
                return true;
            }
        }
        return false;
    }

    private double[] getProbabilities(int[] deltas) {
        double sum = 0.0;
        double[] probabilities = new double[POP];
        for (int i = 0; i < POP; i++) {
            probabilities[i] = 1.0 / deltas[i];
            sum += probabilities[i];
        }
        for (int i = 0; i < POP; i++) {
            probabilities[i] /= sum;
        }
        for (int i = 1; i < POP; i++) {
            probabilities[i] += probabilities[i-1];
        }
        return probabilities;
    }

    private int[][] newGeneration(int[][] oldPopulation, double[] probabilities) {
        int[][] newGen = new int[POP][POP];

        for (int i = 0; i < POP; i++) {
            int root1 = peekRoot(probabilities);
            int root2 = peekRoot(probabilities);
            newGen[i][0] = oldPopulation[root1][0];
            newGen[i][1] = oldPopulation[root1][1];
            newGen[i][2] = oldPopulation[root2][2];
            newGen[i][3] = oldPopulation[root2][3];
        }

        // mutation
        if (RANDOM.nextDouble() < 0.5) {
            newGen[RANDOM.nextInt(POP)][RANDOM.nextInt(POP)]++;
        } else {
            newGen[RANDOM.nextInt(POP)][RANDOM.nextInt(POP)]--;
        }

        return newGen;
    }

    private int peekRoot(double[] probabilities) {
        double rand = RANDOM.nextDouble();
        if(rand < probabilities[0]) {
            return 0;
        } else if (rand < probabilities[1]) {
            return 1;
        } else if (rand < probabilities[2]) {
            return 2;
        } else {
            return 3;
        }
    }
}