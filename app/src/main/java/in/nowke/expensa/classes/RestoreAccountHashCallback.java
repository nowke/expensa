package in.nowke.expensa.classes;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;

import java.util.List;

/**
 * Created by nav on 6/9/15.
 */
public class RestoreAccountHashCallback implements ResultCallback<DriveApi.DriveContentsResult> {

    public  String accountHash;
    public RestoreAccountHashCallback(String accountHash) {
        this.accountHash = accountHash;
    }
    @Override
    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {

    }

}
