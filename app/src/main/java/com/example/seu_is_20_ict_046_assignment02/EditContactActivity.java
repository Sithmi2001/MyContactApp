package com.example.seu_is_20_ict_046_assignment02;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditContactActivity extends AppCompatActivity {

    EditText nameEditText, phoneEditText, emailEditText;
    Button saveButton, cancelButton, selectImageButton;
    ImageView contactImageView;
    DBHelper dbHelper;
    int contactId;
    Uri selectedImageUri = null;
    String currentImageUriString = null;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            String fileName = "contact_" + contactId + "_edited_" + System.currentTimeMillis() + ".jpg";
                            selectedImageUri = copyImageToInternalStorage(uri, fileName);
                            if (selectedImageUri != null) {
                                contactImageView.setImageURI(selectedImageUri);
                            }
                        }
                    });

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        contactImageView = findViewById(R.id.contactImageView);

        dbHelper = new DBHelper(this);

        // Get contactId from intent
        contactId = getIntent().getIntExtra("contactId", -1);

        // Load existing contact data
        if (contactId != -1) {
            Cursor cursor = dbHelper.getContactById(contactId);
            if (cursor.moveToFirst()) {
                nameEditText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME)));
                phoneEditText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PHONE)));
                emailEditText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_EMAIL)));
                currentImageUriString = cursor.getString(cursor.getColumnIndex(DBHelper.COL_IMAGE));
                // Show current image (it will be file:// or null)
                if (currentImageUriString != null && !currentImageUriString.isEmpty()) {
                    contactImageView.setImageURI(Uri.parse(currentImageUriString));
                }
            }
            cursor.close();
        }

        // Save updated contact
        saveButton.setOnClickListener(v -> saveContact());

        // Cancel button returns to previous screen
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(EditContactActivity.this, ContactDetailsActivity.class);
            intent.putExtra("contactId", contactId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        // Select image
        selectImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
    }

    private void saveContact() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        String imageUriString = selectedImageUri != null ? selectedImageUri.toString() : currentImageUriString;


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update contact in database
        boolean success = dbHelper.updateContact(contactId, name, phone, email, imageUriString);
        if (success) {
            Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(EditContactActivity.this, ContactDetailsActivity.class);
            intent.putExtra("contactId", contactId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Create New Function to copy image to apps own space
    private Uri copyImageToInternalStorage(Uri sourceUri, String fileName) {
        if (sourceUri == null) return null;

        try (InputStream inputStream = getContentResolver().openInputStream(sourceUri)) {
            if (inputStream == null) return null;

            // Save to app's private files directory
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();

            // Return file:// URI that will always work
            return Uri.fromFile(new File(getFilesDir(), fileName));

        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}
