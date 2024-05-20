// Define the Matrix interface
interface Matrix<T extends number> {
    rows: number;
    cols: number;
    mapIndexed(callback: (x: number, y: number, value: T) => number): Matrix<number>;
    map(callback: (value: T) => number): Matrix<number>;
    [key: number]: T[];
}
// Define the plus operator
function plus<M extends number, N extends number>(matrix1: Matrix<M>, matrix2: Matrix<N>): Matrix<number> {
    if (matrix1.rows !== matrix2.rows || matrix1.cols !== matrix2.cols) {
        throw new Error("Matrices not match");
    }
    return matrix1.mapIndexed((x, y, value) => value + matrix2[x][y]);
}
// Define the unaryMinus operator
function unaryMinus<N extends number>(matrix: Matrix<N>): Matrix<number> {
    return matrix.map(value => -value);
}
// Define the minus operator
function minus<M extends number, N extends number>(matrix1: Matrix<M>, matrix2: Matrix<N>): Matrix<number> {
    return plus(matrix1, unaryMinus(matrix2));
}
// Define the times operator for matrix multiplication
function times<M extends number, N extends number>(matrix1: Matrix<M>, matrix2: Matrix<N>): Matrix<number> {
    if (matrix1.rows !== matrix2.rows || matrix1.cols !== matrix2.cols) {
        throw new Error("Matrices not match");
    }
    return matrix1.mapIndexed((x, y, value) => value * matrix2[x][y]);
}
// Define the times operator for scalar multiplication
function timesScalar<M extends number>(matrix: Matrix<M>, scalar: number): Matrix<number> {
    return matrix.map(value => value * scalar);
}
// Define the times operator for scalar multiplication (reversed)
function scalarTimes<M extends number>(scalar: number, matrix: Matrix<M>): Matrix<number> {
    return timesScalar(matrix, scalar);
}
// Define the x operator for matrix multiplication
function x<M extends number, N extends number>(matrix1: Matrix<M>, matrix2: Matrix<N>): Matrix<number> {
    if (matrix1.rows !== matrix2.cols) {
        throw new Error("Matrices not match");
    }
    return createMatrix(matrix1.cols, matrix2.rows, (x, y) => {
        let value = 0;
        for (let i = 0; i < matrix1.rows; i++) {
            value += matrix1[x][i] * matrix2[i][y];
        }
        return value;
    });
}
// Helper function to create a matrix
function createMatrix(rows: number, cols: number, callback: (x: number, y: number) => number): Matrix<number> {
    const matrix: Matrix<number> = { rows, cols, mapIndexed: null, map: null };
    for (let x = 0; x < rows; x++) {
        matrix[x] = [];
        for (let y = 0; y < cols; y++) {
            matrix[x][y] = callback(x, y);
        }
    }
    return matrix;
}