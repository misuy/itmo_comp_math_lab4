package entities

import java.awt.Color

class Dot(val x: Double, val y: Double);

class Dots(val dots: List<Dot>);

class DotsGraph(val dots: Dots, val color: Color);