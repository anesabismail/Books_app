package com.example.android.books;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    /** Tag for log messages */
    private static final String LOG_TAG = BooksActivity.class.getSimpleName();

    /** URL to query the book information */
    private static final String GOOGLE_BOOK_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int Book_LOADER_ID = 1;

    /** Create a constant from the {@link BookAdapter} */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** Progress bar to show the progress */
    ProgressBar mProgressBar;

    /** A string variable to store the query text from the home page */
    private String queryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        // Get the query text from the intent
        queryText = getIntent().getSerializableExtra("QUERY_TEXT").toString();

        // Set the query text to the ActionBar
        getSupportActionBar().setTitle(queryText);

        // Find a reference to the {@link ProgressBar} in the layout
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Find the empty text view in the layout.
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Get a reference to the ConnectivityManager to check
        // state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader.
            loaderManager.initLoader(Book_LOADER_ID, null, this);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mProgressBar.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the empty view to the list
        bookListView.setEmptyView(mEmptyStateTextView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Set a click listener on list items
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book itemClicked = (Book) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(BooksActivity.this, BookDetailedInfo.class);
                intent.putExtra("CLICKED_ITEM", itemClicked);
                startActivity(intent);

            }
        });

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, GOOGLE_BOOK_REQUEST_URL + queryText);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide the progress bar
        mProgressBar.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // if there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}

