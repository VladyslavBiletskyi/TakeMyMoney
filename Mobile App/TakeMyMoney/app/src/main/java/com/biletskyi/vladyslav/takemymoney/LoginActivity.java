package com.biletskyi.vladyslav.takemymoney;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements NetworkActivity {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    SharedPreferences preferences;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
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
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        Button signUpButton=(Button)findViewById(R.id.btnRegister);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register=new Intent(LoginActivity.this,Register.class);
                startActivity(register);
            }
        });
        pd=new ProgressDialog(this);
        pd.setMessage("Идет загрузка данных");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        pd.show();
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

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
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            pd.hide();
        } else {
            // perform the user login attempt.
            HttpPostQuery query=new HttpPostQuery(this);
            query.execute(new Pair<String, String>("http://takemymoneyapi.azurewebsites.net/Token", null),
                    new Pair<String, String>("grant_type", "password"),
                    new Pair<String, String>("username", email),
                    new Pair<String, String>("password", password));
        }
    }
    private boolean isEmailValid(String email) {
        return email.matches("([a-zA-Z0-9][-|_|\\.]?){2,20}@[a-z]{2,8}.([a-z]{2,8}.)?[a-z]{2,5}");
    }

    private boolean isPasswordValid(String password) {
        return password.matches("[a-zA-Z0-9]{6,20}");
    }

    @Override
    public void onBackgroundTaskFinish(String ans) {
        try {
            JSONObject jsonObject = new JSONObject(ans);
            preferences.edit().putString("token", jsonObject.getString("access_token")).commit();
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            pd.hide();
            finish();
        } catch (Exception e) {
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage("Ошибка при попытке входа. повторите еще раз");
            alert.show();
            pd.hide();
        }
    }
}

    /**
     * Shows the progress UI and hides the login form.
     */
