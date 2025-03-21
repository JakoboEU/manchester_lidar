package jakobo.geo;

public class Extent {
    private final Double top;
    private final Double bottom;
    private final Double left;
    private final Double right;

    /*
    Where left < right
    And bottom < top
     */
    public Extent(Double top, Double bottom, Double left, Double right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public boolean isXInside(Double x) {
        return left <= x && x <= right;
    }

    public boolean isYInside(Double y) {
        return bottom <= y && y <= top;
    }

    public Double getTop() {
        return top;
    }

    public Double getBottom() {
        return bottom;
    }

    public Double getLeft() {
        return left;
    }

    public Double getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + ", " + top + ") - (" + right + ", " + bottom + ")";
    }
}
