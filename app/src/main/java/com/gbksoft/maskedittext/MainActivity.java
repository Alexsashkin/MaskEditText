package com.gbksoft.maskedittext;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.gbksoft.maskedittext.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = DataBindingUtil.setContentView(this, R.layout.activity_main);

        layout.showMaskAsHint.setChecked(layout.etMask.isShowMaskAsHint());
        layout.showMaskAsHint.setOnCheckedChangeListener((buttonView, isChecked) -> layout.etMask.setShowMaskAsHint(isChecked));

        layout.setMask.setOnClickListener(v -> {
            layout.etMask.setMask(layout.etPlaceholder.getText().toString());
            if (!TextUtils.isEmpty(layout.etCharRepresentation.getText())) {
                layout.etMask.setCharRepresentation(layout.etCharRepresentation.getText().charAt(0));
            } else {
                layout.etMask.setDefaultCharRepresentation();
            }
        });
    }

}
