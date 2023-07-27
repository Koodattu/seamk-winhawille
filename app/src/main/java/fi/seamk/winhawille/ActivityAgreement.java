package fi.seamk.winhawille;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Juha Ala-Rantala on 26.2.2018.
 */

public class ActivityAgreement extends AppCompatActivity {

    @BindView(R.id.button_accept_terms)
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        ButterKnife.bind(this);

        setTitle(getString(R.string.agreement));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences("ApplicationPreferences", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("agreementComplete", true);
                editor.apply();
                finish();
            }
        });
    }
}
