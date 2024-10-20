package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements FoodAdapter.OnDeleteClickListener {

    ListView listView;
    FoodAdapter adapter;
    ArrayList<Food> foodArrayList;
    ConnectionHelper connectionHelper;
    Food selectedItem; // To keep track of the selected food item

    EditText editTextName, editTextPrice;
    Button loadDataButton, updateButton, createFoodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        listView = findViewById(R.id.listViewFood);
        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        loadDataButton = findViewById(R.id.button);
        updateButton = findViewById(R.id.buttonUpdate);
        createFoodButton = findViewById(R.id.buttonCreateFood);

        // Initialize the ArrayList and ConnectionHelper
        foodArrayList = new ArrayList<>();
        connectionHelper = new ConnectionHelper();

        // Load data from the database
        new FetchFoodTask().execute();

        // Load data from the database when the Load Data button is clicked
        loadDataButton.setOnClickListener(v -> new FetchFoodTask().execute());

        // Handle item selection from the ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected Food item
            selectedItem = foodArrayList.get(position);
            // Populate the EditText fields with the current values
            editTextName.setText(selectedItem.getName());
            editTextPrice.setText(String.valueOf(selectedItem.getPrice()));

            // Debug Toast message to verify the click
            Toast.makeText(MainActivity.this, "Item clicked: " + selectedItem.getName(), Toast.LENGTH_SHORT).show();
        });

        // Create new food item
        createFoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateFoodActivity.class);
            startActivityForResult(intent, 1); // Request code is 1
        });

        // Handle update button click to update the selected item
        updateButton.setOnClickListener(v -> {
            // Ensure an item is selected
            if (selectedItem != null) {
                String updatedName = editTextName.getText().toString();
                double updatedPrice = Double.parseDouble(editTextPrice.getText().toString());

                // Perform the update
                new UpdateFoodTask(selectedItem.getId(), updatedName, updatedPrice).execute();
            } else {
                Toast.makeText(MainActivity.this, "Please select an item to update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(Food food) {
        new DeleteFoodTask(food).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the requestCode is 1 and the result is OK
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Reload the list of foods
            new FetchFoodTask().execute();
        }
    }

    private class FetchFoodTask extends AsyncTask<Void, Void, ArrayList<Food>> {
        @Override
        protected ArrayList<Food> doInBackground(Void... voids) {
            ArrayList<Food> foodList = new ArrayList<>();

            try {
                Connection conn = connectionHelper.connectionClass();
                if (conn != null) {
                    String query = "SELECT * FROM Foods";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        int id = rs.getInt("Id");
                        String name = rs.getString("Name");
                        double price = rs.getDouble("Price");
                        Date createAt = rs.getDate("CreateAt");

                        Food food = new Food(id, name, price, createAt);
                        foodList.add(food);
                    }

                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return foodList;
        }

        @Override
        protected void onPostExecute(ArrayList<Food> foods) {
            super.onPostExecute(foods);
            foodArrayList = foods;
            adapter = new FoodAdapter(MainActivity.this, foodArrayList, MainActivity.this);
            listView.setAdapter(adapter);
        }
    }

    // AsyncTask to update the selected food item in the database
    private class UpdateFoodTask extends AsyncTask<Void, Void, Void> {

        private int foodId;
        private String updatedName;
        private double updatedPrice;

        public UpdateFoodTask(int id, String name, double price) {
            this.foodId = id;
            this.updatedName = name;
            this.updatedPrice = price;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection conn = connectionHelper.connectionClass();
                if (conn != null) {
                    java.util.Date currentDate = new java.util.Date();
                    java.sql.Timestamp timestamp = new java.sql.Timestamp(currentDate.getTime());

                    String query = "UPDATE Foods SET Name = ?, Price = ?, CreateAt = ? WHERE Id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, updatedName);
                    stmt.setDouble(2, updatedPrice);
                    stmt.setTimestamp(3, timestamp);
                    stmt.setInt(4, foodId);
                    stmt.executeUpdate();

                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new FetchFoodTask().execute();
        }
    }

    // AsyncTask to delete the food item
    private class DeleteFoodTask extends AsyncTask<Void, Void, Boolean> {
        private Food foodToDelete;

        DeleteFoodTask(Food food) {
            this.foodToDelete = food;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Connection conn = connectionHelper.connectionClass();
                if (conn != null) {
                    String query = "DELETE FROM Foods WHERE Id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, foodToDelete.getId());
                    int affectedRows = pstmt.executeUpdate();
                    conn.close();
                    return affectedRows > 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                foodArrayList.remove(foodToDelete);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Food item deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to delete food item", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
