package com.example.android.books;

import android.text.TextUtils;
import android.util.Log;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving book data from Google Book.
 */

public class QueryUtils {

    /** Tag for log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google book dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJson) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject jsonObject = new JSONObject(bookJson);

            // Extract the JSONArray associated with the key called "items".
            JSONArray items = jsonObject.getJSONArray("items");

            // For each book in the bookArray, create an {@link Book} object
            for (int i = 0; i < items.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = items.getJSONObject(i);

                // Extract the JSON object called "volumeInfo"
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                // Extract the JSON object called "imageLinks"
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                // Extract the value for the key called "smallThumbnail"
                String imageLink = imageLinks.getString("smallThumbnail");
                // Extract the value for the key called "title"
                String title = volumeInfo.getString("title");

                // Extract the JSONArray associated with the key called "authors".
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                // Get the first author in the authors array
                String author = authorsArray.getString(0);

                // Extract the value for the key called "publisher"
                String publisherAndDate = volumeInfo.getString("publisher");

                // Check if the book has a published date or not
                if (volumeInfo.has("publishedDate")) {

                    // Extract the value for the key called "publishedDate"
                    String date = volumeInfo.getString("publishedDate");
                    // Add the published data to the publisher
                    publisherAndDate = publisherAndDate + ", " + date;
                }

                String description = "No description available";
                // Check if the the book has a description or not
                if (volumeInfo.has("description")) {
                    // Extract the value for the key called "publishedDate"
                    description = volumeInfo.getString("description");
                }

                float averageRating = 0;
                String ratingsCount = "0";
                // Check if the book has average rating or not
                if (volumeInfo.has("averageRating")) {
                    // Extract the value for the key called "averageRating"
                    averageRating = volumeInfo.getInt("averageRating");

                    // Extract the value for the key called "ratingsCount"
                    ratingsCount = volumeInfo.getString("ratingsCount");
                }

                // Create a new {@link Book} object with the title, author, publisher,
                // and publishedDate from the JSON response.
                Book book = new Book(imageLink, title, author, publisherAndDate, description, averageRating, ratingsCount);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }
        // Return the list of books
        return books;
    }

}
