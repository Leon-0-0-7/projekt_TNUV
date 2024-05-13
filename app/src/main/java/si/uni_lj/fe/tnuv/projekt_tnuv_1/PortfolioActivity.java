package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

public class PortfolioActivity extends AppCompatActivity {
    String[] assets = {"Stocks", "Precious metals", "Crypto", "Cash"};
    String[] items = {"Risky", "Moderate", "Conservative", "W. Buffet"};
    int[] percentage = {5000, 2500, 2500, 500};

    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        autoCompleteText = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        autoCompleteText.setAdapter(adapterItems);

        // Set default value
        autoCompleteText.setText("Recommended Portfolio", false); // can set to 'false' to avoid triggering item selection event

        setupPieChart("Risky");


        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Selected "+item+" portfolio.", Toast.LENGTH_SHORT).show();
                setupPieChart(item);
            }
        });

    }
    public void setupPieChart(String selected_item) {
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        switch (selected_item) {
            case "Risky":
                percentage = new int[]{7000, 1000, 1000, 1000};
                break;
            case "Moderate":
                percentage = new int[]{5000, 2000, 1000, 0};
                break;
            case "Conservative":
                percentage = new int[]{5000, 2500, 2500, 0};
                break;
            case "W. Buffet":
                percentage = new int[]{4000, 3000, 3000, 0};
                break;
        }
        for (int i = 0; i < assets.length; i++){
            dataEntries.add(new ValueDataEntry(assets[i], percentage[i]));

        }
        pie.data(dataEntries);
        pie.legend(false);
        pie.legend().itemsLayout("verticalExpandable");
        pie.legend().position("bottom");
        pie.legend().align("left");
        pie.background().fill("#ECEEF6");
        pie.innerRadius("30%");
        anyChartView.setChart(pie);
    }
}

