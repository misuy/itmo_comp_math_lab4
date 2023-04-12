//^^

import entities.*
import entities.Function
import ui.ChartPanel
import java.awt.Color
import java.awt.Dimension
import java.io.FileInputStream
import java.util.*
import javax.swing.JFrame
import kotlin.math.max
import kotlin.math.min

val FRAME_SIZE: Dimension = Dimension(1000, 1000);

fun main(args: Array<String>) {
    val scanner: Scanner = if (args.size == 1) Scanner(FileInputStream(args[0])) else Scanner(System.`in`);

    val dots = readDots(scanner);
    println(buildDotsDescription(dots));

    var minStandardDeviation = Double.POSITIVE_INFINITY;
    var bestApproximation: Pair<Approximation, Function>? = null;
    approximations.forEach {
        it.approximate(dots);
        val function: Function = it.buildFunction();
        print(it.description(function, dots));
        val standardDeviation: Double = calcStandardDeviation(function, dots);
        if (standardDeviation < minStandardDeviation) {
            minStandardDeviation = standardDeviation;
            bestApproximation = Pair(it, function);
        }
    }

    if (bestApproximation == null) throw IllegalArgumentException("there is no approximations");
    println("the best approximation is ${bestApproximation?.first?.name} with standard deviation = $minStandardDeviation");
    val function: Function = bestApproximation?.second as Function;


    val chartPanel: ChartPanel = ChartPanel();
    val horizontalSegmentSize: Double = dots.dots.maxOf { it.x } - dots.dots.minOf { it.x };
    val verticalSegmentSize: Double = dots.dots.maxOf { it.y } - dots.dots.minOf { it.y };
    chartPanel.setChartSegments(
        Segment(
            min(dots.dots.minOf { it.x }, 0.0) - horizontalSegmentSize / 4,
            max(dots.dots.maxOf { it.x }, 0.0) + horizontalSegmentSize / 4
        ),
        Segment(
            min(dots.dots.minOf { it.y }, 0.0) - verticalSegmentSize / 4,
            max(dots.dots.maxOf { it.y }, 0.0) + verticalSegmentSize / 4
        )
    );
    chartPanel.addFunction(FunctionGraph(function, Color.BLUE));
    chartPanel.addDots(DotsGraph(dots, Color.RED));

    val frame: JFrame = JFrame("chart");
    frame.add(chartPanel);
    frame.size = FRAME_SIZE;
    frame.isVisible = true;
}
