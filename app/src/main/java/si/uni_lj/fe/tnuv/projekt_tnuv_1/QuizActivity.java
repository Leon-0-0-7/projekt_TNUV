package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * QuizActivity is an activity that presents a quiz to the user.
 * The quiz questions are loaded from Firestore and presented one at a time.
 * The user's answers are stored in a list and saved to Firestore when the quiz is completed.
 */
public class QuizActivity extends AppCompatActivity {

    private ProgressBar quizProgressBar;
    private TextView quizProgressText;
    private int progress = 0;
    private int numberOfQuestions;
    private List<Integer> userInfo = new ArrayList<>();
    private TextView questionTextView;
    private Button confirmButton;
    private DatePicker datePick;
    private List<Question> questions = new ArrayList<>();

    /**
     * This method is called when the activity is starting.
     * It initializes the activity and sets up the UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizProgressBar = findViewById(R.id.quizProgressBar);
        quizProgressText = findViewById(R.id.progressBarText);
        questionTextView = findViewById(R.id.quizQuestion);
        confirmButton = findViewById(R.id.btn_confirm);
        datePick = findViewById(R.id.birthDatePicker);

        questionTextView.setText("What is your birth date?");
        // Load questions from Firestore
        loadQuestionsfromFireStore();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle confirm button click
                // Update progress bar
                quizProgressBar.setProgress(100 / (numberOfQuestions + 1));

                // Hide the date picker and confirm button
                datePick.setVisibility(View.GONE);
                confirmButton.setVisibility(View.GONE);
                // Add the user's birth date to the userInfo list
                userInfo.add(datePick.getYear());

                quizProgressText.setText("Warming up!");

                setQuestion(questions.get(0));
            }
        });

    }

    /**
     * This method loads the quiz questions from Firestore.
     * The questions are stored in a collection named "questions".
     * Each document in the collection represents a question and has a "question" field and an "answers" field.
     */
    private void loadQuestionsfromFireStore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Array to store questions
        questions = new ArrayList<>();

        db.collection("questions").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot qs = task.getResult();
                if (qs != null) {
                    for (int i = 0; i < qs.size(); i++) {
                        Question q = new Question();
                        q.question = qs.getDocuments().get(i).getString("question");
                        q.answers = (List<String>) qs.getDocuments().get(i).get("answers");
                        questions.add(q);
                        numberOfQuestions = qs.size();
                    }
                }
                Toast.makeText(this, "Questions loaded", Toast.LENGTH_SHORT).show();
            } else {
                // Handle errors
                Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method sets the current question to be displayed in the UI.
     * It creates a button for each answer and sets up a click listener for each button.
     * When an answer button is clicked, the index of the answer is added to the userInfo list,
     * the progress bar is updated, and the next question is set.
     */
    private void setQuestion(Question question) {
        questionTextView.setText(question.question);
        LinearLayout answers = findViewById(R.id.answersLayout);
        answers.removeAllViews(); // Clear any existing answer buttons
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < question.answers.size(); i++) {
            String answer = question.answers.get(i);
            AppCompatButton answerButton = (AppCompatButton) inflater.inflate(R.layout.btn_answer, answers, false);
            answerButton.setText(answer);
            answerButton.setVisibility(View.VISIBLE);
            final int finalI = i;
            // Handle answer button click
            answerButton.setOnClickListener(view -> {
                userInfo.add(finalI);
                if (progress == numberOfQuestions - 1) {
                    // Last question
                    // Create an Intent to start PortfolioActivity
                    prefetchAndStartPortfolioActivity();
                    Toast.makeText(this, "Quiz completed:" + userInfo, Toast.LENGTH_SHORT).show();
                    storeUserInfoToFiresStore();
                    return;
                }
                quizProgressBar.setProgress((100 / (numberOfQuestions + 1)) * (progress + 2));
                progress++;
                setQuestion(questions.get(progress));
            });

            answers.addView(answerButton);
        }
    }

    /**
     * This method stores the user's answers to Firestore.
     * The answers are stored in a document in the "users" collection.
     * The ID of the document is the ID of the current user.
     */
    private void storeUserInfoToFiresStore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", userInfo);

        db.collection("users").document(userId).set(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User info stored", Toast.LENGTH_SHORT).show();
            } else {
                // Handle errors
                Toast.makeText(this, "Failed to store user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * This method prefetches the data needed for the PortfolioActivity and starts the activity.
     * The data is fetched from Firestore and stored in a list.
     * The list is passed to the PortfolioActivity as an extra in the Intent.
     */
    // TODO: FINISH THIS FUNCTION
    private void prefetchAndStartPortfolioActivity() {
        Toast.makeText(this, "Fetching data...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("strategies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                ArrayList<Map<String, Object>> allDocuments = new ArrayList<>();
                for (DocumentSnapshot document : documents) {
                    allDocuments.add(document.getData());
                }

                Gson gson = new Gson();
                String json = gson.toJson(allDocuments);
                // Once the data is fetched, start PortfolioActivity
                Intent intent = new Intent(this, PortfolioActivity.class);
                // Pass the fetched data to PortfolioActivity
                intent.putExtra("strategies", json);
//                intent.putStringArrayListExtra("assetAllocationList", new ArrayList<>(assetAllocationList));
//                intent.putStringArrayListExtra("assetList", new ArrayList<>(assetList));
                startActivity(intent);
                finish();
            } else {
                // Handle the error
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}