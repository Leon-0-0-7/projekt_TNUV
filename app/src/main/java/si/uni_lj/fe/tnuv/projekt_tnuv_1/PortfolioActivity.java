package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.core.ui.Label;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PortfolioActivity extends AppCompatActivity {
    List<String> portfolioStrategiesList = new ArrayList<>();
    Map<String, Integer> userPortfolio;
    Map<String, Map<String, Integer>> strategiesMap;
    Map<String, String> userInfo;
    ArrayAdapter<String> adapterItems;
    AutoCompleteTextView autoCompleteText;
    // Create an ArrayList of AssetModel objects, this will hold the models for the assets, that we will send to recycler viewer
    ArrayList<AssetModel> assetModels = new ArrayList<>();
    // TODO: Add budget to question
    int budget = 0;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private AM_RecyclerViewAdapter am_recyclerViewAdapter;
    private Pie pie;
    private AnyChartView anyChartView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_portfolio_activity);

        // Get the portfolio options from the Intent
        getDataFromFirestore();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Initializations
        setUpRecyclerView();
        setUpPieChart();
        setUpNavigationView();
        setUpDropdownMenu();

        // TODO: Use quiz results to determine the default portfolio option
        if(!portfolioStrategiesList.isEmpty()){
            String chosenPortfolio = userInfo.get("Recommended portfolio");
            int recommended = portfolioStrategiesList.indexOf(chosenPortfolio);

            Map<String, Integer> selectedStrategy = strategiesMap.get(portfolioStrategiesList.get(recommended));
            // Add a checkmark to the recommended portfolio
            // portfolioStrategiesList.set(recommended, chosenPortfolio + " \uD83D\uDCAB");
            // Default to the first portfolio option
            autoCompleteText.setText(portfolioStrategiesList.get(recommended), false);
          
            assert selectedStrategy != null;
            setChartDataAndAssetModels(selectedStrategy, pie, am_recyclerViewAdapter);
        }
        else{
            Toast.makeText(getApplicationContext(), "No portfolio strategies found.", Toast.LENGTH_SHORT).show();
        }


        // Set the chart data and asset models for the first portfolio option
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
        textInputLayout.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        am_recyclerViewAdapter = new AM_RecyclerViewAdapter(this, assetModels, userPortfolio);
        recyclerView.setAdapter(am_recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpDropdownMenu() {
        autoCompleteText = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, portfolioStrategiesList);
        autoCompleteText.setAdapter(adapterItems);
        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selected_item_from_dd_menu = parent.getItemAtPosition(position).toString();
                Map<String, Integer> selectedStrategy = strategiesMap.get(selected_item_from_dd_menu);
//                Toast.makeText(getApplicationContext(), "Selected " + selectedStrategy + " portfolio.", Toast.LENGTH_SHORT).show();
                assert selectedStrategy != null;
                setChartDataAndAssetModels(selectedStrategy, pie, am_recyclerViewAdapter);
            }
        });
    }

    private void setUpPieChart() {
        pie = AnyChart.pie();
        anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setChart(pie);
    }


    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
//                drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });
    }

    private boolean handleNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about_app) {
            // Retrieve the app version from BuildConfig

            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
            String version = pInfo.versionName;//Version Name
            int verCode = pInfo.versionCode;//Version Code
            String message = "App version " + version + "\nCode version: " + verCode;

            // Display the Toast message with longer duration
            Toast.makeText(PortfolioActivity.this, message, Toast.LENGTH_LONG).show();
            Toast.makeText(PortfolioActivity.this, "User ID: " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.nav_home) {
            // Handle the home action
            // Start Main Activity
            Intent intent = new Intent(PortfolioActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_portfolio_1) { // TODO: Change to dynamic portfolio handling
            // Show right portfolio
            Toast.makeText(PortfolioActivity.this, "Portfolio 1", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            // Create an AlertDialog.Builder instance
            AlertDialog.Builder builder = new AlertDialog.Builder(PortfolioActivity.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            // Set the positive (Yes) button
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle the logout action
                    // Sign out the user
                    FirebaseAuth.getInstance().signOut();
                    // Start the login activity
                    Intent intent = new Intent(PortfolioActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            // Set the negative (No) button
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Close the dialog
                    dialog.dismiss();
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        drawerLayout.closeDrawer(navigationView);
        return true;

    }


    private void setChartDataAndAssetModels(Map<String, Integer> selectedStrategy, Pie pie, AM_RecyclerViewAdapter adapter) {
        // Set chart data
        List<DataEntry> dataEntries = new ArrayList<>();
        String[] assets = selectedStrategy.keySet().toArray(new String[0]);
        // values are allocations
        int[] values = selectedStrategy.values().stream().mapToInt(i -> i).toArray();
        // convert values from percentages to absolute values
        budget = Integer.parseInt(userInfo.get("Budget"));
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i] * budget / 100;
        }

        for (int i = 0; i < assets.length; i++) {
            dataEntries.add(new ValueDataEntry(assets[i], values[i]));
        }

        Label centerLabel = pie.label(0);
        centerLabel.text("Budget " + budget + "€");
        centerLabel.offsetX("50%");
        centerLabel.offsetY("50%");
        centerLabel.anchor("center");

        pie.data(dataEntries);
        pie.legend(false);
        pie.innerRadius(60);
        pie.background().fill("#ECEEF6");

        // Set up asset models
        if (assetModels.size() != assets.length) {
            assetModels.clear();
            for (int i = 0; i < assets.length; i++) {
                int currentValue = userPortfolio.getOrDefault(assets[i], 0);
                assetModels.add(new AssetModel(assets[i], values[i], currentValue));
            }
            adapter.notifyDataSetChanged();
        }
        else{
            for (int i = 0; i < assets.length; i++) {
                int currentValue = userPortfolio.getOrDefault(assets[i], 0);
                assetModels.get(i).setCurrentValue(currentValue);
                adapter.notifyItemChanged(i);
            }
        }
    }

    private void getDataFromFirestore() {
        // Retrieve the fetched data from the Intent
        Intent intent = getIntent();
        String strategiesJson = intent.getStringExtra("strategies");
        String userPortfolioJson = intent.getStringExtra("portfolio");
        String userInfoJson = intent.getStringExtra("userInfo");
        // Parse the JSON string to get the strategies
        Gson gson = new Gson();

        // strategiesMap is a Map where each key is a strategy name and the value is another Map representing the assets and their allocations
        strategiesMap = gson.fromJson(strategiesJson, new TypeToken<Map<String, Map<String, Integer>>>(){}.getType());
        // userPortfolio is a Map where each key is an asset name and the value is the current value of the asset
        userPortfolio = gson.fromJson(userPortfolioJson, new TypeToken<Map<String, Integer>>(){}.getType());
        userInfo = gson.fromJson(userInfoJson, new TypeToken<Map<String, String>>(){}.getType());

        assert strategiesMap != null;
        // We can get the portfolio options by getting the keys of the strategiesMap
        portfolioStrategiesList.addAll(strategiesMap.keySet());
        Collections.sort(portfolioStrategiesList);
    }


}
