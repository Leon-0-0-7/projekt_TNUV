package si.uni_lj.fe.tnuv.projekt_tnuv_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText field_username, field_password;
    private Button btn_login;
    private TextView tv_create_account;
    private boolean isLoginMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        field_username = findViewById(R.id.field_username);
        field_password = findViewById(R.id.field_password);
        btn_login = findViewById(R.id.btn_login);
        tv_create_account = findViewById(R.id.tv_create_account);

        tv_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoginMode) {
                    btn_login.setText(R.string.btn_create_account);
                    tv_create_account.setText(R.string.login);
                } else {
                    btn_login.setText(R.string.btn_login);
                    tv_create_account.setText(R.string.create_account);
                }
                isLoginMode = !isLoginMode;
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = field_username.getText().toString();
                String password = field_password.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    if (isLoginMode) {
                        loginUser(email, password);
                    } else {
                        createUser(email, password);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        //Toast.makeText(LoginActivity.this, "Authentication successful.",
                        //        Toast.LENGTH_SHORT).show();
                        // Navigate to next activity here
                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Exception e = task.getException();
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        //Toast.makeText(LoginActivity.this, "Account created successfully.",
                        //       Toast.LENGTH_SHORT).show();
                        // Navigate to next activity here
                        Intent intent=new Intent(LoginActivity.this, QuizActivity.class);
                        startActivity(intent);

                    } else {
                        Exception e = task.getException();
                        // If sign up fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}