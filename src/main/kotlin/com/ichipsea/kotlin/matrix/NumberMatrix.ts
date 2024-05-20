// Define the Matrix interface
interface Matrix<T extends number> {
    rows: number;
    cols: number;
    get(x: number, y: number): T;
    map<U extends number>(callback: (value: T, x: number, y: number) => U): Matrix<U>;
    mapIndexed<U extends number>(callback: (x: number, y: number, value: T) => U): Matrix<U>;
}
// Define the createMatrix function
function createMatrix<T extends number>(rows: number, cols: number, callback: (x: number, y: number) => T): Matrix<T> {
    // Implementation of createMatrix
    // This is a placeholder implementation. Replace it with the actual implementation.
    return {
        rows,
        cols,
        get: (x, y) => callback(x, y),
        map: (callback) => createMatrix(rows, cols, (x, y) => callback(callback(x, y), x, y)),
        mapIndexed: (callback) => createMatrix(rows, cols, (x, y) => callback(x, y, callback(x, y)))
    };
}
// Define the plus operator
function plus<M extends number, N extends number>(matrix1: Matrix<M>, matrix2: Matrix<N>): Matrix<number> {
    if (matrix1.rows !== matrix2.rows || matrix1.cols !== matrix2.cols) {
        throw new Error("Matrices not match");
    }
    return matrix1.mapIndexed((x, y, value) => value + matrix2.get(x, y));
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
    return matrix1.mapIndexed((x, y, value) => value * matrix2.get(x, y));
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
            value += matrix1.get(x, i) * matrix2.get(i, y);
        }
        return value;
    });
}