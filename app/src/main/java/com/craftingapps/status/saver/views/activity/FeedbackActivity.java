package com.craftingapps.status.saver.views.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.craftingapps.status.saver.R;
import com.craftingapps.status.saver.databinding.ActivityFeedbackBinding;


public class FeedbackActivity extends AppCompatActivity {

    private ActivityFeedbackBinding binding;
    private final String TAG = FeedbackActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);
        binding.setListener(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //  AnalyticsManager.getInstance().sendAnalytics(TAG, "Activity opened");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }

    public void onSubmitClicked() {
        // AnalyticsManager.getInstance().sendAnalytics(TAG, "Feedback Submit clicked");
        if (!binding.design.isChecked() && !binding.functionality.isChecked() && !binding.performance.isChecked() && binding.feedback.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.provide_valid_feedback), Toast.LENGTH_SHORT).show();
        } else {
            Intent Email = new Intent(Intent.ACTION_SEND);
            Email.setType("text/email");
            Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"craftingapps05@gmail.com"});
            String subject = getString(R.string.feedback);
            subject += " (" + getPackageName() + ")";
            Email.putExtra(Intent.EXTRA_SUBJECT, subject);
            String extra = "";
            if (binding.design.isChecked()) {
                extra += getString(R.string.improve_design);
            }
            if (binding.functionality.isChecked()) {
                extra += getString(R.string.improve_functionality);
            }
            if (binding.performance.isChecked()) {
                extra += getString(R.string.improve_performance);
            }
            if (binding.feedback.getText().toString().trim().length() > 0) {
                extra += getString(R.string.personal_feedback);
                extra += binding.feedback.getText().toString();
            }
            Email.putExtra(Intent.EXTRA_TEXT, extra);
            startActivity(Intent.createChooser(Email, getString(R.string.select_app)));
        }
    }
}
