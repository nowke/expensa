package in.nowke.expensa;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.plus.Plus;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.classes.ApiClientAsyncTask;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.classes.RestoreAccountHashCallback;
import in.nowke.expensa.classes.UploadDriveCallback;
import in.nowke.expensa.classes.Utilities;
import in.nowke.expensa.entity.AccountDetail;
import in.nowke.expensa.fragments.HomeFragment;
import in.nowke.expensa.sync.AccountToJson;
import in.nowke.expensa.sync.JsonToAccount;

/**
 * Created by nav on 5/9/15.
 */
public class BaseActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void addAccount(int accountId) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new UploadDriveCallback(accountId) {
            @Override
            public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Message.message(BaseActivity.this, "Error uploading");
                    return;
                }

                final DriveContents driveContents = driveContentsResult.getDriveContents();
                new Thread() {
                    @Override
                    public void run() {
                        OutputStream outputStream = driveContents.getOutputStream();
                        Writer writer = new OutputStreamWriter(outputStream);
                        try {
                            AccountToJson accountToJson = new AccountToJson(BaseActivity.this, accountId);
                            Log.e("Drive", accountToJson.getJsonString());
                            writer.write(accountToJson.getJsonString());
                            writer.close();

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(accountToJson.getAccountHash() + ".json")
                                    .setMimeType("application/json")
                                    .build();
                            Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);

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

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Message.message(BaseActivity.this, "Error while trying to create the file");
                        return;
                    }
//                    Message.message(BaseActivity.this,"Successfully uploaded!");

                }
            };


    protected void addAccountHash() {
        AccountDBAdapter helper = new AccountDBAdapter(this);
        List<Integer> accountIds = helper.getAllAccountIds();
        String accountHashes = "";
        for (int accountId: accountIds) {
            try {
                AccountToJson accountToJson = new AccountToJson(BaseActivity.this, accountId);
                accountHashes += accountToJson.getAccountHash();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final String finalAccountHashes = accountHashes;
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
                            writer.write(finalAccountHashes);
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

    protected void getAllAccountHashes() {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(fetchAccountListFromDriveCallback);
    }

    final private ResultCallback<DriveApi.DriveContentsResult> fetchAccountListFromDriveCallback =
            new ResultCallback<DriveApi.DriveContentsResult> () {
                @Override
                public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                    if (!driveContentsResult.getStatus().isSuccess()) {
                        Message.message(BaseActivity.this, "Error connecting!");
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

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Message.message(BaseActivity.this, "Problem while retrieving results");
                        return;
                    }
                    MetadataBuffer metadata = result.getMetadataBuffer();
                    if (metadata.getCount() > 0) {
                        DriveId driveID = metadata.get(0).getDriveId();
                        new ReadAccountListDriveAsyncTask(BaseActivity.this).execute(driveID);
                    }
                    metadata.release();
                }
            };


    final private class ReadAccountListDriveAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {

        private Context context;

        public ReadAccountListDriveAsyncTask(Context context) {
            super(context);
            this.context = context;
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
                Message.message(context, "Error while reading from the file");
                return;
            }
            List<String> retrievedAccountIds = Utilities.splitEqually(result, 36);

            for (String accountHash: retrievedAccountIds) {
                Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new RestoreAccountHashCallback(accountHash) {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        if (!driveContentsResult.getStatus().isSuccess()) {
//                            Message.message(BaseActivity.this, "Error connecting!");
                            return;
                        }
                        final DriveContents driveContents = driveContentsResult.getDriveContents();

                        new Thread() {
                            @Override
                            public void run() {
                                Query query = new Query.Builder()
                                        .addFilter(Filters.eq(SearchableField.TITLE, accountHash + ".json"))
                                        .build();
                                Drive.DriveApi.getAppFolder(mGoogleApiClient)
                                        .queryChildren(mGoogleApiClient, query)
                                        .setResultCallback(accountMetadataCallback);
                            }
                        }.start();
                    }
                });
            }

        }
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> accountMetadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Message.message(BaseActivity.this, "Problem while retrieving results");
                        return;
                    }
                    MetadataBuffer metadata = result.getMetadataBuffer();
                    if (metadata.getCount() > 0) {
                        DriveId driveID = metadata.get(0).getDriveId();
                        new ReadAccountDriveAsyncTask(BaseActivity.this).execute(driveID);
                    }
                    metadata.release();
                }
            };


    final private class ReadAccountDriveAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {
        private Context context;
        public ReadAccountDriveAsyncTask(Context context) {
            super(context);
            this.context = context;
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
                Message.message(context, "Error while reading from the file");
                return;
            }
            AccountDBAdapter helper = new AccountDBAdapter(context);
            try {
                JsonToAccount jsonToAccount = new JsonToAccount(context, result);
                jsonToAccount.writeAccountToDb();
                AccountDetail accountDetail = jsonToAccount.getAccountDetailObject();
                HomeFragment.adapter.add(accountDetail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
