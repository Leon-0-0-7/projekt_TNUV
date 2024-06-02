package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PortfolioActivity extends AppCompatActivity {
    List<String> portfolioOptionsList = new ArrayList<>();
    int[] percentage = new int[4];
    Map<String, Map<String, Integer>> strategiesMap;
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;

    // Create an ArrayList of AssetModel objects, this will hold the models for the assets, that we will send to recycler viewer
    ArrayList<AssetModel> assetModels = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private AM_RecyclerViewAdapter am_recyclerViewAdapter;
    private Pie pie;
    private AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_portfolio_activity);

        // Get the portfolio options from the Intent
        getPortfolioData();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Initializations
        setUpRecyclerView();
        setUpDropdownMenu();
        setUpPieChart();
        setUpNavigationView();

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
        am_recyclerViewAdapter = new AM_RecyclerViewAdapter(this, assetModels);
        recyclerView.setAdapter(am_recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpDropdownMenu() {
        autoCompleteText = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, portfolioOptionsList);
        autoCompleteText.setAdapter(adapterItems);
        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selected_item_from_dd_menu = parent.getItemAtPosition(position).toString();
                Map<String, Integer> selectedStrategy = strategiesMap.get(selected_item_from_dd_menu);
                Toast.makeText(getApplicationContext(), "Selected " + selectedStrategy + " portfolio.", Toast.LENGTH_SHORT).show();
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
                drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });
    }

    private void handleNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        // TODO: Implement the navigation items
    }

    private void setChartDataAndAssetModels(Map<String, Integer> selectedStrategy, Pie pie, AM_RecyclerViewAdapter adapter) {
        // Set chart data
        List<DataEntry> dataEntries = new ArrayList<>();
        String[] assets = selectedStrategy.keySet().toArray(new String[0]);
        int[] values = selectedStrategy.values().stream().mapToInt(i -> i).toArray();

        for (int i = 0; i < assets.length; i++) {
            dataEntries.add(new ValueDataEntry(assets[i], values[i]));
        }
        pie.data(dataEntries);
        pie.legend(false);
        pie.innerRadius(30);
        pie.background().fill("#ECEEF6");

        // Set up asset models
        if (assetModels.size() != assets.length) {
            assetModels.clear();
            for (int i = 0; i < assets.length; i++) {
                assetModels.add(new AssetModel(assets[i], values[i], 0));
            }
            adapter.notifyDataSetChanged();
        }
        else{
            for (int i = 0; i < assets.length; i++) {
                assetModels.set(i, new AssetModel(assets[i], values[i], 0));
                adapter.notifyItemChanged(i);
            }
        }
    }

    private void getPortfolioData() {
        // Retrieve the fetched data from the Intent
        Intent intent = getIntent();
        String strategiesJson = intent.getStringExtra("strategies");
        // Parse the JSON string to get the strategies
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
        strategiesMap = gson.fromJson(strategiesJson, type);

        // Now, strategiesMap is a Map where each key is a strategy name and the value is another Map representing the assets and their allocations
        // We can get the portfolio options by getting the keys of the strategiesMap
        assert strategiesMap != null;
        portfolioOptionsList.addAll(strategiesMap.keySet());
    }
}
