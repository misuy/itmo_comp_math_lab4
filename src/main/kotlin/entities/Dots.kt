package entities

import java.awt.Color
import java.io.InputStream
import java.util.*

class Dot(val x: Double, val y: Double);

class Dots(val dots: List<Dot>);

class DotsGraph(val dots: Dots, val color: Color);

fun readDots(scanner: Scanner): Dots {
    val dots: MutableList<Dot> = mutableListOf();
    val dotsCount: Int = scanner.nextInt();
    for (i: Int in 0 until dotsCount) {
        dots.add(Dot(scanner.nextDouble(), scanner.nextDouble()));
    }
    return Dots(dots);
}
