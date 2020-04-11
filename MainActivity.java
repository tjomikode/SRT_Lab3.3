package com.example.srtlab3_3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Random random = new Random(System.currentTimeMillis());
    private EditText a, b, c, d, y;
    private TextView errorLabel;
    private TextView[] outXs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        a = findViewById(R.id.A);
        b = findViewById(R.id.B);
        c = findViewById(R.id.C);
        d = findViewById(R.id.D);
        y = findViewById(R.id.Y);
        outXs = new TextView[]{findViewById(R.id.X1out), findViewById(R.id.X2out), findViewById(R.id.X3out), findViewById(R.id.X4out)};
        errorLabel = findViewById(R.id.error);

        errorLabel.setTextColor(Color.RED);
        for (TextView outX : outXs) {
            outX.setTextColor(Color.GREEN);
        }
    }

    @SuppressLint("SetTextI18n")
    public void executeLab(View v) {
        if (a.getText().toString().trim().equals("") || b.getText().toString().isEmpty() ||
                c.getText().toString().isEmpty() || d.getText().toString().isEmpty() || y.getText().toString().isEmpty()) {
            cleanOuts();
            errorLabel.setText("There is should be all numbers entered : a, b, c, d, y");
            return;
        }
        if (isNotNumeric(a.getText().toString()) || isNotNumeric(b.getText().toString())
                || isNotNumeric(c.getText().toString()) || isNotNumeric(d.getText().toString()) || isNotNumeric(y.getText().toString())) {
            cleanOuts();
            errorLabel.setText("Incorrect input!");
            return;
        }
        else {
            errorLabel.setText("");
        }
        int[] result = solve(Integer.parseInt(a.getText().toString()),
                Integer.parseInt(b.getText().toString()), Integer.parseInt(c.getText().toString()),
                Integer.parseInt(d.getText().toString()), Integer.parseInt(y.getText().toString()));
        showResults(result);
    }

    @SuppressLint("SetTextI18n")
    private void showResults(int[] result) {
        for (int i = 0; i < outXs.length; i++) {
            outXs[i].setText(Integer.toString(result[i]));
        }
    }

    public int[] solve(int a, int b, int c, int d, int y) {
        int[][] populationGene = initialPopulationGene(y);
        int fit;
        int[] inputs = {a, b, c, d};
        int[] deltas;
        while (true) {
            deltas = fitness(populationGene, inputs, y);
            if ((fit = getIndex(deltas)) != -1) {
                break;
            }
            else {
                double avgSurvival = getAverage(genLikelihoods(deltas));
                int[][] newPop = newPopulation(deltas, populationGene);
                if (avgSurvival < getAverage(genLikelihoods(fitness(newPop, inputs, y)))) {
                    populationGene = newPop;
                }
                else {
                    mutate(populationGene, y);
                }
            }
        }
        return populationGene[(int) fit];
    }

    private int[][] initialPopulationGene(int fromZeroTo) {
        int min = 5;
        int index = min + random.nextInt(fromZeroTo + 1 - min);
        int [][] populationGene = new int[index][4];
        for (int i = 0; i < populationGene.length; i++) {
            for (int j = 0; j < populationGene[0].length; j++) {
                populationGene[i][j] = random.nextInt(fromZeroTo >> 1);
            }
        }
        return populationGene;
    }

    private int[] fitness(int[][] population, int[] inputs, int y) {
        int[] deltas = new int[population.length];
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[0].length; j++) {
                deltas[i] += population[i][j] * inputs[j];
            }
            deltas[i] = Math.abs(deltas[i] - y);
        }
        return deltas;
    }

    private int[][] newPopulation(int[] deltas, int[][] populationGene) {
        double[] survivalMulti = genLikelihoods(deltas);
        int[][] parents = genParents(survivalMulti, populationGene);
        return crossOverPairs(parents, populationGene);
    }

    private double getAverage(double[] list) {
        double avg = 0;
        for (double v : list) {
            avg += v;
        }
        avg /= list.length;
        return avg;
    }

    private int getIndex(int[] list) {
        int index = -1;
        for (int i = 0; i < list.length; i++) {
            if (list[i] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    private double[] genLikelihoods(int[] deltas) {
        double multi = 0;
        double[] survival = new double[deltas.length];
        for (int delta : deltas) {
            multi += (double) 1 / delta;
        }
        for (int j = 0; j < deltas.length; j++) {
            survival[j] = ((double)1 / deltas[j]) / multi;
        }
        return survival;
    }

    private int[][] crossOverPairs(int[][] parents, int[][] population) {
        int[][] newPopulation = new int[population.length][4];
        for (int i = 0; i < population.length; i++) {
            newPopulation[i] = crossOver(population[parents[i][0]], population[parents[i][1]]);
        }
        return newPopulation;
    }

    private int[][] genParents(double[] survivalMulti, int[][] populationGene) {
        int[][] pairs = new int[populationGene.length][2];
        int [] parents = new int[survivalMulti.length / 2];
        int flag;
        double maximumSurvProb;
        for (int i = 0; i < survivalMulti.length / 2; i++) {
            maximumSurvProb = survivalMulti[0];
            flag = 0;
            for (int j = 0; j < survivalMulti.length; j++) {
                if (survivalMulti[j] > maximumSurvProb) {
                    maximumSurvProb = survivalMulti[j];
                    flag = j;
                }
            }
            survivalMulti[flag] = -1;
            parents[i] = flag;
        }
        for (int k = 0; k < pairs.length; k++) {
            pairs[k][0] = parents[random.nextInt(parents.length)];
            pairs[k][1] = parents[random.nextInt(parents.length)];
            if (pairs[k][0] != pairs[k][1]) {
                k++;
            }
        }
        return pairs;
    }

    private int[] crossOver(int[] p1, int[] p2){
        int minimum = 1;
        int maximum = 3;
        int field = minimum + random.nextInt(maximum + 1 - minimum);
        int[] child = new int[p1.length];
        for (int i = 0; i < child.length; i++) {
            if (i < field) {
                child[i] = p1[i];
            }
            else {
                child[i] = p2[i];
            }
        }
        return child;
    }

    private void mutate(int[][] populationGene, int y) {
        for (int i = 0; i < populationGene.length; i++) {
            int randInsteadAlleleValue = random.nextInt(populationGene[0].length);
            populationGene[i][randInsteadAlleleValue] = random.nextInt(y + 1);
        }
    }

    public static boolean isNotNumeric(String x) throws NumberFormatException {
        try {
            Integer.parseInt(x);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public void cleanOuts() {
        for (TextView outX : outXs) {
            outX.setText("");
        }
    }

    public void cleanAllForms(View v) {
        cleanOuts();
        a.setText("");
        b.setText("");
        c.setText("");
        d.setText("");
        y.setText("");
        errorLabel.setText("");
    }
}
