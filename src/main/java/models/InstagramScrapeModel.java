package models;

import java.util.List;

public class InstagramScrapeModel {
    private InstagramProfileModel userProfileData;
    private List<InstagramPostModel> userPostData;
    private List<InstagramFeedModel> userFeedData;
    private InstagramAccountSearchModel userSearchData;

    public void setUserProfileData(InstagramProfileModel userProfileData) {
        this.userProfileData = userProfileData;
    }


    public void setUserPostData(List<InstagramPostModel> userPostData) {
        this.userPostData = userPostData;
    }


    public void setUserFeedData(List<InstagramFeedModel> userFeedData) {
        this.userFeedData = userFeedData;
    }


    public void setUserSearchData(InstagramAccountSearchModel userSearchData) {
        this.userSearchData = userSearchData;
    }

}
