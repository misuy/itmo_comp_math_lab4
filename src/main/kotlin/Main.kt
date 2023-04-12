import entities.*
import entities.Function
import ui.ChartPanel
import java.awt.Color
import java.awt.Dimension
import javax.swing.JFrame
import kotlin.math.max
import kotlin.math.min

val FRAME_SIZE: Dimension = Dimension(1000, 1000);

fun main(args: Array<String>) {
    val dots = Dots(listOf(Dot(1.0, 1.5), Dot(3.0, 4.0), Dot(2.0, 1.0), Dot(1.0, -10.0), Dot(0.1, 0.0), Dot(7.0, 7.0)));

    print(buildDotsDescription(dots));

    val approximationResults: MutableMap<Approximation, Function> = mutableMapOf();

    approximations.forEach {
        val checkResult: Pair<Boolean, String> = it.check(dots);
        print(checkResult.second);
        if (it.check(dots).first) {
            it.approximate(dots);
            approximationResults[it] = it.buildFunction();
        }
    }

    var minStandardDeviation = Double.POSITIVE_INFINITY;
    var bestApproximation: Pair<Approximation, Function>? = null;

    approximationResults.entries.forEach {
        print(it.key.description(it.value, dots));
        val standardDeviation: Double = calcStandardDeviation(it.value, dots);
        if (standardDeviation < minStandardDeviation) {
            minStandardDeviation = standardDeviation;
            bestApproximation = Pair(it.key, it.value);
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
        Segment(min(dots.dots.minOf { it.y }, 0.0) - verticalSegmentSize / 4,
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