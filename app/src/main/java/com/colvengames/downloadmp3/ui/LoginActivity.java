package com.colvengames.downloadmp3.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.colvengames.downloadmp3.entity.UserModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.api.apiClient;
import com.colvengames.downloadmp3.api.apiRest;
import com.colvengames.downloadmp3.entity.ApiResponse;
import com.colvengames.downloadmp3.manager.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = "LoginActivity";
    public static final String key_nocode = "NOCODE";
    private static final int RC_SIGN_IN = 9001;

    private LoginButton sign_in_button_facebook;
    private SignInButton sign_in_button_google;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;

    private ProgressDialog register_progress;
    public static SharedPreferences prefM;
    private TextView text_view_skip_login;
    public static LoginActivity staticClass;
    public String emailActual;


    ArrayList<UserModel> userModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
prefM = getPreferences(MODE_PRIVATE);
staticClass = this;

        PrefManager prf= new PrefManager(getApplicationContext());

        if (prf.getString(key_nocode).toString().equals("FALSE")){
            Intent intent= new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        initView();
        initAction();
        customView();
        FaceookSignIn();
        GoogleSignIn();
    }


    public void initView(){
        this.sign_in_button_google   =      (SignInButton)  findViewById(R.id.sign_in_button_google);
        this.sign_in_button_facebook =      (LoginButton)   findViewById(R.id.sign_in_button_facebook);
        this.text_view_skip_login    = (TextView) findViewById(R.id.text_view_skip_login);

    }
    public void initAction(){
        this.sign_in_button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        this.text_view_skip_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        this.text_view_skip_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void customView(){
        sign_in_button_google = (SignInButton) findViewById(R.id.sign_in_button_google);
        sign_in_button_google.setSize(SignInButton.SIZE_STANDARD);
        TextView textView = (TextView) sign_in_button_google.getChildAt(0);
        textView.setText(getResources().getString(R.string.login_gg_text));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //getResultGoogle(result);
            ManageResult(result);
        }
    }
    public void GoogleSignIn(){
      CheckUser();
    }
    public void FaceookSignIn(){
        sign_in_button_facebook.setReadPermissions(Arrays.asList("public_profile"));
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        sign_in_button_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        //getResultFacebook(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();

            }

            @Override
            public void onError(FacebookException exception) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
            }
        });
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }



    @Override
    public void onPause(){
        super.onPause();
    }

    // ================================================================= Google SigIn ========================================================================= //
    // ======================================================================================================================================================== //

    private void CheckUser() {
       // prefM = getPreferences(MODE_PRIVATE);

        // ============ Chequeo si ya esta registrado ================ //
        if(prefM.getInt(IntroActivity.key_st, 0) == 0) {
           /* DBConnect dbConnect = new DBConnect(this);

            dbConnect.execute(getString(R.string.link_DDBB));
*/


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();



        }else{
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Activar(true);
        }

    }
    public void Activar(boolean activ) {

    if(!activ) {

        enter();
    }else{
        SplashActivity.sharedPreferences.edit().putInt(RequestCodeActivity.key_lest, 1).commit();
        Toast.makeText(this, "Ya estas activado!", Toast.LENGTH_LONG).show();
        enter();
    }
  }

    public void enter(){
       // Log.e("MAIN", "enter: LISTO");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(LoginActivity.this,PermissionActivity.class);
               // Log.e("MAIN", "enter: intent listo");
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }else{
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        }else{
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }
    }


    public void SendToDatabase(final String[] nameExct, final String email, final String deviceId){
        Log.e("MAIN", "SendToDatabase: "+deviceId);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_post);

        StringRequest request = new StringRequest(StringRequest.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("MAIN", "onResponse: "+s);
                Intent i = new Intent(getApplicationContext(), RequestCodeActivity.class);

                startActivity(i);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("MAIN", "onErrorResponse: "+volleyError.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("usr", nameExct[0]);
                params.put("em_ail", email);
                params.put("sun", nameExct[1]);
                params.put("prov", deviceId);

                return params;
            }
        };


        queue.add(request);
    }


    private void ManageResult(GoogleSignInResult re){
        staticClass = this;
        Log.e("MAIN", "ManageResult: "+re.getSignInAccount().getId());
        // Log.e("MAIN", "Size: "+userModels.size());
        emailActual = re.getSignInAccount().getEmail();



        final String email = re.getSignInAccount().getEmail();
        final String name = re.getSignInAccount().getDisplayName();
        final String[] nameExct =  name.split(" ");
        final String provi = "l"+re.getSignInAccount().getId();
        // Log.e("MAIN", "ManageResult: "+name);


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_get);

        StringRequest request = new StringRequest(StringRequest.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Log.e("MAIN", "onResponse: "+s);
                String data = s;

//                String jsonObject = JsonUtils.getJSONString(data);

                if(!s.equals("[]")) {
                    try {

                        JSONObject json = new JSONObject(s);

                        JSONArray jsonArray = json.getJSONArray("AllUsers");

                        //Log.e("MAIN", "onResponse: " + jsonArray.length());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objectMain = jsonArray.getJSONObject(i);
                            UserModel model = new UserModel();
                            boolean actv = (Integer.parseInt(objectMain.getString("active")) == 1);
                            model.setActiveit(actv);
                            model.setEmail(objectMain.getString("email"));
                            model.setName(objectMain.getString("name") + " " + objectMain.getString("surname"));


                            if(model.isActiveit()){
                                Activar(model.isActiveit());

                            }else {
                                Intent inte = new Intent(getApplicationContext(), RequestCodeActivity.class);

                                startActivity(inte);
                            }
                            userModels.add(model);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {

                    SendToDatabase(nameExct, email, provi);
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("MAIN", "onErrorResponse: "+volleyError.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("mmei", email);
                return params;
            }
        };


        queue.add(request);

    }

    public void ActivateInDataBase(final String mmeil){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.url_active);

        StringRequest request = new StringRequest(StringRequest.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("MAIN", "onErrorResponse: "+volleyError.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("em_ail", mmeil);

                return params;
            }
        };

        queue.add(request);
    }

    // ================================================================================================================================ //


/*
    public void signUp(String username,String password,String name,String type,String image){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.register(name,username,password,type,image);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    if (response.body().getCode()==200){

                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
                        String image_user="x";
                        String enabled="x";
                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("salt")){
                                salt_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")){
                                name_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")){
                                type_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")){
                                username_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")){
                                image_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")){
                                enabled=response.body().getValues().get(i).getValue();
                            }
                        }if (enabled.equals("true")){
                            PrefManager prf= new PrefManager(getApplicationContext());
                            prf.setString("ID_USER",id_user);
                            prf.setString("SALT_USER",salt_user);
                            prf.setString("TOKEN_USER",token_user);
                            prf.setString("NAME_USER",name_user);
                            prf.setString("TYPE_USER",type_user);
                            prf.setString("USERN_USER",username_user);
                            prf.setString("IMAGE_USER",image_user);
                            prf.setString("LOGGED","TRUE");
                            String  token = FirebaseInstanceId.getInstance().getToken();
                            updateToken(Integer.parseInt(id_user),token_user,token);


                        }else{
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    if (response.body().getCode()==500){
                        Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                    }
                }else{
                    Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }
    public void updateToken(Integer id,String key,String token){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.editToken(id,key,token);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){

                    Toasty.success(getApplicationContext(),response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    register_progress.dismiss();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
               // Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
                finish();
            }
        });
    }

    private void getResultGoogle(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
            if (acct.getPhotoUrl()!=null){
                photo =  acct.getPhotoUrl().toString();
            }

            signUp(acct.getId().toString(),acct.getId(), acct.getDisplayName().toString(),"google",photo);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        } else {

        }
    }

    private void getResultFacebook(JSONObject object){
        Log.d(TAG, object.toString());
        try {
            signUp(object.getString("id").toString(),object.getString("id").toString(),object.getString("name").toString(),"facebook",object.getJSONObject("picture").getJSONObject("data").getString("url"));
            LoginManager.getInstance().logOut();        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */
}

