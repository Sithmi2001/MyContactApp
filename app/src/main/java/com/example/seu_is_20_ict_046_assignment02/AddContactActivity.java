package com.example.seu_is_20_ict_046_assignment02;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddContactActivity extends AppCompatActivity {

    EditText nameEdit, phoneEdit, emailEdit;
    Button saveBtn, cancelButton, selectImageButton;
    ImageView contactImageView;
    DBHelper dbHelper;
    Uri selectedImageUri = null;

    // Use GetContent to pick images (modern API)
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            String fileName = "contact_" + System.currentTimeMillis() + ".jpg";
                            selectedImageUri = copyImageToInternalStorage(uri, fileName);
                            if (selectedImageUri != null) {
                                contactImageView.setImageURI(selectedImageUri);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        nameEdit = findViewById(R.id.nameEditText);
        phoneEdit = findViewById(R.id.phoneEditText);
        emailEdit = findViewById(R.id.emailEditText);
        saveBtn = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        contactImageView = findViewById(R.id.contactImageView);

        dbHelper = new DBHelper(this);

        saveBtn.setOnClickListener(v -> saveContact());

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        selectImageButton.setOnClickListener(v -> {
            // Open image picker (MIME type image/*)
            pickImageLauncher.launch("image/*");
        });
    }

    private void saveContact() {
        String name = nameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String imageUriString = selectedImageUri != null ? selectedImageUri.toString() : null;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.addContact(name, phone, email, imageUriString);
        if (id != -1) {
            Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save contact", Toast.LENGTH_SHORT).show();
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
