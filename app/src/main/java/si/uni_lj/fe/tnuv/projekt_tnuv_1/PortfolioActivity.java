package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.List;

public class PortfolioActivity extends AppCompatActivity {

    AnyChartView anyChartView;

    String[] assets = {"Stocks", "Precious metals", "Crypto", "Cash"};
    int[] percentage = {5000, 2500, 2500, 500};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        anyChartView = findViewById(R.id.any_chart_view);

        setupPieChart();

    }

    public void setupPieChart(){
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();

        for (int i = 0; i < assets.length; i++){
            dataEntries.add(new ValueDataEntry(assets[i], percentage[i]));

        }
        pie.data(dataEntries);
        pie.legend(true);
        pie.legend().itemsLayout("verticalExpandable");
        pie.legend().position("bottom");
        pie.legend().align("left");
        pie.background().fill("#ECEEF6");
        anyChartView.setChart(pie);

    }
}