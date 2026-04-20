package com.example.seu_is_20_ict_046_assignment02;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener {

    RecyclerView recyclerView;
    ContactAdapter adapter;
    DBHelper dbHelper;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
        searchView = findViewById(R.id.searchView);

        adapter = new ContactAdapter(dbHelper.getAllContacts(), this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.addButton);
        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddContactActivity.class)));

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) btnSettings.setOnClickListener(v -> showThemeDialog());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterContacts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });
    }

    private void filterContacts(String keyword) {
        Cursor cursor;
        if (keyword == null || keyword.isEmpty()) {
            cursor = dbHelper.getAllContacts();
        } else {
            cursor = dbHelper.searchContactsByName(keyword);
        }
        adapter.swapCursor(cursor);
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.getAllContacts());
    }


    @Override
    public void onContactClick(int contactId) {
        Intent intent = new Intent(MainActivity.this, ContactDetailsActivity.class);
        intent.putExtra("contactId", contactId);
        startActivity(intent);
    }


    private void showThemeDialog() {
        final String[] modes = {"Light Mode", "Dark Mode"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Theme");
        builder.setItems(modes, (dialog, which) -> {
            ThemeHelper.setTheme(this, which == 0 ? "light" : "dark");
            recreate();
        });
        builder.show();
    }
}


