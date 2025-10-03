package com.example.greetingcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.BoxWithConstraints
import kotlin.math.min
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically

@Preview(showBackground = true, widthDp = 720, heightDp = 1600)
@Composable
fun UI(modifier: Modifier = Modifier) {
    var currentInput by remember { mutableStateOf("0") }
    var previousInput by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf("") }
    var operationPreview by remember { mutableStateOf("") }
    var isOperationPressed by remember { mutableStateOf(false) }
    var showScientificButtons by remember { mutableStateOf(false) }

    fun performCalculation(): String {
        return CalcLogic().performBasicCalculation(previousInput, currentInput, operation)
    }

    fun handleNumberInput(number: String) {
        if (isOperationPressed) {
            currentInput = number
            operationPreview += " $number"
            isOperationPressed = false
        } else {
            currentInput = if (currentInput == "0") number else currentInput + number
            if (operationPreview.isEmpty() || operationPreview.last().isDigit() || operationPreview.last() == '.') {
                operationPreview = operationPreview.dropLastWhile { it.isDigit() || it == '.' } + currentInput
            } else {
                operationPreview += number
            }
        }
    }

    fun handleOperationInput(op: String) {
        if (CalcLogic().isUnaryOperation(op)) {
            previousInput = currentInput
            operation = op
            operationPreview = "$op($currentInput)"
            isOperationPressed = true
        } else {
            if (operation.isNotEmpty() && !isOperationPressed) {
                val result = performCalculation()
                previousInput = result
                currentInput = result
                operationPreview = "$result $op"
            } else {
                previousInput = currentInput
                if (operationPreview.isEmpty()) {
                    operationPreview = "$currentInput $op"
                } else {
                    operationPreview = operationPreview.dropLastWhile { it == ' ' || "+-×÷—%^".contains(it) } + " $op"
                }
            }
            operation = op
            isOperationPressed = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF171616))) {
        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
            Row {
                OPreview(modifier = Modifier.fillMaxWidth(), previewText = operationPreview)
            }
            Row {
                Result(modifier = Modifier.fillMaxWidth(), resultText = currentInput)
            }
            AnimatedVisibility(visible = showScientificButtons, enter = expandVertically(), exit = shrinkVertically())
            {
                ScientificButtonsPanel(onOperationInput = { handleOperationInput(it) })
            }

            BasicCalculatorUI(
                onNumberInput = { handleNumberInput(it) },
                onOperationInput = { handleOperationInput(it) },
                onClear = {
                    currentInput = "0"
                    previousInput = ""
                    operation = ""
                    operationPreview = ""
                    isOperationPressed = false
                },
                onBackspace = {
                    if (currentInput.length > 1 && currentInput != "0") {
                        currentInput = currentInput.dropLast(1)
                        if (operationPreview.isNotEmpty()) {
                            operationPreview = operationPreview.dropLast(1)
                        }
                    } else {
                        currentInput = "0"
                        if (operationPreview.isNotEmpty() && operationPreview.last().isDigit()) {
                            operationPreview = operationPreview.dropLastWhile { it.isDigit() } + "0"
                        }
                    }
                },
                onEquals = {
                    if (operation.isNotEmpty() && previousInput.isNotEmpty()) {
                        val result = performCalculation()
                        currentInput = result
                        operationPreview = ""
                        previousInput = ""
                        operation = ""
                        isOperationPressed = false
                    }
                },
                onDecimal = {
                    if (!currentInput.contains(".")) {
                        if (isOperationPressed) {
                            currentInput = "0."
                            operationPreview += " 0."
                            isOperationPressed = false
                        } else {
                            currentInput += "."
                            operationPreview = operationPreview.dropLastWhile { it.isDigit() || it == '.' } + currentInput
                        }
                    }
                },
                onToggleScientific = {
                    showScientificButtons = !showScientificButtons
                }
            )
        }
    }
}

@Composable
fun ScientificButtonsPanel(onOperationInput: (String) -> Unit)
{
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1F1F1F)).padding(horizontal = 8.dp, vertical = 8.dp))
    {
        Row {
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "sin", buttonAction = { onOperationInput("sin") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "cos", buttonAction = { onOperationInput("cos") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "tan", buttonAction = { onOperationInput("tan") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "^", buttonAction = { onOperationInput("^") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "√x", buttonAction = { onOperationInput("√x") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "x!", buttonAction = { onOperationInput("x!") })
        }
        Row {
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "sin⁻¹", buttonAction = { onOperationInput("sin⁻¹") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "cos⁻¹", buttonAction = { onOperationInput("cos⁻¹") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "tan⁻¹", buttonAction = { onOperationInput("tan⁻¹") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "log", buttonAction = { onOperationInput("log") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "ln", buttonAction = { onOperationInput("ln") })
            CalButton(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF575757), textButton = "1/x", buttonAction = { onOperationInput("1/x") })
        }
    }
}

@Composable
fun BasicCalculatorUI(
    onNumberInput: (String) -> Unit,
    onOperationInput: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onEquals: () -> Unit,
    onDecimal: () -> Unit,
    onToggleScientific: () -> Unit
) {
    Row {
        CalButton(modifier = Modifier, backgroundColor = Color(0xFF998050), textButton = "AC", buttonAction = onClear)
        CalButton(modifier = Modifier, backgroundColor = Color(0xFF998050), textButton = "Sci", buttonAction = onToggleScientific)
        CalButton(modifier = Modifier, backgroundColor = Color(0xFF998050), textButton = "%", buttonAction = { onOperationInput("%") })
        CalButton(modifier = Modifier, backgroundColor = Color(0xFF998050), textButton = "÷", buttonAction = { onOperationInput("÷") })
    }
    Row {
        CalButton(modifier = Modifier, textButton = "7", buttonAction = { onNumberInput("7") })
        CalButton(modifier = Modifier, textButton = "8", buttonAction = { onNumberInput("8") })
        CalButton(modifier = Modifier, textButton = "9", buttonAction = { onNumberInput("9") })
        CalButton(modifier = Modifier, backgroundColor = Color(0xFF998050), textButton = "×", buttonAction = { onOperationInput("×") })
    }
    Row {
        CalButton(modifier = Modifier, textButton = "4", buttonAction = { onNumberInput("4") })
        CalButton(modifier = Modifier, textButton = "5", buttonAction = { onNumberInput("5") })
        CalButton(modifier = Modifier, textButton = "6", buttonAction = { onNumberInput("6") })
        CalButton(modifier = Modifier, backgroundColor = Color(0xFF998050), textButton = "—", buttonAction = { onOperationInput("—") })
    }
    Row {
        CalButton(modifier = Modifier, textButton = "1", buttonAction = { onNumberInput("1") })
        CalButton(modifier = Modifier, textButton = "2", buttonAction = { onNumberInput("2") })
        CalButton(modifier = Modifier, textButton = "3", buttonAction = { onNumberInput("3") })
        CalButton(modifier = Modifier, backgroundColor = Color(0xFF998050), textButton = "+", buttonAction = { onOperationInput("+") })
    }
    Row {
        CalButton(modifier = Modifier, textButton = "0", buttonAction = { onNumberInput("0") })
        CalButton(modifier = Modifier, textButton = ".", buttonAction = onDecimal)
        CalButton(modifier = Modifier, textButton = "⌫", buttonAction = onBackspace)
        CalButton(modifier = Modifier, backgroundColor = Color(0xFFd17a21), textButton = "=", buttonAction = onEquals)
    }
}

@Composable
fun CalButton(modifier: Modifier = Modifier, textButton: String, buttonAction: () -> Unit, backgroundColor: Color = Color(0xFF575757))
{
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val buttonWidth = screenWidth / 4
    val buttonHeight = 60.dp
    val fontSize = 20.sp

    Button(modifier = modifier.width(buttonWidth).height(buttonHeight).padding(horizontal = 6.dp, vertical = 4.dp), onClick = buttonAction, contentPadding = PaddingValues(all = 0.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, contentColor = Color.White))
    {
        Text(text = textButton, fontSize = fontSize, textAlign = TextAlign.Center)
    }
}

@Composable
fun Result(modifier: Modifier = Modifier, resultText: String) {
    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).background(Color(0xFF2A2A2A)).padding(16.dp))
    {
        BoxWithConstraints(modifier = Modifier.align(Alignment.CenterEnd).height(80.dp).wrapContentHeight(align = Alignment.CenterVertically))
        {
            val availableWidth = this.maxWidth
            val baseSize = 50f
            val estimatedTextWidth = resultText.length * 30f
            val scaleFactor = if (estimatedTextWidth > availableWidth.value) {
                (availableWidth.value / estimatedTextWidth).coerceIn(0.4f, 1.0f)
            } else { 1.0f }

            val dynamicFontSize = (baseSize * scaleFactor).sp
            Text(text = resultText, fontSize = min(dynamicFontSize.value, 50f).sp, color = Color.White, textAlign = TextAlign.End, maxLines = 1, softWrap = false)
        }
    }
}

@Composable
fun OPreview(modifier: Modifier = Modifier, previewText: String) {
    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).background(Color(0xFF1F1F1F)).padding(12.dp))
    {
        Text(text = previewText, fontSize = 32.sp, color = Color.Gray, textAlign = TextAlign.End, modifier = Modifier.align(Alignment.CenterEnd).height(120.dp).wrapContentHeight(align = Alignment.CenterVertically))
    }
}
