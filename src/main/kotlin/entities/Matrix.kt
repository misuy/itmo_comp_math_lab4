package entities

class Matrix(val width: Int, val height: Int, val matrix: List<List<Double>>) {
    init {
        if (this.matrix.size != this.height) throw IllegalArgumentException("matrix height isn't match provided height");
        for (row: List<Double> in this.matrix) if (row.size != this.width) throw IllegalArgumentException("matrix width isn't match provided width");
    }

    fun getMinor(rows: List<Int>, columns: List<Int>): Double {
        if (rows.size != columns.size) throw IllegalArgumentException("provided minor isn't square matrix");
        if (rows.isEmpty()) return 1.0;
        val row: Int = rows[0];
        var minor: Double = 0.0;
        var columnsCount = 0;
        for (column: Int in columns) {
            if (columnsCount % 2 == 0) minor += this.matrix[row][column] * getMinor(rows.subList(1, rows.size), columns.subList(0, columnsCount) + columns.subList(columnsCount + 1, columns.size));
            else minor -= this.matrix[row][column] * getMinor(rows.subList(1, rows.size), columns.subList(0, columnsCount) + columns.subList(columnsCount + 1, columns.size));
            columnsCount++;
        }
        return minor;
    }
}