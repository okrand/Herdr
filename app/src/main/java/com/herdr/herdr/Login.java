package com.herdr.herdr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class Login extends AppCompatActivity {
    CallbackManager callbackManager = CallbackManager.Factory.create();
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final Intent startBrowse = new Intent(Login.this, Browse.class);
        if (AccessToken.getCurrentAccessToken() != null){ // If already logged in, move onto next activity
            Log.d("ACCESSTOKEN", "NOT NULL");
            LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("email", "public_profile"));
            //Log.d("LOGGED", "Logged In " + Profile.getCurrentProfile().getName());
            handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
        }
        Log.d("ACCESSTOKEN", "NULL");
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final LoginManager loginManager = LoginManager.getInstance();
            //loginManager.logInWithReadPermissions(Login.this, Arrays.asList("email", "public_profile"));
            loginManager.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {
                            Log.d("CALLBACK", "SUCCESS");
                            // Check user's age range for 18+
                            new GraphRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    "me?fields=age_range",
                                    null,
                                    HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        public void onCompleted(GraphResponse response) {
                                            Log.d("GraphAPI", response.toString());
                                            JSONObject jsonObject = response.getJSONObject();
                                            try {
                                                JSONObject age_range = jsonObject.getJSONObject("age_range");
                                                if ((int) age_range.get("min") < 18) {
                                                    //user below 18
                                                    Toast.makeText(getApplicationContext(), "You aren't old enough", Toast.LENGTH_LONG).show();
                                                    loginManager.logOut();
                                                } else { //Welcome user
                                                    //Log.d("WELCOME USER", "Welcome" + Profile.getCurrentProfile().getFirstName());
                                                    handleFacebookAccessToken(loginResult.getAccessToken());
                                                    //Toast.makeText(getApplicationContext(), "Welcome " + Profile.getCurrentProfile().getName(), Toast.LENGTH_LONG).show();
                                                }
                                            } catch (JSONException e) {
                                                Log.d("ONCOMPLETEEXCEPTION", "JSONEXCEPTION");
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                            ).executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getApplicationContext(), "You need a Facebook account to use Herdr", Toast.LENGTH_LONG).show();
                            Log.d("FACEBOOK", "CANCEL");
                            LoginManager.getInstance().logOut();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Toast.makeText(getApplicationContext(), "There was a problem logging you in.", Toast.LENGTH_LONG).show();
                            Log.d("FACEBOOK", exception.toString());
                        }
                    });
                }
            });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FACEBOOKACCESSTOKEN", "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SUCCESS", "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(), "Welcome " + Profile.getCurrentProfile().getFirstName(), Toast.LENGTH_LONG).show();
                            final Intent startBrowse = new Intent(Login.this, Browse.class);
                            Login.this.startActivity(startBrowse);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FIREBASE FAIL", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
