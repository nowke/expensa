package in.nowke.expensa.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import in.nowke.expensa.BaseActivity;
import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.classes.ApiClientAsyncTask;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.sync.JsonToAccount;


public class TestActivity extends BaseActivity {

    private TextView testTextView;
    List<Integer> accountIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        testTextView = (TextView) findViewById(R.id.testTextView);
        AccountDBAdapter helper = new AccountDBAdapter(this);
        accountIds = helper.getAllAccountIds();
//        String fullJson = "";
//        try {
//

//            testTextView.setText(fullJson);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }

    public void uploadDrive(View view) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
    }

        final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Message.message(TestActivity.this, "Error while trying to create new file contents");
                        return;
                    }

                    final DriveContents driveContents = result.getDriveContents();
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            OutputStream outputStream = driveContents.getOutputStream();
//                            Writer writer = new OutputStreamWriter(outputStream);
//                            try {
//                                AccountToJson accountToJson = null;
//                                for (int accountId: accountIds) {
//                                    try {
//                                         accountToJson = new AccountToJson(TestActivity.this, accountId);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                }
//
//
//                                try {
//                                    writer.write(accountToJson.getJsonString());
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                writer.close();
//                            } catch (IOException e) {
//                                Log.e("Drive", e.getMessage());
//                            }
//
//
//                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                                    .setTitle("New file")
//                                    .setMimeType("text/plain")
//                                    .build();
//
//                            Drive.DriveApi.getAppFolder(mGoogleApiClient)
//                                    .createFile(mGoogleApiClient, changeSet, driveContents)
//                                    .setResultCallback(fileCallback);
//
//                        }
//                    }.start();
                    new Thread() {
                        @Override
                        public void run() {
                            Query query = new Query.Builder()
                                    .addFilter(Filters.eq(SearchableField.TITLE, "New file"))
                                    .build();
                            Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                    .queryChildren(mGoogleApiClient, query)
                                    .setResultCallback(metadataCallback);
                        }
                    }.start();

                }
            };

//        final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
//            ResultCallback<DriveFolder.DriveFileResult>() {
//                @Override
//                public void onResult(DriveFolder.DriveFileResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        Message.message(TestActivity.this, "Error while trying to create the file");
//                        return;
//                    }
//                    Message.message(TestActivity.this, "Created a file in App Folder: "
//                            + result.getDriveFile().getDriveId());
//
//                }
//            };

        final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Message.message(TestActivity.this, "Problem while retrieving results");
                        return;
                    }
                    MetadataBuffer metadata = result.getMetadataBuffer();
                    if (metadata.getCount() > 0) {
                        DriveId driveID = metadata.get(0).getDriveId();
                        new RetrieveDriveFileContentsAsyncTask(TestActivity.this).execute(driveID);
                    }
                }
            };

    final private class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackgroundConnected(DriveId... params) {
            String contents = null;
            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, params[0]);
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(driveContents.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                contents = builder.toString();
            } catch (IOException e) {
                Log.e("Drive", "IOException while reading from the stream", e);
            }

            driveContents.discard(mGoogleApiClient);
            return contents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                Message.message(TestActivity.this, "Error while reading from the file");
                return;
            }
//            Message.message(TestActivity.this, "File contents: " + result);
            try {
                JsonToAccount jsonToAccount = new JsonToAccount(TestActivity.this, result);
                jsonToAccount.writeAccountToDb();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
