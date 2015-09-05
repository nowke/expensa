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
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import junit.framework.Test;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import in.nowke.expensa.BaseActivity;
import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.classes.ApiClientAsyncTask;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.classes.UploadDriveCallback;
import in.nowke.expensa.sync.AccountToJson;
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

    }

    public void uploadDrive(View view) {

        final String[] allAccountHashes = {""};
        final int[] accountSize = {accountIds.size()};

        for (int accountId: accountIds) {
            Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new UploadDriveCallback(accountId) {
                @Override
                public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                    if (!driveContentsResult.getStatus().isSuccess()) {
                        Message.message(TestActivity.this, "Error uploading");
                        return;
                    }
                    final DriveContents driveContents = driveContentsResult.getDriveContents();
                    new Thread() {
                        @Override
                        public void run() {
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                AccountToJson accountToJson = new AccountToJson(TestActivity.this, accountId);
                                writer.write(accountToJson.getJsonString());
                                writer.close();

                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(accountToJson.getAccountHash() + ".json")
                                        .setMimeType("application/json")
                                        .build();
                                Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                        .createFile(mGoogleApiClient, changeSet, driveContents)
                                        .setResultCallback(fileCallback);
                                if (--accountSize[0] == 0) {
                                    allAccountHashes[0] += accountToJson.getAccountHash();
                                } else {
                                    allAccountHashes[0] += accountToJson.getAccountHash() + "\r\n";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                Log.e("Drive", e.getMessage());
                            }
                        }
                    }.start();
                }
            });
        }

        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    return;
                }
                final DriveContents driveContents = driveContentsResult.getDriveContents();
                new Thread() {
                    @Override
                    public void run() {
                        OutputStream outputStream = driveContents.getOutputStream();
                        Writer writer = new OutputStreamWriter(outputStream);
                        try {
                            writer.write(allAccountHashes[0]);
                            writer.close();
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("accountlist.txt")
                                    .setMimeType("text/plain")
                                    .build();
                            Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    public void restoreDrive(View view) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(restoreDriveCallback);
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
        ResultCallback<DriveFolder.DriveFileResult>() {
            @Override
            public void onResult(DriveFolder.DriveFileResult result) {
                if (!result.getStatus().isSuccess()) {
                    Message.message(TestActivity.this, "Error while trying to create the file");
                    return;
                }
                Message.message(TestActivity.this,"Successfully uploaded!");

            }
        };

    final private ResultCallback<DriveApi.DriveContentsResult> restoreDriveCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                    if (!driveContentsResult.getStatus().isSuccess()) {
                        Message.message(TestActivity.this, "Error connecting!");
                        return;
                    }
                    final DriveContents driveContents = driveContentsResult.getDriveContents();

                    new Thread() {
                        @Override
                        public void run() {
                            Query query = new Query.Builder()
                                    .addFilter(Filters.eq(SearchableField.TITLE, "accountlist.txt"))
                                    .build();
                            Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                    .queryChildren(mGoogleApiClient, query)
                                    .setResultCallback(metadataCallback);
                        }
                    }.start();
                }
            };


        /*
        JUNK ***************************************************************************************************************
         */

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
            testTextView.setText(result);
//            try {
//                JsonToAccount jsonToAccount = new JsonToAccount(TestActivity.this, result);
//                jsonToAccount.writeAccountToDb();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

}
