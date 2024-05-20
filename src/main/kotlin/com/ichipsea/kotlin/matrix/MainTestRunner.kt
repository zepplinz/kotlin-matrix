package com.ichipsea.kotlin.matrix

fun main() {
    val matrixA = Matrix(arrayOf(
        arrayOf(1, 2, 3),
        arrayOf(4, 5, 6)
    ))

    val matrixB = Matrix(arrayOf(
        arrayOf(7, 8),
        arrayOf(9, 10),
        arrayOf(11, 12)
    ))

    val result = matrixA.multiply(matrixB)
    println("Result of matrix multiplication:")
    result.printMatrix()
}

fun Matrix.printMatrix() {
    for (row in this.values) {
        println(row.joinToString(" "))
    }
}
