package work.chiro.egui;

public class Pos2 {
    public float x, y;

    public Pos2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}
