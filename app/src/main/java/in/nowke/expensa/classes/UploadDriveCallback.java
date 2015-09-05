package in.nowke.expensa.classes;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;

/**
 * Created by nav on 6/9/15.
 */
public class UploadDriveCallback implements ResultCallback<DriveApi.DriveContentsResult> {

    public  int accountId;
    public UploadDriveCallback(int accountId) {
        this.accountId = accountId;
    }
    @Override
    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {

    }
}
