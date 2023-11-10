package InstagramScraperBot.bot;

import com.google.gson.GsonBuilder;
import models.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class InstagramScraperBot {
    // model variables to store the scraped data
    InstagramScrapeModel userData;

    InstagramProfileModel userProfile;
    List<InstagramPostModel> userPosts;
    List<InstagramFeedModel> userFeeds;
    InstagramAccountSearchModel userSearch;

    // constructor to initialize the variables.
    InstagramScraperBot() {
        userData = new InstagramScrapeModel();
        userProfile = new InstagramProfileModel();
        userPosts = new ArrayList<>();
        userFeeds = new ArrayList<>();
        userSearch = new InstagramAccountSearchModel();

        userData.setUserProfileData(userProfile);
        userData.setUserPostData(userPosts);
        userData.setUserFeedData(userFeeds);
        userData.setUserSearchData(userSearch);


    }

    // this function is used to pause the execution for a specified duration in seconds.
    private void waitForSeconds(int seconds) {

        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            System.out.print("Error: " + e);
        }
    }

    // this function is used to check if an element identified by the given selector is present on the web page.
    private static boolean isElementPresent(WebDriver driver, By selector) {
        return !driver.findElements(selector).isEmpty();
    }

    // this function is used to load and return a WebElement identified by the given XPath expression.
    private WebElement loadElementByXpath(WebDriver driver, String xpath) {
        try{
            // Create a By object using the provided XPath expression
            By selector = By.xpath(xpath);

            // Set up a WebDriverWait with a timeout of 10 seconds
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait until the element is present in the DOM
            wait.until(ExpectedConditions.presenceOfElementLocated(selector));

            WebElement element = null;

            // Check if the element is present before attempting to find it and then return the element
            if (isElementPresent(driver, selector)) {
                element = driver.findElement(selector);
            } else {
                System.out.println("Element Not Found");
            }
            return element;
        } catch (NoSuchElementException e) {
            System.out.println("Exception: " + e);
        }
        return null;
    }

    // Initiates the login process on Instagram by entering the provided email and password
    private void initLogin(WebDriver driver, String email, String password) {

        // Locate the username input field and password input field
        WebElement usernameInput = loadElementByXpath(driver,"//input[@name='username']");
        WebElement passwordInput = loadElementByXpath(driver,"//input[@name='password']");

        waitForSeconds(1);

        // Enter the email and password into the respective input fields
        assert usernameInput != null;
        usernameInput.sendKeys(email);

        waitForSeconds(1);

        assert passwordInput != null;
        passwordInput.sendKeys(password);

        waitForSeconds(1);

        // get the login button and click it
        WebElement submitButton = loadElementByXpath(driver,"//button[@type='submit']");
        assert submitButton != null;
        submitButton.click();


        // Handle "Not now" dialog for saving password if present after login
        try {
            WebElement notNowButton = loadElementByXpath(driver, "//div[contains(text(), 'Not now')]");
            assert notNowButton != null;
            String notNowText = notNowButton.getText();
            if (notNowText.compareTo("Not now") == 0) {
                notNowButton.click();
            }
        } catch (NoSuchElementException e) {
            System.out.print("No Element Found: " + e);
        }

        waitForSeconds(2);
        // Handle "Turn on notifications" dialogs if present after login
        try {
            WebElement notNowNotificationButton = loadElementByXpath(driver, "//button[contains(text(), 'Not Now')]");
            assert notNowNotificationButton != null;
            String notNowNotificationText = notNowNotificationButton.getText();
            if (notNowNotificationText.compareTo("Not Now") == 0) {
                notNowNotificationButton.click();
            }

            // Handle repeated "Turn on notifications" dialogs, if any or if it's not removed. Basically I this
            // I am checking if the dialog is still open and keep on calling click if it exists.
            WebElement notNowNotificationDialog;
            try{
                notNowNotificationDialog = loadElementByXpath(driver, "//span[contains(text(), 'Turn on notifications')]");
            } catch (Exception e) {
                notNowNotificationDialog = null;
            }
            while (notNowNotificationDialog != null) {
                notNowNotificationButton.click();
                waitForSeconds(3);
                try{
                    notNowNotificationDialog = loadElementByXpath(driver, "//span[contains(text(), 'Turn on notifications')]");
                } catch (Exception e) {
                    notNowNotificationDialog = null;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.print("No Element Found: " + e);
        }
    }

    // this method is used to retrieve and populates the Instagram
    // user profile data by navigating to the user's profile page.
    public void getProfileData(WebDriver driver) {

        try {
            // Locate the "Profile" link in the navigation
            WebElement profileElement = loadElementByXpath(driver, "//span[contains(text(), 'Profile')]");
            assert profileElement != null;

            // Find the ancestor link of the "Profile" element
            // and click on the ancestor link to navigate to the user's profile page
            WebElement ancestorProfileLink = profileElement.findElement(By.xpath("ancestor::a"));
            assert ancestorProfileLink != null;
            ancestorProfileLink.click();

            // Retrieve and set the user's profile image URL
            WebElement profilePhotoElement = loadElementByXpath(driver, "//img[@alt='Change profile photo']");
            assert profilePhotoElement != null;
            userProfile.setProfileImageUrl(profilePhotoElement.getAttribute("src"));

            // Retrieve and set the user's username
            WebElement userNameElement = loadElementByXpath(driver, "//section/div/a/h2");
            assert userNameElement != null;
            userProfile.setUsername(userNameElement.getText());

            // Retrieve and set the user's number of posts
            WebElement postsCountElement = loadElementByXpath(driver, "//li[contains(text(), 'posts')]");
            assert postsCountElement != null;
            WebElement postCountSpan = postsCountElement.findElement(By.xpath(".//span/span"));
            assert postCountSpan != null;
            userProfile.setPostsCount(Integer.parseInt(postCountSpan.getText()));

            // Retrieve and set the user's number of followers
            WebElement followersCountElement = loadElementByXpath(driver, "//li/a[contains(text(), 'followers')]");
            assert followersCountElement != null;
            WebElement followersCountSpan = followersCountElement.findElement(By.xpath(".//span/span"));
            assert followersCountSpan != null;
            userProfile.setFollowersCount(Integer.parseInt(followersCountSpan.getText()));

            // Retrieve and set the user's number of following
            WebElement followingCountElement = loadElementByXpath(driver, "//li/a[contains(text(), 'following')]");
            assert followingCountElement != null;
            WebElement followingCountSpan = followingCountElement.findElement(By.xpath(".//span/span"));
            assert followingCountSpan != null;
            userProfile.setFollowingCount(Integer.parseInt(followingCountSpan.getText()));

            // Retrieve and set the user's bio
            WebElement bioElement = loadElementByXpath(driver, "//section/div/h1");
            assert bioElement != null;
            userProfile.setBio(bioElement.getText());

        } catch (NoSuchElementException e) {
            System.out.print("No Element Found: " + e);
        }

    }

    // this function is used to retrieve data from the top N user
    // posts on Instagram by clicking through the post carousel.
    private void getTopNUserPosts(WebDriver driver, int numberOfPosts) {
        // Locate the container element of user posts
        WebElement postsElement = loadElementByXpath(driver, "//article/div/div");
        assert postsElement != null;
        // Find all post section elements within the container
        List<WebElement> postSectionElements = postsElement.findElements(By.xpath(".//div"));
        assert postSectionElements != null;

        int parsedPosts = 0;
        int sectionPointer = 0;

        // Get the current section element
        WebElement currentSectionElement = postSectionElements.get(sectionPointer);

        // Find all post elements within the current section
        // and click on the first post to start the iteration
        List<WebElement> postElements = currentSectionElement.findElements(By.xpath(".//div"));
        WebElement firstPostElement = postElements.get(0);
        firstPostElement.click();

        // Retrieve data from the first post
        getUserPostData(driver);

        parsedPosts++;
        waitForSeconds(5);

        // Iterate through the next posts until the desired number is reached
        while (parsedPosts<numberOfPosts) {
            WebElement nextElement = null;

            try{
                // create the selector for the "Next" button in the post carousel
                By nextElementSelector = By.xpath("//*[local-name()='svg' and @aria-label='Next']");

                // Set up a WebDriverWait to wait for the "Next" button to be clickable and present
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.and(ExpectedConditions.elementToBeClickable(nextElementSelector), ExpectedConditions.presenceOfElementLocated(nextElementSelector)));

                // Check if the "Next" button is present before attempting to find it
                if (isElementPresent(driver, nextElementSelector)) {
                    nextElement = driver.findElement(nextElementSelector);
                } else {
                    System.out.println("Element Not Found");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Exception: " + e);
            }
            assert nextElement != null;
            waitForSeconds(2);

            nextElement.click();

            waitForSeconds(5);

            // Retrieve data from the current post
            getUserPostData(driver);
            parsedPosts++;

        }

        // Close the post carousel
        WebElement closeElement = loadElementByXpath(driver, "//*[local-name()='svg' and @aria-label='Close']");
        assert closeElement != null;
        closeElement.click();

        // Navigate back to the home page
        WebElement profileElement = loadElementByXpath(driver, "//span[contains(text(), 'Home')]");
        assert profileElement != null;
        WebElement ancestorProfileLink = profileElement.findElement(By.xpath("ancestor::a"));
        assert ancestorProfileLink != null;
        ancestorProfileLink.click();

    }

    //this function is used to retrieve data from the top N Instagram
    // feeds by scrolling through the user's feed.
    public void getTopNFeeds(WebDriver driver, int numberOfFeeds) {
        List<WebElement> feeds;

        int feedsCounter = 0;

        // Iterate until the desired number of feeds is reached
        while (feedsCounter < numberOfFeeds) {
            waitForSeconds(6);
            InstagramFeedModel newFeed = new InstagramFeedModel();

            // Find all feed elements on the current page
            feeds = driver.findElements(By.xpath("//div[@class='x9f619 xjbqb8w x78zum5 x168nmei x13lgxp2 x5pf9jr xo71vjh x1uhb9sk x1plvlek xryxfnj x1c4vz4f x2lah0s xdt5ytf xqjyukv x1qjc9v5 x1oa3qoh x1nhvcw1']/div/div/article"));

            // Determine the size of the feeds list, as the list is dynamically loading
            int feedsSize = feeds.size()-1;

            try {
                // Retrieve data from the current feed like
                // postedBy, mediaUrl, viewsOrLikesCount
                WebElement postedByElement = feeds.get(Math.min(feedsCounter, feedsSize)).findElement(By.xpath(".//span[@class='_ap3a _aaco _aacw _aacx _aad7 _aade']"));
                assert postedByElement != null;
                newFeed.setPostedBy(postedByElement.getText());

                WebElement mediaElement = feeds.get(Math.min(feedsCounter, feedsSize)).findElement(By.xpath(".//img[@class='x5yr21d xu96u03 x10l6tqk x13vifvy x87ps6o xh8yej3']"));
                assert mediaElement != null;
                newFeed.setMediaUrl(mediaElement.getAttribute("src"));

                WebElement likesElement = feeds.get(Math.min(feedsCounter, feedsSize)).findElement(By.xpath(".//section[@class='xat24cr']/div/div/span/a/span/span"));
                assert likesElement != null;
                newFeed.setViewsOrLikesCount(likesElement.getText());

                // Add the new feed to the userFeeds list
                userFeeds.add(newFeed);
            } catch (Exception e) {
                JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                jsExecutor.executeScript("window.scrollBy(0, 600);");
                continue;
            }

            // Scroll down to load more feeds
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("window.scrollBy(0, 600);");

            // Move to the next feed
            feedsCounter++;

        }

    }

    // Retrieves data from an Instagram post, including the media type (image or video), media URL, views or likes count,
    // and the date the post was posted. Adds the post data to the userPosts list.
    private void getUserPostData(WebDriver driver) {
        // Flag to determine if the post contains a video
        boolean isVideo = true;
        InstagramPostModel post = new InstagramPostModel();

        try {
            // Locate and retrieve data from the video element of the post
            WebElement videoElement = loadElementByXpath(driver, "//video[@class='x1lliihq x5yr21d xh8yej3']");
            assert  videoElement != null;
            post.setMediaUrl(videoElement.getAttribute("src"));

            WebElement viewsElement = loadElementByXpath(driver, "//span[contains(text(), ' views')]/span[@class='html-span xdj266r x11i5rnm xat24cr x1mh8g0r xexx8yu x4uap5 x18d9i69 xkhd6sd x1hl2dhg x16tdsg8 x1vvkbs']");
            assert  viewsElement != null;

            post.setViewsOrLikesCount(viewsElement.getText());


        } catch (TimeoutException e) {
            // If a TimeoutException occurs, set the isVideo flag to false
            isVideo = false;
        }

        // If the post is not a video, retrieve data from the image element
        if(!isVideo) {
            WebElement imageElement = loadElementByXpath(driver, "//img[@class='x5yr21d xu96u03 x10l6tqk x13vifvy x87ps6o xh8yej3']");
            assert  imageElement != null;
            post.setMediaUrl(imageElement.getAttribute("src"));

            WebElement likesElement = loadElementByXpath(driver, "//span[contains(text(), 'Liked by ')]/a/span/span[@class='html-span xdj266r x11i5rnm xat24cr x1mh8g0r xexx8yu x4uap5 x18d9i69 xkhd6sd x1hl2dhg x16tdsg8 x1vvkbs']");
            assert likesElement != null;
            post.setViewsOrLikesCount(likesElement.getText());
        }

        // Retrieve the date the post was posted
        WebElement datePostedElement = loadElementByXpath(driver, "//time[@class='_aaqe']");
        assert datePostedElement != null;
        post.setDateOfPost(datePostedElement.getAttribute("datetime"));

        // Add the post data to the userPosts list
        userPosts.add(post);
    }

    // This function searches for Instagram accounts based on a provided keyword, retrieves a specified number of top results,
    // and captures relevant data such as account names. The results are stored in the userSearch object.
    public void getTopNAccountNamesByKeyword(WebDriver driver, int numberOfAccount, String searchTerm) {
        // Locate and click on the Search page element
        WebElement searchPageElement = loadElementByXpath(driver, "//span[contains(text(), 'Search')]");
        assert searchPageElement != null;

        WebElement ancestorSearchLink = searchPageElement.findElement(By.xpath("ancestor::a"));
        assert ancestorSearchLink != null;
        ancestorSearchLink.click();

        waitForSeconds(3);

        // Locate and input the search term in the search input element
        WebElement searchInputElement = loadElementByXpath(driver, "//input[@aria-label='Search input']");
        assert searchInputElement != null;
        searchInputElement.sendKeys(searchTerm);

        waitForSeconds(3);

        // Retrieve search result elements and limit to the specified number of accounts
        List<WebElement> searchResultElement = driver.findElements(By.xpath("//div[@class='x9f619 x78zum5 xdt5ytf x1iyjqo2 x6ikm8r x1odjw0f xh8yej3 xocp1fn']/a"));
        List<WebElement> topNSearchElements = searchResultElement.subList(0, Math.min(numberOfAccount, searchResultElement.size()));

        // Set the search term in the userSearch object
        userSearch.setSearchTerm(searchTerm);

        // Retrieve and store account names in the userSearch object
        List<String> accounts = new ArrayList<>();
        for(WebElement searchElement: topNSearchElements) {
            accounts.add(searchElement.getAttribute("href"));
        }

        userSearch.setAccountNames(accounts);

        // Navigate back to the home page
        WebElement homeElement = loadElementByXpath(driver, "//*[local-name()='svg' and @aria-label='Home']");
        assert homeElement != null;
        homeElement.click();

    }

    // Initiates the logout process on Instagram by accessing the settings, clicking on the logout option,
    // and logging the user out of the current session.
    public void initLogout(WebDriver driver) {
        // Locate and click on the Settings element
        WebElement settingsElement = loadElementByXpath(driver, "//*[local-name()='svg' and @aria-label='Settings']");
        assert settingsElement != null;
        settingsElement.click();

        waitForSeconds(5);

        // Locate the logout span element and navigate to its parent (logout option)
        WebElement logoutSpanElement = loadElementByXpath(driver, "//span[contains(text(), 'Log out')]");
        assert logoutSpanElement != null;

        // Navigate up the DOM hierarchy to find the parent element representing the logout option
        WebElement logOutElement = logoutSpanElement.findElement(By.xpath("./../../../../../../../.."));
        assert logOutElement != null;

        // Click on the logout option to initiate the logout process
        logOutElement.click();
    }

    // Converts the Instagram user data object (userData) into a JSON format using Gson,
    // and saves the formatted JSON data to a file named "instagramUserData.json" in the source directory.
    public void saveJsonData() {
        // Create a Gson instance with pretty printing enabled
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Convert the userData object to a JSON string
        String json = gson.toJson(userData);

        // Define the path for the JSON file in the source directory
        String path = "src/instagramUserData.json";

        try (FileWriter writer = new FileWriter(path)) {
            // Attempt to write the JSON string to the specified file
            try (PrintWriter printWriter = new PrintWriter(writer)) {
                printWriter.println(json);
            }
        } catch (IOException e) {
            System.out.println("Error While Saving.");
        }

    }

    // this is the main driver code for the scraper bot.
    // it opens a Chrome browser, executes different functions to extract data
    // and then store the data as json and closes the browser.
    public static void main(String[] args) {
        System.out.println("**************** Instagram Scraper Bot *******************");
        WebDriver driver = new ChromeDriver();

        InstagramScraperBot scraperBot = new InstagramScraperBot();
        try {
            Scanner userInput = new Scanner(System.in);
            System.out.print("Please Enter your Instagram Email: ");
            String email = userInput.nextLine();

            System.out.print("Please Enter your Instagram Password: ");
            String password = userInput.nextLine();
            
            userInput.close();

            System.out.println("Initialize Selenium Driver");
            System.out.println();
            driver.get("https://www.instagram.com/");

            System.out.println("Scraping Started");
            System.out.println("    Logging in to Instagram");

            scraperBot.initLogin(driver, email, password);

            System.out.println("    Getting Profile Data");
            scraperBot.getProfileData(driver);

            System.out.println("    Getting Top N User Posts");
            scraperBot.getTopNUserPosts(driver, 10);

            System.out.println("    Getting Top N User Feeds");
            scraperBot.getTopNFeeds(driver, 7);

            System.out.println("    Getting Top N Account Results");
            scraperBot.getTopNAccountNamesByKeyword(driver, 10, "computer");

            System.out.println("    Logging out of Instagram");
            scraperBot.initLogout(driver);

            System.out.println("    Saving Data to Json File");
            scraperBot.saveJsonData();

            System.out.println("Scraping Completed");

        } catch (NoSuchElementException e) {
            System.out.print("No Element Found: " + e);
        }
        finally {
            driver.quit();
        }
    }
}