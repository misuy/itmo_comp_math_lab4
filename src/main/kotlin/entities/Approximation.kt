package entities

import util.*
import java.util.Scanner
import kotlin.math.*

fun calcStandardDeviation(function: Function, dots: Dots): Double {
    return sqrt(dots.dots.sumOf { (function.getValueByX(it.x) - it.y).pow(2) } / dots.dots.size.toDouble());
}

fun calcPirsonCorrelationCoefficient(dots: Dots): Double {
    val xMean: Double = dots.dots.sumOf { it.x } / dots.dots.size;
    val yMean: Double = dots.dots.sumOf { it.y } / dots.dots.size;
    return dots.dots.sumOf { (it.x - xMean) * (it.y - yMean) } / sqrt(dots.dots.sumOf { (it.x - xMean).pow(2) } * dots.dots.sumOf { (it.y - yMean).pow(2) });
}

fun buildDotsDescription(dots: Dots): String {
    val builder: StringBuilder = StringBuilder();
    builder.append("dots:\n");
    builder.append("X = ${dots.dots.map { it.x }};\n");
    builder.append("Y = ${dots.dots.map { it.y }};\n");
    builder.append("pirson coefficient = ${calcPirsonCorrelationCoefficient(dots)};\n")
    return builder.toString();
}


class Approximation(val name: String, private val approximationMethod: (Dots, MutableMap<String, Double>) -> Unit, private val expression: String) {
    private var readyToBuild: Boolean = false;
    private val parameters: MutableMap<String, Double> = mutableMapOf();

    fun approximate(dots: Dots) {
        this.approximationMethod(dots, this.parameters);
        this.readyToBuild = true;
    }

    fun description(function: Function, dots: Dots): String {
        val builder: StringBuilder = StringBuilder();
        builder.append("-----$name-----\n");
        builder.append("f(x) = $expression");
        this.parameters.keys.forEach { builder.append(", $it = ${this.parameters[it]}") }
        builder.append(";\n")
        builder.append("f(X) = ${dots.dots.map { function.getValueByX(it.x) }};\n");
        builder.append("E = ${dots.dots.map { function.getValueByX(it.x) - it.y }};\n");
        builder.append("standard deviation = ${calcStandardDeviation(function, dots)};\n\n");
        return builder.toString();
    }

    fun buildFunction(): Function {
        if (!this.readyToBuild) throw IllegalArgumentException("function isn't ready to build");
        return Function(
            buildCompTree(parseTokens(this.expression, constants + this.parameters, openingBrackets, closingBrackets, unaryPreOperations, unaryPostOperations, binaryOperations)),
            listOf("x"),
        );
    }
}

val linearApproximation: Approximation = Approximation(
    name = "linear approximation",
    approximationMethod =  fun (dots: Dots, parameters: MutableMap<String, Double>) {
        val matrix: Matrix = Matrix(3, 2,
            listOf(
                listOf(
                    dots.dots.size.toDouble(),
                    dots.dots.sumOf { it.x },
                    dots.dots.sumOf { it.y },
                ),
                listOf(
                    dots.dots.sumOf { it.x },
                    dots.dots.sumOf { it.x.pow(2) },
                    dots.dots.sumOf { it.x * it.y },
                ),
            ),
        );
        val mainMinor = matrix.getMinor(listOf(0, 1), listOf(0, 1));
        parameters["a_0"] = matrix.getMinor(listOf(0, 1), listOf(2, 1)) / mainMinor;
        parameters["a_1"] = matrix.getMinor(listOf(0, 1), listOf(0, 2)) / mainMinor;
    },
    expression = "a_0+a_1*x",
);

val quadraticApproximation: Approximation = Approximation(
    name = "quadratic approximation",
    approximationMethod = fun (dots: Dots, parameters: MutableMap<String, Double>) {
        val matrix: Matrix = Matrix(4, 3,
            listOf(
                listOf(
                    dots.dots.size.toDouble(),
                    dots.dots.sumOf { it.x },
                    dots.dots.sumOf { it.x.pow(2) },
                    dots.dots.sumOf { it.y },
                ),
                listOf(
                    dots.dots.sumOf { it.x },
                    dots.dots.sumOf { it.x.pow(2) },
                    dots.dots.sumOf { it.x.pow(3) },
                    dots.dots.sumOf { it.x * it.y },
                ),
                listOf(
                    dots.dots.sumOf { it.x.pow(2) },
                    dots.dots.sumOf { it.x.pow(3) },
                    dots.dots.sumOf { it.x.pow(4) },
                    dots.dots.sumOf { it.x.pow(2) * it.y },
                ),
            ),
        );
        val mainMinor = matrix.getMinor(listOf(0, 1, 2), listOf(0, 1, 2));
        parameters["a_0"] = matrix.getMinor(listOf(0, 1, 2), listOf(3, 1, 2)) / mainMinor;
        parameters["a_1"] = matrix.getMinor(listOf(0, 1, 2), listOf(0, 3, 2)) / mainMinor;
        parameters["a_2"] = matrix.getMinor(listOf(0, 1, 2), listOf(0, 1, 3)) / mainMinor;
    },
    expression = "a_0+a_1*x+a_2*x^2",
);

val cubicApproximation: Approximation = Approximation(
    name = "cubic approximation",
    approximationMethod = fun (dots: Dots, parameters: MutableMap<String, Double>) {
        val matrix: Matrix = Matrix(5, 4,
            listOf(
                listOf(
                    dots.dots.size.toDouble(),
                    dots.dots.sumOf { it.x },
                    dots.dots.sumOf { it.x.pow(2) },
                    dots.dots.sumOf { it.x.pow(3) },
                    dots.dots.sumOf { it.y },
                ),
                listOf(
                    dots.dots.sumOf { it.x },
                    dots.dots.sumOf { it.x.pow(2) },
                    dots.dots.sumOf { it.x.pow(3) },
                    dots.dots.sumOf { it.x.pow(4) },
                    dots.dots.sumOf { it.x * it.y },
                ),
                listOf(
                    dots.dots.sumOf { it.x.pow(2) },
                    dots.dots.sumOf { it.x.pow(3) },
                    dots.dots.sumOf { it.x.pow(4) },
                    dots.dots.sumOf { it.x.pow(5) },
                    dots.dots.sumOf { it.x.pow(2) * it.y },
                ),
                listOf(
                    dots.dots.sumOf { it.x.pow(3) },
                    dots.dots.sumOf { it.x.pow(4) },
                    dots.dots.sumOf { it.x.pow(5) },
                    dots.dots.sumOf { it.x.pow(6) },
                    dots.dots.sumOf { it.x.pow(3) * it.y },
                ),
            ),
        );
        val mainMinor = matrix.getMinor(listOf(0, 1, 2, 3), listOf(0, 1, 2, 3));
        parameters["a_0"] = matrix.getMinor(listOf(0, 1, 2, 3), listOf(4, 1, 2, 3)) / mainMinor;
        parameters["a_1"] = matrix.getMinor(listOf(0, 1, 2, 3), listOf(0, 4, 2, 3)) / mainMinor;
        parameters["a_2"] = matrix.getMinor(listOf(0, 1, 2, 3), listOf(0, 1, 4, 3)) / mainMinor;
        parameters["a_3"] = matrix.getMinor(listOf(0, 1, 2, 3), listOf(0, 1, 2, 4)) / mainMinor;
    },
    expression = "a_0+a_1*x+a_2*x^2+a_3*x^3",
);

val exponentialApproximation: Approximation = Approximation(
    name = "exponential approximation",
    approximationMethod = fun (dots: Dots, parameters: MutableMap<String, Double>) {
        val matrix: Matrix = Matrix(3, 2,
            listOf(
                listOf(
                    dots.dots.size.toDouble(),
                    dots.dots.sumOf { it.x },
                    dots.dots.sumOf { log(it.y, E) },
                ),
                listOf(
                    dots.dots.sumOf { it.x },
                    dots.dots.sumOf { it.x.pow(2) },
                    dots.dots.sumOf { it.x * log(it.y, E) },
                ),
            ),
        );
        val mainMinor = matrix.getMinor(listOf(0, 1), listOf(0, 1));
        parameters["a_0"] = E.pow(matrix.getMinor(listOf(0, 1), listOf(2, 1)) / mainMinor);
        parameters["a_1"] = matrix.getMinor(listOf(0, 1), listOf(0, 2)) / mainMinor;
    },
    expression = "a_0*e^(a_1*x)",
);

val logarithmicApproximation: Approximation = Approximation(
    name = "logarithmic approximation",
    approximationMethod = fun (dots: Dots, parameters: MutableMap<String, Double>) {
        val matrix: Matrix = Matrix(3, 2,
            listOf(
                listOf(
                    dots.dots.size.toDouble(),
                    dots.dots.sumOf { log(it.x, E) },
                    dots.dots.sumOf { it.y },
                ),
                listOf(
                    dots.dots.sumOf { log(it.x, E) },
                    dots.dots.sumOf { log(it.x, E).pow(2) },
                    dots.dots.sumOf { log(it.x, E) * it.y },
                ),
            ),
        );
        val mainMinor = matrix.getMinor(listOf(0, 1), listOf(0, 1));
        parameters["a_0"] = matrix.getMinor(listOf(0, 1), listOf(2, 1)) / mainMinor;
        parameters["a_1"] = matrix.getMinor(listOf(0, 1), listOf(0, 2)) / mainMinor;
    },
    expression = "a_0+a_1*ln(x)",
);

val powerApproximation: Approximation = Approximation(
    name = "power approximation",
    approximationMethod = fun (dots: Dots, parameters: MutableMap<String, Double>) {
        val matrix: Matrix = Matrix(3, 2,
            listOf(
                listOf(
                    dots.dots.size.toDouble(),
                    dots.dots.sumOf { log(it.x, E) },
                    dots.dots.sumOf { log(it.y, E) },
                ),
                listOf(
                    dots.dots.sumOf { log(it.x, E) },
                    dots.dots.sumOf { log(it.x, E).pow(2) },
                    dots.dots.sumOf { log(it.x, E) * log(it.y, E) },
                ),
            ),
        );
        val mainMinor = matrix.getMinor(listOf(0, 1), listOf(0, 1));
        parameters["a_0"] = E.pow(matrix.getMinor(listOf(0, 1), listOf(2, 1)) / mainMinor);
        parameters["a_1"] = matrix.getMinor(listOf(0, 1), listOf(0, 2)) / mainMinor;
    },
    expression = "a_0*x^a_1",
);

val approximations: List<Approximation> = listOf(linearApproximation, quadraticApproximation, cubicApproximation, exponentialApproximation, logarithmicApproximation, powerApproximation);