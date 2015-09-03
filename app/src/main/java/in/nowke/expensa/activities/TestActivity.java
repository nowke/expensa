package in.nowke.expensa.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.classes.AccountToJson;


public class TestActivity extends AppCompatActivity {

    private TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testTextView = (TextView) findViewById(R.id.testTextView);
        AccountDBAdapter helper = new AccountDBAdapter(this);
        List<Integer> accountIds = helper.getAllAccountIds();
        String fullJson = "";
        try {
//            AccountToJson accountToJson = new AccountToJson(this, 2);
//            testTextView.setText(accountToJson.getJsonString());

            for (int accountId: accountIds) {
                AccountToJson accountToJson = new AccountToJson(this, accountId);
                fullJson += "\n" + accountToJson.getJsonString();
            }
            testTextView.setText(fullJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
