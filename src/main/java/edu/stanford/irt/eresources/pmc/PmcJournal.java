package edu.stanford.irt.eresources.pmc;

import edu.stanford.lane.catalog.TextHelper;

public class PmcJournal {

    private String agreementStatus;

    private String earliest;

    private String eIssn;

    private String embargo;

    private String journalUrl;

    private String lastIssue;

    private String nlmId;

    private String participation;

    private String pIssn;

    private String publisher;

    private String title;

    private String titleTA;

    /**
     * @return the agreementStatus
     */
    public String getAgreementStatus() {
        return this.agreementStatus;
    }

    /**
     * @return the earliest
     */
    public String getEarliest() {
        return this.earliest;
    }

    /**
     * @return the eIssn
     */
    public String geteIssn() {
        return TextHelper.cleanIsxn(this.eIssn);
    }

    /**
     * @return the Release Delay (Embargo)
     */
    public String getEmbargo() {
        if (this.embargo.contains("month") && !this.embargo.contains("0 months")) {
            return "Full text delayed " + this.embargo;
        }
        return null;
    }

    /**
     * @return the journalUrl
     */
    public String getJournalUrl() {
        return this.journalUrl;
    }

    /**
     * @return the lastIssue
     */
    public String getLastIssue() {
        return this.lastIssue;
    }

    /**
     * @return the nlmId
     */
    public String getNlmId() {
        return this.nlmId;
    }

    /**
     * @return the participation or agreement to deposit
     */
    public String getParticipation() {
        return this.participation;
    }

    /**
     * @return the pIssn
     */
    public String getpIssn() {
        return TextHelper.cleanIsxn(this.pIssn);
    }

    /**
     * @return the publisher
     */
    public String getPublisher() {
        return this.publisher;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return the titleTA
     */
    public String getTitleTA() {
        return this.titleTA;
    }

    /**
     * @param depositStatus
     *            the depositStatus to set
     */
    public void setAgreementStatus(final String depositStatus) {
        this.agreementStatus = depositStatus.trim();
    }

    /**
     * @param earliestVolume
     *            the earliestVolume to set
     */
    public void setEarliest(final String earliestVolume) {
        this.earliest = earliestVolume.trim();
    }

    /**
     * @param eIssn
     *            the eIssn to set
     */
    public void seteIssn(final String eIssn) {
        this.eIssn = eIssn.trim();
    }

    /**
     * @param embargo
     *            the embargo to set
     */
    public void setEmbargo(final String embargo) {
        this.embargo = embargo.trim();
    }

    /**
     * @param journalUrl
     *            the journalUrl to set
     */
    public void setJournalUrl(final String journalUrl) {
        this.journalUrl = journalUrl.trim();
    }

    /**
     * @param lastIssue
     *            the lastIssue to set
     */
    public void setLastIssue(final String lastIssue) {
        this.lastIssue = lastIssue.trim();
    }

    /**
     * @param nlmId
     *            the nlmId to set
     */
    public void setNlmId(final String nlmId) {
        this.nlmId = nlmId.trim();
    }

    /**
     * @param participation
     *            the participation or agreement to deposit to set
     */
    public void setParticipation(final String participation) {
        this.participation = participation.trim();
    }

    /**
     * @param pIssn
     *            the pIssn to set
     */
    public void setpIssn(final String pIssn) {
        this.pIssn = pIssn.trim();
    }

    /**
     * @param publisher
     *            the publisher to set
     */
    public void setPublisher(final String publisher) {
        this.publisher = publisher.trim();
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(final String title) {
        this.title = title.trim();
    }

    /**
     * @param titleTA
     *            the titleTA to set
     */
    public void setTitleTA(final String titleTA) {
        this.titleTA = titleTA.trim();
    }
}
