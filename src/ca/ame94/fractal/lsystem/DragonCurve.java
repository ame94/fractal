package ca.ame94.fractal.lsystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class DragonCurve {
    private static BufferedImage image;
    private static LinkedList<Character> list1 = new LinkedList<>();
    private static LinkedList<Character> list2 = new LinkedList<>();
    private static boolean writingToList1 = true; // if list1 will hold the next round of data
    private static int imageWidth = 0;
    private static int imageHeight = 0;
    private static int drawMovements = 0;
    private enum Direction { UP, RIGHT, DOWN, LEFT }
    private static int x = 0, y = 0;
    private static int max_x = 0, max_y = 0, min_x = 0, min_y = 0;
    private static Direction facing = Direction.UP;

    private static Direction getNewDirectionForTurn(char turn) {
        Direction newDirection = facing;
        switch (turn) {
            case '+':
                switch (newDirection) {
                    case UP:
                        newDirection = Direction.RIGHT;
                        break;
                    case RIGHT:
                        newDirection = Direction.DOWN;
                        break;
                    case DOWN:
                        newDirection = Direction.LEFT;
                        break;
                    case LEFT:
                        newDirection = Direction.UP;
                        break;
                }
                break;
            case '-':
                switch (newDirection) {
                    case UP:
                        newDirection = Direction.LEFT;
                        break;
                    case LEFT:
                        newDirection = Direction.DOWN;
                        break;
                    case DOWN:
                        newDirection = Direction.RIGHT;
                        break;
                    case RIGHT:
                        newDirection = Direction.UP;
                        break;
                }
                break;
        }
        return newDirection;
    }

    private static void moveFacing(Direction direction) {
        switch (direction) {
            case UP:
                --y;
                break;
            case RIGHT:
                ++x;
                break;
            case DOWN:
                ++y;
                break;
            case LEFT:
                --x;
                break;
        }

        // maintain max and min values
        if (x > max_x) max_x = x;
        if (x < min_x) min_x = x;
        if (y > max_y) max_y = y;
        if (y < min_y) min_y = y;
    }

    private static void calcFrameDimensions(LinkedList<Character> inList, LinkedList<Character> outList) {

        while (!inList.isEmpty()) {
            char cmd = inList.removeFirst();
            outList.add(cmd); // add it back to list1
            switch (cmd) {
                case '+':
                case '-':
                    facing = getNewDirectionForTurn(cmd);
                    break;
                case 'F':
                    ++drawMovements;
                    moveFacing(facing);
                    break;
            }
        }
        writingToList1 = !writingToList1;

        /*
        System.out.println(drawMovements + " draw movements");
        System.out.println("x, y: " + x + ", " + y);
        System.out.println("max x, max y: " + max_x + ", " + max_y);
        System.out.println("min x, min y: " + min_x + ", " + min_y);
        */
        imageWidth = max_x + (min_x * -1) + 1;
        imageHeight = max_y + (min_y * -1) + 1;
        //System.out.println("imageWidth, imageHeight: " + imageWidth + ", " + imageHeight);
        x = (min_x * -1);
        y = (min_y * -1);
        facing = Direction.UP;
    }

    private static void writeImage(LinkedList<Character> inList, LinkedList<Character> outList) {
        int colorStep = 0;

        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        while (!inList.isEmpty()) {
            Character cmd = inList.removeFirst();
            outList.add(cmd);
            switch (cmd) {
                case '+':
                case '-':
                    facing = getNewDirectionForTurn(cmd);
                    break;
                case 'F':
                    ++colorStep;
                    float color = (float)colorStep / (float)drawMovements;
                    g.setColor(Color.getHSBColor(color, 1.0f, 1.0f));
                    moveFacing(facing);
                    g.drawRect(x, y, 1, 1);
                    break;
            }
        }

        g.dispose();

        File outfile = new File("dragon-fractal.png");
        try {
            ImageIO.write(image, "png", outfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void start(int numIterations) {
        list1.add('F');
        list2.add('X');
        writingToList1 = false;

        // calculate draw sequence
        System.out.print("Iterating... ");
        while (numIterations-- > 0) {
            System.out.print(numIterations + " ");
            if (writingToList1) {
                iterate(list2, list1);
            } else {
                iterate(list1, list2);
            }
            writingToList1 = !writingToList1;
        }
        System.out.println("done.");

        System.out.print("Calculating image bounds... ");
        // Determine image bounds
        if (writingToList1) {
            calcFrameDimensions(list2, list1);
        } else {
            calcFrameDimensions(list1, list2);
        }
        System.out.println("done.");


        System.out.print("Writing png... ");
        if (writingToList1) {
            writeImage(list2, list1);
        } else {
            writeImage(list1, list2);
        }
        System.out.println("all done!");
    }

    private static void iterate(LinkedList<Character> inList, LinkedList<Character> outList) {
        while (!inList.isEmpty()) {
            char cmd = inList.removeFirst();
            switch (cmd) {
                case 'Y':
                    outList.add('-');
                    outList.add('F');
                    outList.add('X');
                    outList.add('-');
                    outList.add('Y');
                    break;
                case 'X':
                    outList.add('X');
                    outList.add('+');
                    outList.add('Y');
                    outList.add('F');
                    outList.add('+');
                    break;
                default:
                    outList.add(cmd);
                    break;
            }
        }
    }
}
