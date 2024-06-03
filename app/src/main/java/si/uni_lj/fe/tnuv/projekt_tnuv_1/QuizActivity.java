package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    Map<String, String> userInfo = new HashMap<>();
    public List<Integer> user_answers = new ArrayList<>();
    private TextView questionTextView;
    private Button confirmButton;
    private DatePicker datePick;
    private List<Question> questions = new ArrayList<>();
    private EditText budgetEditText;
    boolean validTransition = false;
    boolean isBudgetQuestion = false;

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
        budgetEditText = findViewById(R.id.budgetEditText);
        questionTextView.setText("What is your birth date?");
        // Load questions from Firestore
        loadQuestionsfromFireStore();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle confirm button click
                // Update progress bar
                if(!isBudgetQuestion){
                    quizProgressBar.setProgress(100 / (numberOfQuestions + 1));

                    // Hide the date picker and confirm button
                    datePick.setVisibility(View.GONE);
                    budgetEditText.setVisibility(View.VISIBLE);
                    questionTextView.setText("What is your investment budget?");
                    // Add the user's birth date to the userInfo list
                    userInfo.put("Date of birth", String.valueOf(datePick.getYear()));

                    quizProgressText.setText("Warming up!");
                    isBudgetQuestion = true;
                } else {
                    budgetEditText.setVisibility(View.GONE);
                    confirmButton.setVisibility(View.GONE);
                    userInfo.put("Budget", String.valueOf(Integer.parseInt(budgetEditText.getText().toString())));
                    setQuestion(questions.get(0));
                }
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
     * When an answer button is clicked, the index of the answer is added to the answers list,
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
                user_answers.add(finalI);
                if (progress == numberOfQuestions - 1) {
                    // Last question
                    // Create an Intent to start MainActivity (Loading Screen)
                    validTransition = true;
                    Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                    // Calculate recommended portfolio after the last question and store value in the user info
                    userInfo.put("Recommended portfolio", calculateRecommendedPortfolio());
                    Toast.makeText(this, "Quiz completed:" + userInfo, Toast.LENGTH_SHORT).show();
                    storeUserInfoToFiresStore();
                    startActivity(intent);
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

        // Add the 'portfolio' field
        Map<String, Integer> portfolio = new HashMap<>();
        portfolio.put("Cash", Integer.valueOf(userInfo.get("Budget")));
        data.put("portfolio", portfolio);

        db.collection("users").document(userId).set(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User info stored", Toast.LENGTH_SHORT).show();
            } else {
                // Handle errors
                Toast.makeText(this, "Failed to store user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (validTransition) {
            return;
        }
        // Delete the current user
        // This depends on how you're storing the current user
        // For example, if you're using SharedPreferences, you can do something like this:
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(QuizActivity.this, "User account deleted.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(QuizActivity.this, "Failed to delete user account.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        // Start MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // This will finish the current activity (QuizActivity)
    }


    /**
     * This method calculates the recommended portfolio based on the user's answers.
     * The user's answers are stored in the userInfo list.
     */
    private String calculateRecommendedPortfolio() {
        int budget = Integer.parseInt(userInfo.get("Budget"));
        int weightedSum = 0;

        // Improved loop by replacing manual index manipulation with enhanced for loop
        for (int i = 2; i < user_answers.size(); i++) {
            weightedSum += user_answers.get(i) * 10;  // Simplified accumulation
        }

        // Determine the portfolio name based on budget and weightedSum
        String recommendedPortfolioName;  // Default value set here
        if (budget <= 2500) {
            recommendedPortfolioName = "Basic";
        } else if (weightedSum <= 10) {
            recommendedPortfolioName = "Conservative";
        } else if (weightedSum > 30) {
            recommendedPortfolioName = "Aggressive";
        } else {
            recommendedPortfolioName = "Moderate";
        }

        // Display a toast with the portfolio type
        Toast.makeText(this, recommendedPortfolioName, Toast.LENGTH_SHORT).show();

        // Return the string corresponding to the recommended portfolio, or return default Moderate
        return recommendedPortfolioName;
    }
}