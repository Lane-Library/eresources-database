package edu.stanford.irt.eresources.web;

import java.time.LocalDateTime;

/**
 * indexing job
 */
public class Job {

    private String name;

    private LocalDateTime start;

    public Job(final String name, final LocalDateTime start) {
        this.name = name;
        this.start = start;
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("name: ").append(this.name);
        sb.append("; start: ").append(this.start);
        return sb.toString();
    }
}
