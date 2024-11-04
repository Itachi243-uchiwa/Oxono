package g63888.ascii.model;

import g63888.ascii.Util.Command;

public class AddCommand implements Command {

    private final Drawing drawing;
    private final Shape shape;

    public AddCommand(Drawing drawing, Shape shape) {
        this.drawing = drawing;
        this.shape = shape;
    }


    @Override
    public void execute() {
       drawing.addShape(shape);
    }

    @Override
    public void unexecute() {
      drawing.removeShape(shape);
    }
}
