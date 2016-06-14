package com.example.moststarredrepositories;


import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.moststarredrepositories.network.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author rachit
 */
public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private Activity mActivity;
    protected RecyclerView mRecyclerView;
    protected RecyclerViewAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = getActivity();

        if (NetworkManager.isNetworkAvailable(mActivity)) {
            new FetchMostStarredTask().execute();
        } else {
            NetworkManager.displayNetworkUnavailableToast(mActivity);
        }
    }

    /**
     * Initialize the views in this Fragment
     *
     * @param rootView Fragment View
     */
    private void initViews(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_main_recycler_view);
        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getActivity(), Constants.SPAN_COUNT_PORTRAIT);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Task to fetch the most starred repositories
     */
    private class FetchMostStarredTask extends AsyncTask<Void, Void, Repository[]> {
        @Override
        protected Repository[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Uri builtUri = buildUri();
            Log.i(LOG_TAG, "Built URI is: " + builtUri.toString());

            // Will contain the raw JSON response as a string
            String resultJSON = null;

            try {
                URL url = new URL(builtUri.toString());

                // Create request to GitHub API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                resultJSON = buffer.toString();
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Search URL could not be built", e);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to open URL connection", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                try {
                    if (resultJSON != null) {
                        return getSingleItemFromJson(resultJSON);
                    } else {
                        return null;
                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(Repository[] strings) {
            if (strings != null) {
                // Set RecyclerViewAdapter as the adapter for RecyclerView.
                mAdapter = new RecyclerViewAdapter(strings, mActivity);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                Toast.makeText(getActivity(), Constants.ERROR, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Take the String representing the complete result in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private Repository[] getSingleItemFromJson(String jsonStr) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String ARRAY = "items";
            final String REPO_NAME = "name";
            final String CONTRIBUTOR_URL = "contributors_url";

            JSONObject searchJson = new JSONObject(jsonStr);
            JSONArray resultArray = searchJson.getJSONArray(ARRAY);

            int totalResults = resultArray.length();
            Repository[] result = new Repository[totalResults];

            for (int i = 0; i < totalResults; i++) {
                // Get the JSON object representing the item
                JSONObject item = resultArray.getJSONObject(i);
                String name = item.getString(REPO_NAME);
                String url = item.getString(CONTRIBUTOR_URL);
                result[i] = new Repository(name, url);
            }
            return result;
        }

        /**
         * Build a URI for most starred repos
         *
         * @return Uri
         */
        private Uri buildUri() {
            String queryParamValue = "created:>" + getCalculatedDate("yyyy-MM-dd", -8);
            return Uri.parse(Constants.SEARCH_REPOS_URL).buildUpon()
                    .appendQueryParameter(Constants.SEARCH_REPOS_QUERY_PARAM, queryParamValue)
                    .appendQueryParameter(Constants.SEARCH_REPOS_SORT_PARAM,
                            Constants.SEARCH_REPOS_SORT_PARAM_VALUE)
                    .appendQueryParameter(Constants.SEARCH_REPOS_ORDER_PARAM,
                            Constants.SEARCH_REPOS_ORDER_PARAM_VALUE)
                    .build();
        }

        /**
         * Pass your date format and no of days for minus from current
         * If you want to get previous date then pass days with minus sign
         * else you can pass as it is for next date
         * @param dateFormat
         * @param days
         * @return Calculated Date
         */
        private String getCalculatedDate(String dateFormat, int days) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat s = new SimpleDateFormat(dateFormat);
            cal.add(Calendar.DAY_OF_YEAR, days);
            return s.format(new Date(cal.getTimeInMillis()));
        }
    }
}
