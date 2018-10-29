package edu.stanford.irt.eresources.redivis;

public class TemporalRange {

    private String max;

    private String min;

    private String precision;

    public TemporalRange() {
        // empty constructor
    }

    public String getDisplayRange() {
        StringBuilder sb = new StringBuilder();
        if (null != this.min) {
            sb.append(this.min.substring(0, 4));
        }
        if (null != this.max) {
            sb.append("-");
            sb.append(this.max.substring(0, 4));
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

    /**
     * @param max
     *            the max to set
     */
    public void setMax(final String max) {
        this.max = max;
    }

    /**
     * @param min
     *            the min to set
     */
    public void setMin(final String min) {
        this.min = min;
    }

    /**
     * @param precision
     *            the precision to set
     */
    public void setPrecision(final String precision) {
        this.precision = precision;
    }
}
