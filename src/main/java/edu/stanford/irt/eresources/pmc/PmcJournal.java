package edu.stanford.irt.eresources.pmc;

import edu.stanford.irt.eresources.TextParserHelper;

public class PmcJournal {

    private String depositStatus;

    private String earliestVolume;

    private String eIssn;

    private String freeAccess;

    private String journalUrl;

    private String lastIssue;

    private String nlmId;

    private String openAcess;

    private String participation;

    private String pIssn;

    private String publisher;

    private String title;

    private String titleTA;

    /**
     * @return the depositStatus
     */
    public String getDepositStatus() {
        return this.depositStatus;
    }

    /**
     * @return the earliestVolume
     */
    public String getEarliestVolume() {
        return this.earliestVolume;
    }

    /**
     * @return the eIssn
     */
    public String geteIssn() {
        return TextParserHelper.cleanIsxn(this.eIssn);
    }

    /**
     * @return the freeAccess
     */
    public String getFreeAccess() {
        if (this.freeAccess.contains("months")) {
            return "Full text delayed " + this.freeAccess;
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
     * @return the openAcess
     */
    public String getOpenAcess() {
        return this.openAcess;
    }

    /**
     * @return the participation
     */
    public String getParticipation() {
        return this.participation;
    }

    /**
     * @return the pIssn
     */
    public String getpIssn() {
        return TextParserHelper.cleanIsxn(this.pIssn);
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
    public void setDepositStatus(final String depositStatus) {
        this.depositStatus = depositStatus.trim();
    }

    /**
     * @param earliestVolume
     *            the earliestVolume to set
     */
    public void setEarliestVolume(final String earliestVolume) {
        this.earliestVolume = earliestVolume.trim();
    }

    /**
     * @param eIssn
     *            the eIssn to set
     */
    public void seteIssn(final String eIssn) {
        this.eIssn = eIssn.trim();
    }

    /**
     * @param freeAccess
     *            the freeAccess to set
     */
    public void setFreeAccess(final String freeAccess) {
        this.freeAccess = freeAccess.trim();
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
     * @param openAcess
     *            the openAcess to set
     */
    public void setOpenAcess(final String openAcess) {
        this.openAcess = openAcess.trim();
    }

    /**
     * @param participation
     *            the participation to set
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
