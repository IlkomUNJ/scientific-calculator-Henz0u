package com.example.greetingcard

import kotlin.math.*

data class CalculatorOperation(
    val execute: (Double) -> Double,
    val symbol: String
)

class CalcLogic {
    fun getReciprocal(): CalculatorOperation = CalculatorOperation(
        execute = { value ->
            if (value == 0.0) throw ArithmeticException("Cannot divide by zero")
            1.0 / value
        },
        symbol = "1/x"
    )

    fun getFactorial(): CalculatorOperation = CalculatorOperation(
        execute = { value ->
            if (value < 0 || value != floor(value)) throw ArithmeticException("Factorial requires non-negative integer")
            if (value > 170) throw ArithmeticException("Result too large")
            var result = 1.0
            for (i in 2..value.toInt()) {
                result *= i
            }
            result
        },
        symbol = "x!"
    )

    fun getSqrt(): CalculatorOperation = CalculatorOperation(
        execute = { value ->
            if (value < 0) throw ArithmeticException("Cannot calculate square root of negative number")
            sqrt(value)
        },
        symbol = "√x"
    )

    fun getSine(): CalculatorOperation = CalculatorOperation(
        execute = { value -> sin(Math.toRadians(value)) },
        symbol = "sin"
    )

    fun getCosine(): CalculatorOperation = CalculatorOperation(
        execute = { value -> cos(Math.toRadians(value)) },
        symbol = "cos"
    )

    fun getTangent(): CalculatorOperation = CalculatorOperation(
        execute = { value -> tan(Math.toRadians(value)) },
        symbol = "tan"
    )

    fun getArcSine(): CalculatorOperation = CalculatorOperation(
        execute = { value ->
            if (value < -1 || value > 1) throw ArithmeticException("Invalid input for arcsin")
            Math.toDegrees(asin(value))
        },
        symbol = "sin⁻¹"
    )

    fun getArcCosine(): CalculatorOperation = CalculatorOperation(
        execute = { value ->
            if (value < -1 || value > 1) throw ArithmeticException("Invalid input for arccos")
            Math.toDegrees(acos(value))
        },
        symbol = "cos⁻¹"
    )

    fun getArcTangent(): CalculatorOperation = CalculatorOperation(
        execute = { value -> Math.toDegrees(atan(value)) },
        symbol = "tan⁻¹"
    )

    fun getLog(): CalculatorOperation = CalculatorOperation(
        execute = { value ->
            if (value <= 0) throw ArithmeticException("Logarithm requires positive number")
            log10(value)
        },
        symbol = "log"
    )

    fun getLn(): CalculatorOperation = CalculatorOperation(
        execute = { value ->
            if (value <= 0) throw ArithmeticException("Natural log requires positive number")
            ln(value)
        },
        symbol = "ln"
    )

    fun calculate(operation: CalculatorOperation, value: String): String {
        return try {
            val numValue = value.toDoubleOrNull() ?: return "Error"
            val result = operation.execute(numValue)
            formatResult(result)
        } catch (e: Exception) {
            "Error"
        }
    }

    fun calculatePower(base: String, exponent: String): String {
        return try {
            val baseValue = base.toDoubleOrNull() ?: return "Error"
            val expValue = exponent.toDoubleOrNull() ?: return "Error"
            val result = baseValue.pow(expValue)
            formatResult(result)
        } catch (e: Exception) {
            "Error"
        }
    }

    fun performBasicCalculation(previousInput: String, currentInput: String, operation: String): String {
        return try {
            val prev = previousInput.toDoubleOrNull() ?: return currentInput
            val current = currentInput.toDoubleOrNull() ?: return currentInput
            val result = when (operation) {
                "+" -> prev + current
                "—" -> prev - current
                "×" -> prev * current
                "÷" -> {
                    if (current != 0.0) prev / current
                    else return "Error"
                }
                "%" -> current / 100
                "^" -> calculatePower(prev.toString(), current.toString()).toDoubleOrNull() ?: return "Error"
                "sin" -> calculate(getSine(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "cos" -> calculate(getCosine(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "tan" -> calculate(getTangent(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "sin⁻¹" -> calculate(getArcSine(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "cos⁻¹" -> calculate(getArcCosine(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "tan⁻¹" -> calculate(getArcTangent(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "log" -> calculate(getLog(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "ln" -> calculate(getLn(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "√x" -> calculate(getSqrt(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "x!" -> calculate(getFactorial(), prev.toString()).toDoubleOrNull() ?: return "Error"
                "1/x" -> calculate(getReciprocal(), prev.toString()).toDoubleOrNull() ?: return "Error"
                else -> current
            }
            formatResult(result)
        } catch (e: Exception) {
            "Error"
        }
    }

    fun formatResult(result: Double): String {
        return if (result == result.toInt().toDouble()) {
            result.toInt().toString()
        } else {
            String.format("%.10f", result).trimEnd('0').trimEnd('.')
        }
    }

    fun isUnaryOperation(op: String): Boolean {
        return op in listOf("sin", "cos", "tan", "sin⁻¹", "cos⁻¹", "tan⁻¹", "log", "ln", "√x", "x!", "1/x")
    }
}
