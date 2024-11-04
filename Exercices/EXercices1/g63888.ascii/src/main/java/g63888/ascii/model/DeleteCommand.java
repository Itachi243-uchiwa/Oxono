package g63888.ascii.model;

import g63888.ascii.Util.Command;

public class DeleteCommand implements Command {

    private Drawing drawing;
    private Shape shape;
    private int index;


    public DeleteCommand(Drawing drawing, int index) {
        this.drawing = drawing;
        this.index = index;

    }



    @Override
    public void execute() {
       shape = drawing.getShapeAt(index);
       if (shape != null ){
           drawing.removeShape(shape);
       }
    }

    @Override
    public void unexecute() {
       drawing.addShape(shape);
    }
}
