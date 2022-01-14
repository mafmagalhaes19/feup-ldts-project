package pt.up.fe.ldts.frogger;

public class MoveLeft extends Command{

    public Position execute(Position position) {
        if (position.getX() == 0)
            return new Position(59, position.getY());
        return new Position(position.getX() - 1, position.getY());
    }
}
