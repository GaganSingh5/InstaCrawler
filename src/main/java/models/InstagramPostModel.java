package models;

public class InstagramPostModel {
    private String mediaUrl;
    private String dateOfPost;
    private String viewsOrLikesCount;

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setDateOfPost(String dateOfPost) {
        this.dateOfPost = dateOfPost;
    }


    public void setViewsOrLikesCount(String viewsOrLikesCount) {
        this.viewsOrLikesCount = viewsOrLikesCount;
    }

}