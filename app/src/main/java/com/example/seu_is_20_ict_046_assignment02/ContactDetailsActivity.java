package com.example.seu_is_20_ict_046_assignment02;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactDetailsActivity extends AppCompatActivity {

    TextView nameTextView, phoneTextView, emailTextView;
    DBHelper dbHelper;
    Button backButton, deleteButton;
    ImageButton callButton, messageButton, emailButton;
    ImageView contactImageView;
    ImageButton editButton;


    int contactId; // selected contact id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        // Initialize views
        nameTextView = findViewById(R.id.nameTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        emailTextView = findViewById(R.id.emailTextView);
        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);


        callButton = findViewById(R.id.callButton);
        messageButton = findViewById(R.id.messageButton);
        emailButton = findViewById(R.id.emailButton);
        contactImageView = findViewById(R.id.contactImageView);

        dbHelper = new DBHelper(this);

        // Get contact ID passed from MainActivity
        contactId = getIntent().getIntExtra("contactId", -1);

        loadContactDetails();

        // BACK → just finish this page
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContactDetailsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });



        // DELETE contact → return to MainActivity
        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteContact(contactId);
            Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ContactDetailsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        //Edit Button
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContactDetailsActivity.this, EditContactActivity.class);
            intent.putExtra("contactId", contactId);
            startActivity(intent);
        });



        // CALL button
        callButton.setOnClickListener(v -> {
            String phone = phoneTextView.getText().toString();
            if (!phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });

        // MESSAGE button
        messageButton.setOnClickListener(v -> {
            String phone = phoneTextView.getText().toString();
            if (!phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:" + phone));
                startActivity(intent);
            }
        });

        // EMAIL button
        emailButton.setOnClickListener(v -> {
            String email = emailTextView.getText().toString();
            if (!email.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContactDetails();   // Refresh after edit
    }

    private void loadContactDetails() {
        if (contactId == -1) return;

        Cursor cursor = dbHelper.getContactById(contactId);
        if (cursor.moveToFirst()) {

            nameTextView.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME)));
            phoneTextView.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PHONE)));
            emailTextView.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_EMAIL)));

            // Load image
            int imgIndex = cursor.getColumnIndex(DBHelper.COL_IMAGE);
            if (imgIndex != -1) {
                String uriString = cursor.getString(imgIndex);

                if (uriString != null && !uriString.isEmpty()) {
                    try {
                        contactImageView.setImageURI(Uri.parse(uriString));
                    } catch (Exception e) {
                        contactImageView.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                } else {
                    contactImageView.setImageResource(android.R.drawable.ic_menu_camera);
                }
            }
        }
        cursor.close();
    }
}
