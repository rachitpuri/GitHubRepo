package com.example.moststarredrepositories;

/** POJO for storing contributor information
 * @author rachit
 */
public class Contributor {

    private String name;
    private String repoUrl;
    private String imageUrl;

    public Contributor(String name, String repoUrl, String imageUrl) {
        this.name = name;
        this.repoUrl = repoUrl;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
