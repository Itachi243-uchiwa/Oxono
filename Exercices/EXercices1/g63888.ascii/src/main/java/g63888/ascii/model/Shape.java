package g63888.ascii.model;


public interface Shape {

    void move(double dx, double dy);


    boolean isInside(Point point);


    char getColor();


    void setColor(char color);
}

