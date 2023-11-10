package models;

public class InstagramProfileModel {
    String username;
    String bio;
    String profileImageUrl;
    int followersCount;
    int postsCount;
    int followingCount;


    public void setUsername(String username) {
        this.username = username;
    }


    public void setBio(String bio) {
        this.bio = bio;
    }


    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }


    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }


    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}
