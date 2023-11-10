package models;

import java.util.*;

public class InstagramAccountSearchModel {

    String searchTerm;
    List<String> accountNames;

    public void setSearchTerm(String searchTerm)
    {
        this.searchTerm = searchTerm;
    }

    public void setAccountNames(List<String> accountNames)
    {
        this.accountNames = accountNames;
    }

}
