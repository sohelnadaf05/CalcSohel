package com.example.calcsohel;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    TextView txtResult, txtExpression;
    LinearLayout historyPanel;
    ListView historyList;

    String input = "";
    ArrayList<String> history = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResult = findViewById(R.id.txtResult);
        txtExpression = findViewById(R.id.txtExpression);

        historyPanel = findViewById(R.id.historyPanel);
        historyList = findViewById(R.id.historyList);

        // NULL CHECK FIX
        if (historyList != null) {
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, history);
            historyList.setAdapter(adapter);
        }

        View btnHistory = findViewById(R.id.btnHistory);
        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> toggleHistory());
        }

        setupButtons();
    }

    // 🔥 Attach all buttons
    private void setupButtons() {
        int[] numberIds = {
                R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btn6, R.id.btn5, R.id.btn4,
                R.id.btn3, R.id.btn2, R.id.btn1,
                R.id.btn0
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(v -> {
                Button b = (Button) v;
                append(b.getText().toString());
            });
        }

        // You MUST assign IDs to all buttons in XML for this to work
    }

    // 🔢 Add input
    private void append(String val) {
        input += val;
        if (txtExpression != null) {
            txtExpression.setText(input);
        }
    }

    // ➕ Operators
    public void onOperator(View v) {
        String val = ((Button) v).getText().toString();

        if (input.isEmpty()) return;

        char last = input.charAt(input.length() - 1);
        if ("+-*/".contains(String.valueOf(last))) {
            input = input.substring(0, input.length() - 1);
        }

        input += val;
        if (txtExpression != null) {
            txtExpression.setText(input);
        }
    }

    // 🧹 Clear
    public void onClear(View v) {
        input = "";
        txtExpression.setText("");
        txtResult.setText("0");
    }

    // ⌫ Backspace
    public void onBack(View v) {
        if (!input.isEmpty()) {
            input = input.substring(0, input.length() - 1);
            if (txtExpression != null) {
                txtExpression.setText(input);
            }
        }
    }

    // ± Toggle
    public void onPlusMinus(View v) {
        if (input.isEmpty()) return;

        if (input.startsWith("-")) {
            input = input.substring(1);
        } else {
            input = "-" + input;
        }
        if (txtExpression != null) {
            txtExpression.setText(input);
        }
    }

    // = Evaluate
    public void onEqual(View v) {
        try {
            double result = evaluate(input);
            txtResult.setText(String.valueOf(result));

            history.add(input + " = " + result);
            adapter.notifyDataSetChanged();

            input = String.valueOf(result);
        } catch (Exception e) {
            txtResult.setText("Error");
        }
    }

    // 🔥 CUSTOM EVALUATOR (no Rhino)
    private double evaluate(String expr) {
        expr = expr.replace("×", "*").replace("÷", "/");

        Stack<Double> numbers = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expr.length() &&
                        (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    sb.append(expr.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            }

            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(c)) {
                    numbers.push(applyOp(ops.pop(), numbers.pop(), numbers.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            numbers.push(applyOp(ops.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return b == 0 ? 0 : a / b;
        }
        return 0;
    }

    // 📜 History toggle
    private void toggleHistory() {
        if (historyPanel == null) return;

        historyPanel.setVisibility(
                historyPanel.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
    }
}