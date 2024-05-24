package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PortfolioActivity extends AppCompatActivity {

    //prepare data for the pie chart and dropdown menu
    String[] assets = {"Stocks", "Precious metals", "Crypto", "Cash"};
    //String[] portfolio_options = {"Risky", "Moderate", "Conservative", "W. Buffet"};
    // Initialize portfolio options as a List
    List<String> portfolioOptionsList = new ArrayList<>(Arrays.asList("Risky", "Moderate", "Conservative", "W. Buffet", "C. Wood"));

    int[] percentage = new int[4];

    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;

    // Create an ArrayList of AssetModel objects, this will hold the models for the assets, that we will send to recycler viewer
    ArrayList<AssetModel> assetModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Set up the AssetModels
        SetUpAssetModels("Risky"); // To be changed after getting results from quiz

        // Create an instance of the AM_RecyclerViewAdapter and pass the context
        AM_RecyclerViewAdapter am_recyclerViewAdapter = new AM_RecyclerViewAdapter(this, assetModels);

        // Set the adapter to the recycler view
        recyclerView.setAdapter(am_recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set up the dropdown menu
        autoCompleteText = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, portfolioOptionsList);
        autoCompleteText.setAdapter(adapterItems);

        // Modify the specific element
        portfolioOptionsList.set(0, "Risky (Recommended)");  // not handled in recycler view yet
        autoCompleteText.setText("Risky (Recommended)", false); //  To be changed after getting results from quiz

        // set up the pie chart
        final Pie pie = AnyChart.pie();
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        setChartData("Risky", pie);  //to be changed after getting results from quiz
        anyChartView.setChart(pie);

        // set up the dropdown menu listener - when an item is selected, update the pie chart
        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selected_item_from_dd_menu = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Selected " + selected_item_from_dd_menu + " portfolio.", Toast.LENGTH_SHORT).show();
                setChartData(selected_item_from_dd_menu, pie);

                // Update the asset models
                SetUpAssetModels2(selected_item_from_dd_menu,am_recyclerViewAdapter);
            }
        });
    }

    private List<String> getAssets(String selected_item_from_dd_menu) {
        switch (selected_item_from_dd_menu) {
            case "Conservative":
                return Arrays.asList("Bonds", "Real Estate", "Cash", "Precious metals");
            case "W. Buffet":
                return Arrays.asList("Apple", "Bank America Corp", "American Express", "Coca Cola Co", "Chevron", "Others");
            case "C. Wood":
                return Arrays.asList("Coinbase", "Tesla", "Roku", "Block", "UiPath", "Robinhood", "Others");
            default:
                return Arrays.asList("Stocks", "Precious metals", "Crypto", "Cash"); // Default assets
        }
    }
    private int[] getAllocation(String selected_item_from_dd_menu) {
        switch (selected_item_from_dd_menu) {
            case "Risky":
                return new int[]{70, 10, 10, 10};
            case "Moderate":
                return new int[]{50, 20, 10, 10};
            case "Conservative":
                return new int[]{50, 25, 25, 10};
            case "W. Buffet":
                return new int[]{40, 12, 10, 8, 5, 25};
            case "C. Wood":
                return new int[]{8, 7, 6, 5, 4, 4, 67};
            default:
                return new int[]{0, 0, 0, 0}; // Default allocation in case none of the above matches
        }
    }

    private void setChartData(String selected_item_from_dd_menu, Pie pie) {
        List<DataEntry> dataEntries = new ArrayList<>();
        percentage = getAllocation(selected_item_from_dd_menu);

        assets = getAssets(selected_item_from_dd_menu).toArray(new String[0]);

        for (int i = 0; i < assets.length; i++) {
            dataEntries.add(new ValueDataEntry(assets[i], percentage[i]));
        }
        pie.data(dataEntries);
        pie.legend(false);
        pie.innerRadius(30);
        pie.background().fill("#ECEEF6");
    }


    private void SetUpAssetModels(String selected_item_from_quiz) {
        int[] allocation = getAllocation(selected_item_from_quiz);
        assets = getAssets(selected_item_from_quiz).toArray(new String[0]);

        for (int i = 0; i < assets.length; i++) {
            assetModels.add(new AssetModel(assets[i], allocation[i], 0));
        }
    }

    private void SetUpAssetModels2(String selected_item_from_dd_menu, AM_RecyclerViewAdapter adapter) {
        //lahko bi si naredil v values strings.xml in tam shranil vrednosti
        // Add the AssetModel objects to the assetModels ArrayList
        int[] allocation = getAllocation(selected_item_from_dd_menu);
        assets = getAssets(selected_item_from_dd_menu).toArray(new String[0]);

        if (assetModels.size() != assets.length) {
            assetModels.clear();
            for (int i = 0; i < assets.length; i++) {
                assetModels.add(new AssetModel(assets[i], allocation[i], 0));
            }
            adapter.notifyDataSetChanged();
        }
        else{
            for (int i = 0; i < assets.length; i++) {
                assetModels.set(i, new AssetModel(assets[i], allocation[i], 0));
                adapter.notifyItemChanged(i);
            }
        }
    }
}
