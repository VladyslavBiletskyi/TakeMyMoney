package com.biletskyi.vladyslav.takemymoney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends AppCompatActivity implements NetworkActivity {

    ProgressDialog pd;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the register form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.regEmail);
        mPasswordView = (EditText) findViewById(R.id.regPassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        mPasswordConfirmView = (EditText) findViewById(R.id.regPasswordC);
        mPasswordConfirmView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        preferences = getSharedPreferences("Important",MODE_PRIVATE);
        if (preferences.contains("token")){
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            finish();
        }
        Button signUpButton=(Button)findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
        pd=new ProgressDialog(this);
        pd.setMessage("Идет загрузка данных");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
    }


    @Override
    public void onBackgroundTaskFinish(String ans) {
        pd.hide();
        finish();
    }
    private void attemptRegister(){
        pd.show();
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirm=mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError("Заполните это поле");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("Пароль введен некорректно. Введите пароль, содержащий только буквы латинского алфавита или цифры");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Заполните это поле");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("E-mail введен некорректно");
            focusView = mEmailView;
            cancel = true;
        }
        //Check if passwords matches
        if(!confirm.equals(password)){
            mPasswordConfirmView.setError("Пароли не совпадают");
            focusView=mPasswordConfirmView;
            cancel=true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            pd.hide();
        } else {
            // perform the user login attempt.
            HttpPostQuery query=new HttpPostQuery(this);
            query.execute(new Pair<String, String>("http://takemymoneyapi.azurewebsites.net/api/Account/Register", "Register"),
                    new Pair<String, String>("Email", email),
                    new Pair<String, String>("Password", password),
                    new Pair<String, String>("ConfirmPassword", confirm));
        }
    }
    private boolean isEmailValid(String email) {
        return email.matches("([a-zA-Z0-9][-|_|\\.]?){2,20}@[a-z]{2,8}.([a-z]{2,8}.)?[a-z]{2,5}");
    }

    private boolean isPasswordValid(String password) {
        return password.matches("[a-zA-Z0-9]{6,20}");
    }

}
