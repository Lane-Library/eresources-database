package edu.stanford.irt.eresources.redivis;

public class TemporalRange {

    private static final int YEAR_LENGTH = 4;

    private String max;

    private String min;

    private String precision;

    public TemporalRange() {
        // empty constructor
    }

    public String getDisplayRange() {
        StringBuilder sb = new StringBuilder();
        if (null != this.min) {
            sb.append(this.min.substring(0, YEAR_LENGTH));
        }
        if (null != this.max) {
            sb.append("-");
            sb.append(this.max.substring(0, YEAR_LENGTH));
        }
        return sb.toString();
    }

    /**
     * @return the max
     */
    public String getMax() {
        return this.max;
    }

    /**
     * @return the min
     */
    public String getMin() {
        return this.min;
    }

    /**
     * @return the precision
     */
    public String getPrecision() {
        return this.precision;
    }
}
