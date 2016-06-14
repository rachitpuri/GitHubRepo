package com.example.moststarredrepositories;

/**
 * Constants defined in the whole app
 * @author rachit
 */
public class Constants {

    // RecyclerView Span count
    public static final int SPAN_COUNT_PORTRAIT = 1;

    // Search Repo URLs and Params
    public static final String SEARCH_REPOS_URL = "https://api.github.com/search/repositories";
    public static final String SEARCH_REPOS_QUERY_PARAM = "q";
    public static final String SEARCH_REPOS_SORT_PARAM = "sort";
    public static final String SEARCH_REPOS_SORT_PARAM_VALUE = "stars";
    public static final String SEARCH_REPOS_ORDER_PARAM = "order";
    public static final String SEARCH_REPOS_ORDER_PARAM_VALUE = "desc";

    // Error Messages
    public static final String INTERNET_UNAVAILABLE = "NO INTERNET CONNECTION";
    public static final String ERROR = "Couldn't fetch the data, please try again";
}
