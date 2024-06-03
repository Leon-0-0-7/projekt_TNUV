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
            prefetchAndStartPortfolioActivity();
        } else {
            // User is signed out, navigate to the login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    /**
     * Fetches the data from Firestore and starts the PortfolioActivity.
     * The data is passed to PortfolioActivity as a JSON string.
     * The data is fetched from the Firestore collection "strategies".
     * The data is a map of strategy names to strategy data.
     */
    private void prefetchAndStartPortfolioActivity() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;

        // Fetch strategies data
        db.collection("strategies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                Map<String, Map<String, Object>> documentsMap = new HashMap<>();
                for (DocumentSnapshot document : documents) {
                    documentsMap.put(document.getId(), document.getData());
                }

                // Fetch portfolio data
                if (userId != null) {
                    db.collection("users").document(userId).get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {

                            DocumentSnapshot userDocument = userTask.getResult();
                            Map<String, Object> portfolio = null;

                            if (userDocument != null && userDocument.exists()) {
                                portfolio = (Map<String, Object>) userDocument.get("portfolio");
//                                if (portfolio != null) {
//                                    documentsMap.put("Current Portfolio", portfolio);
//                                }
                            }

                            // Convert the data to JSON and start PortfolioActivity
                            Gson gson = new Gson();

                            String jsonStrategies = gson.toJson(documentsMap);
                            String jsonPortfolio = gson.toJson(portfolio);

                            Intent intent = new Intent(this, PortfolioActivity.class);

                            intent.putExtra("strategies", jsonStrategies);
                            intent.putExtra("portfolio", jsonPortfolio);

                            startActivity(intent);
                            finish();
                        } else {
                            // Handle the error
                            Toast.makeText(this, "Error fetching portfolio data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                // Handle the error
                Toast.makeText(this, "Error fetching strategy data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}