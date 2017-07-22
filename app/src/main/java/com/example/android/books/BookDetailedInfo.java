package com.example.android.books;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class BookDetailedInfo extends AppCompatActivity {

    /** TextViews that display book information */
    TextView title, author, publisher, description, ratingsCount;

    /** Image of the book */
    ImageView image;

    /** RatingBar that show the average rating of book book */
    RatingBar averageRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);


        // Find the layout elements
        image = (ImageView)findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title_text_view);
        author = (TextView) findViewById(R.id.author_text_view);
        publisher = (TextView) findViewById(R.id.publisher_and_date_text_view);
        description = (TextView) findViewById(R.id.description_text_view);
        averageRating = (RatingBar) findViewById(R.id.rating_bar);
        ratingsCount = (TextView) findViewById(R.id.rating_count);

        // get the Book that was clicked to get the information
        Book clickedBook = (Book) getIntent().getSerializableExtra("CLICKED_ITEM");

        // Set the title of the book to action bar
        getSupportActionBar().setTitle(clickedBook.getTitle());

        // Load and display the image with tha Picasso library
        Picasso.with(BookDetailedInfo.this)
                .load(clickedBook.getImageLink())
                .resize(170, 250)
                .into(image);

        // Get the information form the {@link Book} object,
        // and display them.
        title.setText(getString(R.string.title).toUpperCase() + " " + clickedBook.getTitle());
        author.setText(getString(R.string.author).toUpperCase() + " " + clickedBook.getAuthor());
        publisher.setText(getString(R.string.publisher).toUpperCase() + " " + clickedBook.getPublisherAndDate());
        description.setText(getString(R.string.description).toUpperCase() + "\n" + clickedBook.getDescription());
        averageRating.setRating(clickedBook.getAverageRating());
        ratingsCount.setText("(" + clickedBook.getRatingsCount() + ")");
    }
}
