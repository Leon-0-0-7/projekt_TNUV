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
    String[] portfolio_options = {"Risky", "Moderate", "Conservative", "W. Buffet"};

    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    List<DataEntry> dataEntries = new ArrayList<>();
    int[] percentage = new int[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        autoCompleteText = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, portfolio_options);
        autoCompleteText.setAdapter(adapterItems);
        autoCompleteText.setText("Risky (Recommended)", false); //to be changed after getting results from quiz

        final Pie pie = AnyChart.pie();
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        setChartData("Risky", pie);  //to be changed after getting results from quiz
        anyChartView.setChart(pie);

        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selected_item_from_dd_menu = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Selected " + selected_item_from_dd_menu + " portfolio.", Toast.LENGTH_SHORT).show();
                setChartData(selected_item_from_dd_menu, pie);
            }
        });

    }

    private void setChartData(String selected_item_from_dd_menu, Pie pie) {
        List<DataEntry> dataEntries = new ArrayList<>();

        switch (selected_item_from_dd_menu) {
            case "Risky":
                percentage = new int[]{70, 10, 10, 10};
                break;
            case "Moderate":
                percentage = new int[]{50, 20, 10, 10};
                break;
            case "Conservative":
                percentage = new int[]{50, 25, 25, 10};
                break;
            case "W. Buffet":
                percentage = new int[]{40, 30, 30, 10};
                break;
        }

        for (int i = 0; i < assets.length; i++) {
            dataEntries.add(new ValueDataEntry(assets[i], percentage[i]));
        }
        pie.data(dataEntries);
        pie.legend(false);
        pie.background().fill("#ECEEF6");
    }
}
