package com.example.lab5;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CalculatorController {

    @FXML
    private TextField displayField;

    private enum CalculatorState {
        START,
        INPUT_FIRST_OPERAND,
        INPUT_OPERATOR,
        INPUT_SECOND_OPERAND,
        SHOW_RESULT
    }

    private CalculatorState state = CalculatorState.START;
    private double firstOperand = 0;
    private double secondOperand = 0;
    private String operator = "";
    private String displayText = "0";

    @FXML
    public void initialize() {
        displayField.setText(displayText);
        displayField.setOnKeyPressed(this::handleKeyPress);
    }

    @FXML
    private void handleNumber(ActionEvent event) {
        Button button = (Button) event.getSource();
        handleNumberInput(button.getText());
    }

    private void handleNumberInput(String number) {
        if (number.equals(".")) {
            if (displayText.contains(".")) {
                return;
            }
            if (state == CalculatorState.START || state == CalculatorState.SHOW_RESULT) {
                displayText = "0.";
                state = CalculatorState.INPUT_FIRST_OPERAND;
            } else {
                displayText += ".";
            }
        } else {
            switch (state) {
                case START:
                case SHOW_RESULT:
                    displayText = number;
                    state = CalculatorState.INPUT_FIRST_OPERAND;
                    break;
                case INPUT_FIRST_OPERAND:
                case INPUT_SECOND_OPERAND:
                    if (displayText.equals("0")) {
                        displayText = number;
                    } else {
                        displayText += number;
                    }
                    break;
                case INPUT_OPERATOR:
                    displayText = number;
                    state = CalculatorState.INPUT_SECOND_OPERAND;
                    break;
            }
        }

        displayField.setText(displayText);
    }

    @FXML
    private void handleOperator(ActionEvent event) {
        Button button = (Button) event.getSource();
        handleOperatorInput(button.getText());
    }

    private void handleOperatorInput(String selectedOperator) {
        switch (state) {
            case INPUT_FIRST_OPERAND:
            case SHOW_RESULT:
                firstOperand = parseDisplayText();
                operator = selectedOperator;
                state = CalculatorState.INPUT_OPERATOR;
                break;
            case INPUT_SECOND_OPERAND:
                secondOperand = parseDisplayText();
                calculateResult();
                firstOperand = parseDisplayText();
                operator = selectedOperator;
                state = CalculatorState.INPUT_OPERATOR;
                break;
        }

        displayField.setText(displayText);
    }

    @FXML
    private void handleEquals(ActionEvent event) {
        handleEqualsInput();
    }

    private void handleEqualsInput() {
        if (state == CalculatorState.INPUT_SECOND_OPERAND) {
            secondOperand = parseDisplayText();
            calculateResult();
            state = CalculatorState.SHOW_RESULT;
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        resetCalculator();
    }

    @FXML
    private void handleSquareRoot(ActionEvent event) {
        if (state == CalculatorState.INPUT_FIRST_OPERAND || state == CalculatorState.SHOW_RESULT) {
            firstOperand = parseDisplayText();
            if (firstOperand < 0) {
                displayText = "Ошибка";
                resetCalculator();
            } else {
                displayText = String.valueOf(Math.sqrt(firstOperand));
                state = CalculatorState.SHOW_RESULT;
            }
            displayField.setText(displayText);
        }
    }

    @FXML
    private void handlePower(ActionEvent event) {
        operator = "^";
        state = CalculatorState.INPUT_OPERATOR;
        firstOperand = parseDisplayText();
        displayText = "0";
        displayField.setText(displayText);
    }

    private void calculateResult() {
        double result = 0;
        switch (operator) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "*":
                result = firstOperand * secondOperand;
                break;
            case "/":
                if (secondOperand == 0) {
                    displayText = "Ошибка";
                    resetCalculator();
                    return;
                }
                result = firstOperand / secondOperand;
                break;
            case "^":
                result = Math.pow(firstOperand, secondOperand);
                break;
        }
        displayText = String.valueOf(result);
        displayField.setText(displayText);
    }

    private void resetCalculator() {
        state = CalculatorState.START;
        firstOperand = 0;
        secondOperand = 0;
        operator = "";
        displayText = "0";
        displayField.setText(displayText);
    }

    private double parseDisplayText() {
        return Double.parseDouble(displayText);
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code.isDigitKey()) {
            handleNumberInput(code.getName());
        } else if (code == KeyCode.ADD || (event.isShiftDown() && code == KeyCode.EQUALS)) {
            handleOperatorInput("+");
        } else if (code == KeyCode.SUBTRACT || code == KeyCode.MINUS) {
            handleOperatorInput("-");
        } else if (code == KeyCode.MULTIPLY || code == KeyCode.ASTERISK) {
            handleOperatorInput("*");
        } else if (code == KeyCode.DIVIDE || code == KeyCode.SLASH) {
            handleOperatorInput("/");
        } else if (code == KeyCode.ENTER || code == KeyCode.EQUALS) {
            handleEqualsInput();
        } else if (code == KeyCode.BACK_SPACE) {
            resetCalculator();
        }
    }
}
