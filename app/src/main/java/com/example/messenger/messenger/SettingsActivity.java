package com.example.messenger.messenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;


public class SettingsActivity extends Activity {

    private SharedPreferences.Editor editor;
    private Context context;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this;

        String [] settings = new String [] {"Information", "Tell a Friend", "Account", "About"} ;

        ListView listView = (ListView) findViewById(R.id.settingsLv);

        listView.setAdapter(new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_list_item_1, settings));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text

                if(position == 0){
                    final CharSequence[] items = {"List of Q&A", "Contact Us"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    //builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("Account Options!");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            // Do something with the selection
                            if(item == 0){
                                Toast.makeText(getApplicationContext(),
                                        "Under Construction...", Toast.LENGTH_LONG)
                                        .show();
                            }

                            if(item == 1){
                                Toast.makeText(getApplicationContext(),
                                        "Under Construction...", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                if(position == 1){
                    Toast.makeText(getApplicationContext(),
                            "Under Construction...", Toast.LENGTH_LONG)
                            .show();
                }

                if(position == 2){
                    final CharSequence[] items = {"Delete Account", "Change Password"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    //builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("Account Options!");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            // Do something with the selection
                            if(item == 0){
                                Toast.makeText(getApplicationContext(),
                                        "Under Construction...", Toast.LENGTH_LONG)
                                        .show();
                            }

                            if(item == 1){

                                if(((Network) getApplication()).connection.isConnected()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    LinearLayout lila1= new LinearLayout(context);
                                    lila1.setOrientation(LinearLayout.VERTICAL);
                                    final EditText input = new EditText(context);
                                    input.setHint("Type your old passwod");
                                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    final EditText input1 = new EditText(context);
                                    input1.setHint("Type your new passwod");
                                    input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    final EditText input2 = new EditText(context);
                                    input2.setHint("Confirm your new passwod");
                                    input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    lila1.addView(input);
                                    lila1.addView(input1);
                                    lila1.addView(input2);
                                    builder.setTitle("Change Password");
                                    //builder.setIcon(R.drawable.ic_launcher);
                                    builder.setView(lila1);

                                    // Set up the buttons
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if((input.getText().toString().equals(LoginActivity.sharedPref.getString("password", "default"))) && (input1.getText().toString().equals(input2.getText().toString())) ){

                                                new Thread(new Runnable() {
                                                    public void run() {
                                                        try {
                                                            AccountManager am = AccountManager.getInstance(((Network) getApplication()).connection);
                                                            am.sensitiveOperationOverInsecureConnection(true);
                                                            am.changePassword(input1.getText().toString());
                                                            editor = LoginActivity.sharedPref.edit();
                                                            editor.putString("password", input1.getText().toString());
                                                            editor.commit();

                                                            mHandler.post(new Runnable() {
                                                                public void run() {
                                                                    Toast.makeText(context,
                                                                            "Password was changed successfully", Toast.LENGTH_LONG)
                                                                            .show();
                                                                }
                                                            });
                                                        } catch (XMPPException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        } catch (SmackException.NotConnectedException e) {
                                                            e.printStackTrace();
                                                        } catch (SmackException.NoResponseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();
                                            }else{
                                                Toast.makeText(SettingsActivity.this,
                                                        "Passwords do not match...", Toast.LENGTH_LONG)
                                                        .show();
                                            }

                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();
                                }else{
                                    Toast.makeText(SettingsActivity.this,
                                            "Please wait for reconnection", Toast.LENGTH_LONG)
                                            .show();
                                }


                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                if(position == 3){
                    Toast.makeText(getApplicationContext(),
                            "Under Construction...", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
