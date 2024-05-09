package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class QuizActivity extends AppCompatActivity {

    private ProgressBar quizProgressBar;
    private TextView quizProgressText;
    private TextView questionTextView;
    private Button confirmButton;
    private Button answerButton1;
    private Button answerButton2;
    private Button answerButton3;
    private DatePicker datePick;
    JSONObject user_json = new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizProgressBar = findViewById(R.id.quizProgressBar);
        quizProgressText = findViewById(R.id.progressBarText);
        questionTextView = findViewById(R.id.quizQuestion);
        confirmButton = findViewById(R.id.btn_confirm);
        answerButton1 = findViewById(R.id.btn_answer1);
        answerButton2 = findViewById(R.id.btn_answer2);
        answerButton3 = findViewById(R.id.btn_answer3);
        datePick = findViewById(R.id.birthDatePicker);
        // Set up your quiz here. For example:
        questionTextView.setText("What is your birth date?");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle confirm button click
                // Update progress bar
                quizProgressBar.setProgress(33);

                // Hide the date picker and confirm button
                datePick.setVisibility(View.GONE);
                confirmButton.setVisibility(View.GONE);

                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(300);
                // Show the answer buttons
                answerButton1.startAnimation(fadeIn);
                answerButton1.setVisibility(View.VISIBLE);
                answerButton2.startAnimation(fadeIn);
                answerButton2.setVisibility(View.VISIBLE);
                answerButton3.startAnimation(fadeIn);
                answerButton3.setVisibility(View.VISIBLE);

                answerButton3.startAnimation(fadeIn);
                quizProgressText.setText("Warming up!");
                // Create an Intent to start PortfolioActivity
//                Intent intent = new Intent(QuizActivity.this, PortfolioActivity.class);
//                startActivity(intent);
                saveUserData(user_json, "birth-year", datePick.getYear());
                saveUserData(user_json, "birth-day", datePick.getDayOfMonth());
                saveUserData(user_json, "birth-month", datePick.getMonth() + 1);

            }
        });
        answerButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle answer button 1 click
                // Update progress bar
                quizProgressBar.setProgress(quizProgressBar.getProgress() + 10);

                answerButton1.setText(getValueFromUserData("birth-year"));

                // Check if progress bar is full
                if (quizProgressBar.getProgress() >= 100) {
                    // Start PortfolioActivity
                    Intent intent = new Intent(QuizActivity.this, PortfolioActivity.class);
                    startActivity(intent);
                }

            }
        });

        answerButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle answer button 2 click
                // Update progress bar
                quizProgressBar.setProgress(quizProgressBar.getProgress() + 10);
                // Check if progress bar is full
                if (quizProgressBar.getProgress() >= 100) {
                    // Start PortfolioActivity
                    Intent intent = new Intent(QuizActivity.this, PortfolioActivity.class);
                    startActivity(intent);
                }
            }
        });

        answerButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle answer button 3 click
                // Update progress bar
                quizProgressBar.setProgress(quizProgressBar.getProgress() + 10);
                // Check if progress bar is full
                if (quizProgressBar.getProgress() >= 100) {
                    // Start PortfolioActivity
                    Intent intent = new Intent(QuizActivity.this, PortfolioActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public String getValueFromUserData(String key) {
        String value = "";
        try {
            File file = new File(getFilesDir(), "user_data.json");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String jsonString = stringBuilder.toString();

            JSONObject jsonObject = new JSONObject(jsonString);
            value = jsonObject.getString(key);

            fileReader.close();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public void saveUserData(JSONObject json, String key, int value) {
        try {
            json.put(key, value);
            String jsonString = json.toString();
            FileWriter fileWriter = new FileWriter(new File(getFilesDir(), "user_data.json"));
            fileWriter.write(jsonString);
            fileWriter.close();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
}

