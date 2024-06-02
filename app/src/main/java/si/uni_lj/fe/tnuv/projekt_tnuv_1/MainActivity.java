package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);
//            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            startActivity(intent);
            finish();
            prefetchAndStartPortfolioActivity();
        } else {
            // User is signed out, navigate to the login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    // TODO: Move this method to a separate class
    /**
     * This method prefetches the data needed for the PortfolioActivity and starts the activity.
     * The data is fetched from Firestore and stored in a list.
     * The list is passed to the PortfolioActivity as an extra in the Intent.
     */
    private void prefetchAndStartPortfolioActivity() {
        Toast.makeText(this, "Fetching data...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("strategies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                Map<String, Map<String, Object>> documentsMap = new HashMap<>();
                for (DocumentSnapshot document : documents) {
                    documentsMap.put(document.getId(), document.getData());
                }

                Gson gson = new Gson();
                String json = gson.toJson(documentsMap);
                // Once the data is fetched, start PortfolioActivity
                Intent intent = new Intent(this, PortfolioActivity.class);
                //  Pass the fetched data to PortfolioActivity
                intent.putExtra("strategies", json);
                startActivity(intent);
                finish();
            } else {
                // Handle the error
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}