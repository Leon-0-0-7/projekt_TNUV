package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to the next activity
//            Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);
//            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
//            startActivity(intent);
//            finish();
            prefetchAndStartPortfolioActivity();
        } else {
            // User is signed out, navigate to the login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    // TODO: Move this method to a separate class
    private void prefetchAndStartPortfolioActivity() {
        Toast.makeText(this, "Fetching data...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("strategies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> portfolioOptionsList = new ArrayList<>();

                for (int i = 0; i < task.getResult().size(); i++) {
                    portfolioOptionsList.add(task.getResult().getDocuments().get(i).getId());

                }

                // Once the data is fetched, start PortfolioActivity
                Intent intent = new Intent(this, PortfolioActivity.class);
                // Pass the fetched data to PortfolioActivity
                intent.putStringArrayListExtra("portfolioOptionsList", new ArrayList<>(portfolioOptionsList));
                startActivity(intent);
                finish();
            } else {
                // Handle the error
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}