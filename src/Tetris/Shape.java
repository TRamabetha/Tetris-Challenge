package Tetris;

import java.util.Random;

public class Shape {

    protected enum gameShapes { NoShape, ZShape, SShape, LineShape,
        TShape, SquareShape, LShape, InverseLShape }

    private gameShapes pieceShape;
    private int coords[][];
    private int[][][] coordsTable;


    public Shape() { initShape(); }

    private void initShape() {

        coords = new int[4][2];

        coordsTable = new int[][][] {
                { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
                { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
                { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
                { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
                { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
                { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
                { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
                { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
        };

        setShape(gameShapes.NoShape);
    }

    protected void setShape(gameShapes shape) {

        for (int i = 0; i < 4 ; i++) {

            for (int j = 0; j < 2; ++j) { coords[i][j] = coordsTable[shape.ordinal()][i][j]; }
        }

        pieceShape = shape;
    }

    private void setX(int index, int x) { coords[index][0] = x; }
    
    
    private void setY(int index, int y) { coords[index][1] = y; }
    
    
    public int x(int index) { return coords[index][0]; }
    
    
    public int y(int index) { return coords[index][1]; }
    
    
    public gameShapes getShape()  { return pieceShape; }

    
    public void setRandomShape() {

        var rand = new Random();
        int randShape = Math.abs(rand.nextInt()) % 7 + 1;

        gameShapes[] values = gameShapes.values();
        setShape(values[randShape]);
    }

    public int minX() {

        int m = coords[0][0];

        for (int i=0; i < 4; i++) { m = Math.min(m, coords[i][0]); }

        return m;
    }


    public int minY() {

        int m = coords[0][1];

        for (int i=0; i < 4; i++) { m = Math.min(m, coords[i][1]); }

        return m;
    }

 
    public Shape rotate() {

        if (pieceShape == gameShapes.SquareShape) { return this; }

        var result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {

            result.setX(i, -y(i));
            result.setY(i, x(i));
        }

        return result;
    }
}

