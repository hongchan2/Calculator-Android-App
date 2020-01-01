package com.kw.activity.firstproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

enum Mode {DECIMAL, HEXIMAL}

public class MainActivity extends AppCompatActivity {
    Mode mode;

    String strSusic = "";
    String strResult = "";
    double curResult = 0;

    TextView txtSusic;
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mode = Mode.DECIMAL;
        txtSusic = findViewById(R.id.susic);
        txtResult = findViewById(R.id.result);
    }

    public void clickRadio(View v){
        RadioButton radioButton = (RadioButton)v;

        if(radioButton.getText().equals("Decimal")){
            mode = Mode.DECIMAL;
        }
        else if(radioButton.getText().equals("Heximal")){
            mode = Mode.HEXIMAL;
        }
        // print result
        if(curResult != 0)
            calculate(curResult);
    }

    public void clickNum(View v){
        TextView textView = (TextView)v;

        strSusic += textView.getText().toString();
        txtSusic.setText(strSusic);
    }

    public void clickOper(View v){
        TextView textView = (TextView)v;

        switch(textView.getText().toString()){
            case "←":
                if(strResult != null && !strResult.isEmpty()){
                    //result exist
                    strSusic = "";
                    strResult = "";
                    curResult = 0;
                    txtSusic.setText(strSusic);
                    txtResult.setText(strResult);
                }
                else{
                    //result empty
                    if(strSusic != null && !strSusic.isEmpty()) {
                        // susic exist
                        strSusic = strSusic.substring(0, strSusic.length() - 1);
                        txtSusic.setText(strSusic);
                        return;
                    }
                }
                break;
            case "=":
                curResult = calcPostfix(infixToPostfix(strSusic));
                calculate(curResult);
                break;
        }
    }

    private void calculate(double result){
        double pointResult = Math.round(result * 100) / 100.0;

        if(mode == Mode.DECIMAL){
            if ((pointResult == Math.floor(pointResult)) && !Double.isInfinite(pointResult))
                strResult = "= " + (int)pointResult;
            else
                strResult = "= " + pointResult;

            txtResult.setText(strResult);
        }
        else if(mode == Mode.HEXIMAL){
            strResult = "= ";
            strResult += Integer.toHexString((int)pointResult).toUpperCase();

            txtResult.setText(strResult);
        }
    }

    private ArrayList infixToPostfix(String str){
        ArrayList<String> result = new ArrayList<>();
        Stack<Character> stack = new Stack<>();

        int longNum = 0;
        String curStr = "";

        for(int i = 0; i < str.length(); i++){
            char c = str.charAt(i);
            if(c == '('){
                if(longNum != 0){
                    result.add(curStr);
                    curStr = "";
                    longNum = 0;
                }

                stack.push(c);
            }
            else if(c == ')'){
                if(longNum != 0){
                    result.add(curStr);
                    curStr = "";
                    longNum = 0;
                }

                while(true){
                    if(stack.peek() == '('){
                        stack.pop();
                        break;
                    }
                    else{
                        result.add(Character.toString(stack.peek()));
                        stack.pop();
                    }
                }
            }
            else if(c == '.'){
                curStr += c;
            }
            else if(Character.isDigit(c)){
                //result += c;
                curStr += c;
                longNum++;
            }
            else if(c == 'X' || c == '/' || c == '+' || c == '-'){
                if(longNum != 0){
                    result.add(curStr);
                    curStr = "";
                    longNum = 0;
                }

                while(!stack.isEmpty() && stack.peek() != '('){
                    if(stack.peek() == '+' || stack.peek() == '-')
                        break;
                    result.add(Character.toString(stack.peek()));
                    stack.pop();
                }
                stack.push(c);
            }
        }

        if(longNum != 0){
            result.add(curStr);
        }

        while(!stack.isEmpty()){
            result.add(Character.toString(stack.peek()));
            stack.pop();
        }
        //Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        return result;
    }

    private double calcPostfix(ArrayList<String> str){
        Stack<Double> stack = new Stack<>();

        for(String s : str){
            if(s.equals("X") || s.equals("/") || s.equals("+") || s.equals("-")){
                double c1 = stack.peek();
                stack.pop();
                double c2 = stack.peek();
                stack.pop();

                if(s.equals("X")){
                    stack.push(c2 * c1);
                }
                else if(s.equals("/")){
                    stack.push(c2 / c1);
                }
                else if(s.equals("+")){
                    stack.push(c2 + c1);
                }
                else if(s.equals("-")){
                    stack.push(c2 - c1);
                }
            }
            else{
                //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                stack.push(Double.parseDouble(s));
            }
        }

        return stack.peek();
    }
}
