package com.example.moststarredrepositories;

/**
 * POJO for storing Repos information
 * @author rachit
 */
public class Repository {

    /**
     * The Name of the Repo
     */
    private String name;

    /**
     * ContributorsActivity URL
     */
    private String contributorsUrl;

    public Repository(String name, String contributorsUrl) {
        this.name = name;
        this.contributorsUrl = contributorsUrl;
    }

    /* Getters */

    public String getName() {
        return name;
    }

    public String getContributorsUrl() {
        return contributorsUrl;
    }
}
