package com.gdgistanbul.firebasecodelab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.gdgistanbul.firebasecodelab.models.Message;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ValueEventListener, View.OnClickListener, TextWatcher {

    // Firebase instance degisken tanimlari
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static final String ANONYMOUS = "anonymous";
    public static GoogleApiClient mGoogleApiClient;
    public static final int RC_SIGN_IN = 9001;
    private String TAG = this.getClass().getName();
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String mUsername;
    private ListView messageListView;
    private EditText mMessageEditText;

    private Button mSendButton;

    private MessageAdapter messageAdapter = new MessageAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        //toplu notf. icin konu basligi
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        //Notf. alabilmeniz icin cihazin idsi
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, token);

        messageListView = (ListView) findViewById(R.id.messageListView);
        messageListView.setAdapter(messageAdapter);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("messages");
        // databaseden sirali okuma islemi
        myRef.orderByChild("messageTime").addValueEventListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Initialize Firebase Auth
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }

        mMessageEditText.addTextChangedListener(this);
        mSendButton.setOnClickListener(this);

        //Reklam init
        //https://apps.admob.com // YOUR_ADMOB_APP_ID
        //Reklam tipi secilip, konfigurasyon yapilir.
        MobileAds.initialize(this, "YOUR_ADMOB_APP_ID");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "Failed connection." + connectionResult.getErrorMessage());

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        messageAdapter.clearAllItems();
        //tum mesajlar firebase'den cekilir.
        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
            Message value = messageSnapshot.getValue(Message.class);
            Log.d(TAG, "Value is: " + value.getData());
            messageAdapter.add(value);
        }
        messageListView.setAdapter(messageAdapter);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "Failed to read value.", databaseError.toException());

    }

    @Override
    public void onClick(View view) {
        Date now = new Date();
        Message message = new
                Message(mUsername, mMessageEditText.getText().toString(), now.getTime());
        String uniqe = myRef.push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put(uniqe, message.toMap());
        myRef.updateChildren(map);
        mMessageEditText.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //none
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().trim().length() > 0) {
            mSendButton.setEnabled(true);
        } else {
            mSendButton.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //none
    }
}
