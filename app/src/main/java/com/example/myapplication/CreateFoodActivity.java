package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

public class CreateFoodActivity extends AppCompatActivity {

    private EditText editTextName, editTextPrice;
    private Button buttonCreate, buttonBack;
    private ConnectionHelper connectionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food);

        editTextName = findViewById(R.id.editTextNewName);
        editTextPrice = findViewById(R.id.editTextNewPrice);
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonBack = findViewById(R.id.buttonBack);
        connectionHelper = new ConnectionHelper();

        buttonCreate.setOnClickListener(v -> createFood());
        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void createFood() {
        String name = editTextName.getText().toString();
        String priceString = editTextPrice.getText().toString();

        if (name.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceString);
        new CreateFoodTask().execute(name, price);
    }


    private class CreateFoodTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            String name = (String) params[0];
            double price = (double) params[1];

            // Get the current date
            java.util.Date createAt = new java.util.Date(); // Ensure you're using java.util.Date

            try {
                Connection conn = connectionHelper.connectionClass();
                if (conn != null) {
                    String query = "INSERT INTO Foods (Name, Price, CreateAt) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, name);
                    stmt.setDouble(2, price);

                    // Convert java.util.Date to java.sql.Date
                    stmt.setDate(3, new java.sql.Date(createAt.getTime()));

                    stmt.executeUpdate();
                    conn.close();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CreateFoodActivity.this, "Food created successfully", Toast.LENGTH_SHORT).show();

                // Set result to indicate success
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);

                finish(); // Close the activity and return to MainActivity
            } else {
                Toast.makeText(CreateFoodActivity.this, "Failed to create food", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
