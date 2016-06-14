package com.example.moststarredrepositories.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.moststarredrepositories.Constants;
import com.example.moststarredrepositories.Contributor;
import com.example.moststarredrepositories.ContributorAdapter;
import com.example.moststarredrepositories.R;
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

/**
 * class to display the list of contributors of a particular repository
 * @author rachit
 */
public class ContributorsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ContributorsActivity.class.getSimpleName();

    private Context mContext;
    protected RecyclerView mRecyclerView;
    protected ContributorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributors);
        initViews();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.repo_contributor);
        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(this, Constants.SPAN_COUNT_PORTRAIT);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getApplicationContext();
        if (NetworkManager.isNetworkAvailable(this)) {
            String string = getIntent().getExtras().getString("contributors");
            new FetchContributorList().execute(string);
        } else {
            NetworkManager.displayNetworkUnavailableToast(this);
        }
    }

    /**
     * Task to fetch the most starred repositories
     */
    private class FetchContributorList extends AsyncTask<String, Void, Contributor[]> {
        @Override
        protected Contributor[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string
            String resultJSON = null;

            try {
                URL url = new URL(params[0]);
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
        protected void onPostExecute(Contributor[] strings) {
            if (strings != null) {
                mAdapter = new ContributorAdapter(strings, mContext);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                Toast.makeText(ContributorsActivity.this, Constants.ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Take the String representing the complete result in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */
    private Contributor[] getSingleItemFromJson(String jsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String PERSON_NAME = "login";
        final String PERSON_LINK = "html_url";
        final String PERSON_IMAGE = "avatar_url";
        JSONArray resultArray = new JSONArray(jsonStr);
        int totalResults = resultArray.length();
        Contributor[] result = new Contributor[totalResults];

        for (int i = 0; i < totalResults; i++) {
            // Get the JSON object representing the item
            JSONObject item = resultArray.getJSONObject(i);
            String name = item.getString(PERSON_NAME);
            String url = item.getString(PERSON_LINK);
            String image = item.getString(PERSON_IMAGE);
            result[i] = new Contributor(name, url, image);
        }
        return result;
    }
}
