package edu.calvin.cs262.mon2;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String, Void, String> {
    //Weak References to the TextView objects in MainActivity
    private WeakReference<TextView> mTitleText;
    private WeakReference<TextView> mAuthorText;

    public FetchBook(TextView mTitleText, TextView mAuthorText) {
        this.mTitleText = new WeakReference<>(mTitleText);
        this.mAuthorText = new WeakReference<>(mAuthorText);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            //Convert the response into a JSON object
            JSONObject jsonObject = new JSONObject(s);
            //Get the JSONarray of book items
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            //Initialize iterator and results field
            int i = 0;
            String title = null;
            String authors = null;

            //Look for results in the items array, existing when both the title and author are found
            //or when all items have been checked
            while (i < itemsArray.length() && (authors == null && title == null)){
                //Get the current item information
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                //Try to get the author and title from the current item,
                //Catch if either field is empty and move on
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Move to the next item
                i++;
            }
            //If both are found, display the result
            if (title != null && authors != null) {
                mTitleText.get().setText(title);
                mAuthorText.get().setText(authors);
            }else {
                //If none are found, update UI to show failed results
                mTitleText.get().setText(R.string.no_results);
                mAuthorText.get().setText("");
            }
        } catch (JSONException e) {
            //If onPostExecute does not recieve a proper JSON string,
            //Update the UI to show failed results
            mTitleText.get().setText(R.string.no_results);
            mAuthorText.get().setText("");
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

}
