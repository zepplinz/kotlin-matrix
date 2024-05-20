interface Matrix<T> {
    readonly cols: number;
    readonly rows: number;
    get(x: number, y: number): T;
}

interface MutableMatrix<T> extends Matrix<T> {
    set(x: number, y: number, value: T): void;
}

abstract class AbstractMatrix<T> implements Matrix<T> {
    abstract readonly cols: number;
    abstract readonly rows: number;
    abstract get(x: number, y: number): T;

    toString(): string {
        let sb: string[] = [];
        sb.push('[');
        this.forEachIndexed((x, y, value) => {
            if (x === 0) sb.push('[');
            sb.push(value.toString());
            if (x === this.cols - 1) {
                sb.push(']');
                if (y < this.rows - 1) sb.push(", ");
            } else {
                sb.push(", ");
            }
        });
        sb.push(']');
        return sb.join('');
    }

    equals(other: any): boolean {
        if (!(other instanceof AbstractMatrix)) return false;
        if (this.rows !== other.rows || this.cols !== other.cols) return false;

        let eq = true;
        this.forEachIndexed((x, y, value) => {
            if (value === null) {
                if (other.get(x, y) !== null) {
                    eq = false;
                    return;
                }
            } else {
                if (!value.equals(other.get(x, y))) {
                    eq = false;
                    return;
                }
            }
        });
        return eq;
    }

    hashCode(): number {
        let h = 17;
        h = h * 39 + this.cols;
        h = h * 39 + this.rows;
        this.forEach(value => {
            h = h * 37 + (value?.hashCode() ?? 1);
        });
        return h;
    }

    forEachIndexed(action: (x: number, y: number, value: T) => void): void {
        for (let y = 0; y < this.rows; y++) {
            for (let x = 0; x < this.cols; x++) {
                action(x, y, this.get(x, y));
            }
        }
    }

    forEach(action: (value: T) => void): void {
        this.forEachIndexed((x, y, value) => action(value));
    }
}

class TransposedMatrix<T> extends AbstractMatrix<T> {
    protected original: Matrix<T>;

    constructor(original: Matrix<T>) {
        super();
        this.original = original;
    }

    get cols(): number {
        return this.original.rows;
    }

    get rows(): number {
        return this.original.cols;
    }

    get(x: number, y: number): T {
        return this.original.get(y, x);
    }
}

class TransposedMutableMatrix<T> extends TransposedMatrix<T> implements MutableMatrix<T> {
    constructor(original: MutableMatrix<T>) {
        super(original);
    }

    set(x: number, y: number, value: T): void {
        (this.original as MutableMatrix<T>).set(y, x, value);
    }
}

function asTransposed<T>(matrix: Matrix<T>): Matrix<T> {
    return new TransposedMatrix(matrix);
}

function asTransposedMutable<T>(matrix: MutableMatrix<T>): MutableMatrix<T> {
    return new TransposedMutableMatrix(matrix);
}

class ListMatrix<T> extends AbstractMatrix<T> {
    readonly cols: number;
    readonly rows: number;
    protected list: T[];

    constructor(cols: number, rows: number, list: T[]) {
        super();
        this.cols = cols;
        this.rows = rows;
        this.list = list;
    }

    get(x: number, y: number): T {
        return this.list[y * this.cols + x];
    }
}

class MutableListMatrix<T> extends ListMatrix<T> implements MutableMatrix<T> {
    constructor(cols: number, rows: number, list: T[]) {
        super(cols, rows, list);
    }

    set(x: number, y: number, value: T): void {
        (this.list as T[])[y * this.cols + x] = value;
    }
}

function matrixOf<T>(cols: number, rows: number, ...elements: T[]): Matrix<T> {
    return new ListMatrix(cols, rows, elements);
}

function mutableMatrixOf<T>(cols: number, rows: number, ...elements: T[]): MutableMatrix<T> {
    return new MutableListMatrix(cols, rows, elements);
}

function prepareListForMatrix<T>(cols: number, rows: number, init: (x: number, y: number) => T): T[] {
    const list: T[] = new Array(cols * rows);
    for (let y = 0; y < rows; y++) {
        for (let x = 0; x < cols; x++) {
            list[y * cols + x] = init(x, y);
        }
    }
    return list;
}

function createMatrix<T>(cols: number, rows: number, init: (x: number, y: number) => T): Matrix<T> {
    return new ListMatrix(cols, rows, prepareListForMatrix(cols, rows, init));
}

function createMutableMatrix<T>(cols: number, rows: number, init: (x: number, y: number) => T): MutableMatrix<T> {
    return new MutableListMatrix(cols, rows, prepareListForMatrix(cols, rows, init));
}

function mapIndexed<T, U>(matrix: Matrix<T>, transform: (x: number, y: number, value: T) => U): Matrix<U> {
    return createMatrix(matrix.cols, matrix.rows, (x, y) => transform(x, y, matrix.get(x, y)));
}

function map<T, U>(matrix: Matrix<T>, transform: (value: T) => U): Matrix<U> {
    return mapIndexed(matrix, (x, y, value) => transform(value));
}

function forEachIndexed<T>(matrix: Matrix<T>, action: (x: number, y: number, value: T) => void): void {
    for (let y = 0; y < matrix.rows; y++) {
        for (let x = 0; x < matrix.cols; x++) {
            action(x, y, matrix.get(x, y));
        }
    }
}

function forEach<T>(matrix: Matrix<T>, action: (value: T) => void): void {
    forEachIndexed(matrix, (x, y, value) => action(value));
}

function toList<T>(matrix: Matrix<T>): T[] {
    return prepareListForMatrix(matrix.cols, matrix.rows, (x, y) => matrix.get(x, y));
}

function toMutableList<T>(matrix: Matrix<T>): T[] {
    return prepareListForMatrix(matrix.cols, matrix.rows, (x, y) => matrix.get(x, y));
}

function toArrayList<T>(iterable: Iterable<T>, size: number): T[] {
    const list: T[] = new Array(size);
    const itr = iterable[Symbol.iterator]();

    for (let i = 0; i < size; i++) {
        const next = itr.next();
        if (next.done) {
            throw new Error("No enough elements");
        }
        list[i] = next.value;
    }
    return list;
}

function toMatrix<T>(iterable: Iterable<T>, cols: number, rows: number): Matrix<T> {
    const list = toArrayList(iterable, cols * rows);
    return new ListMatrix(cols, rows, list);
}

function toMutableMatrix<T>(iterable: Iterable<T>, cols: number, rows: number): MutableMatrix<T> {
    const list = toArrayList(iterable, cols * rows);
    return new MutableListMatrix(cols, rows, list);
}