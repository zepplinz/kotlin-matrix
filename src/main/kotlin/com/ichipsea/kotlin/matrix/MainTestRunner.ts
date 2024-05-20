// Define the Matrix class
class Matrix {
    values: number[][];

    constructor(values: number[][]) {
        this.values = values;
    }

    multiply(matrix: Matrix): Matrix {
        const result: number[][] = [];
        for (let i = 0; i < this.values.length; i++) {
            result[i] = [];
            for (let j = 0; j < matrix.values[0].length; j++) {
                result[i][j] = 0;
                for (let k = 0; k < this.values[0].length; k++) {
                    result[i][j] += this.values[i][k] * matrix.values[k][j];
                }
            }
        }
        return new Matrix(result);
    }

    printMatrix(): void {
        for (const row of this.values) {
            console.log(row.join(" "));
        }
    }
}

// Main function
function main() {
    const matrixA = new Matrix([
        [1, 2, 3],
        [4, 5, 6]
    ]);

    const matrixB = new Matrix([
        [7, 8],
        [9, 10],
        [11, 12]
    ]);

    const result = matrixA.multiply(matrixB);
    console.log("Result of matrix multiplication:");
    result.printMatrix();
}

// Execute the main function
main();