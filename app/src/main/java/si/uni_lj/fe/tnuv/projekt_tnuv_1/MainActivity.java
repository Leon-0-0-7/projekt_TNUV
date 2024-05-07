package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button gumb_create_portfolio=findViewById(R.id.create_portolio);
        gumb_create_portfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, PortfolioActivity.class);
                startActivity(intent);

            }
        });

    }
}