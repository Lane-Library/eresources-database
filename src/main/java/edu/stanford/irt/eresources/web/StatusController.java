package edu.stanford.irt.eresources.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.stanford.irt.status.ApplicationStatus;
import edu.stanford.irt.status.StatusService;

@Controller
public class StatusController {

    private StatusService statusService;

    public StatusController(final StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping(value = { "/status.json" }, produces = "application/json; charset=utf-8")
    @ResponseBody
    public ApplicationStatus getStatusJson() {
        return this.statusService.getStatus();
    }

    @GetMapping(value = { "/status.txt" }, produces = "text/plain; charset=utf-8")
    @ResponseBody
    public String getStatusTxt() {
        return this.statusService.getStatus().toString();
    }
}
