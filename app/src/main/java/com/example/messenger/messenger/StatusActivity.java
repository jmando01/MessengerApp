package com.example.messenger.messenger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class StatusActivity extends Activity {

    private EditText currentStatus;
    private SharedPreferences.Editor editor;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        currentStatus = (EditText) findViewById(R.id.currentStatus);
        currentStatus.setText(LoginActivity.sharedPref.getString("status", "Available"));

        String [] statusList = new String [] {"Available", "Busy", "At the phone", "At Work", "Driving"} ;

        ListView listView = (ListView) findViewById(R.id.statuslv);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statusList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text

                status = (String) parent.getItemAtPosition(position);
                if(((Network) getApplication()).connection.isConnected()){
                    new ChangeStatus().execute();
                }else{
                    Toast.makeText(getApplication(),
                            "Please wait for reconnection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    class ChangeStatus extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog pDialog = new ProgressDialog(StatusActivity.this);
        private boolean success = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           // pDialog.setMessage("Changing Status...");
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            // TODO Auto-generated method stub
            if(((Network) getApplication()).connection.isConnected()){
                Log.d("LoginActivity", "connectedOK");
                success = ((Network) getApplication()).setPresence(status);;
            }else{
                //No network available
                Log.d("LoginActivity", "No Network Available");
                return false;
            }
            return success;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean success) {
            // dismiss the dialog once product deleted
            // pDialog.dismiss();
            if(success){
                currentStatus.setText(status);
                editor = LoginActivity.sharedPref.edit();
                editor.putString("status", status);
                editor.commit();
                Toast.makeText(getApplicationContext(),
                        "Your status has been updated", Toast.LENGTH_LONG)
                        .show();
            }else{
                ((Network) getApplication()).showAlertDialog("Notification!", "Error changing status. Verify your Internet connection and try again.", StatusActivity.this);
            }
        }
    }

    public void changeStatusBtn(View view){
        status = currentStatus.getText().toString();
        if(((Network) getApplication()).connection.isConnected()){
            new ChangeStatus().execute();
        }else{
            Toast.makeText(getApplication(),
                    "Please wait for reconnection", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
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
