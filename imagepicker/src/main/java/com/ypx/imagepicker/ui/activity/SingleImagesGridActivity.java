package com.ypx.imagepicker.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.ui.ImagesGridFragment;


public class SingleImagesGridActivity extends FragmentActivity {
    ImagesGridFragment mFragment;
    View v_masker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        setContentView(R.layout.ipk_activity_images_grid);
        TextView mBtnOk = (TextView) findViewById(R.id.btn_ok);
        v_masker = findViewById(R.id.v_masker);
        mBtnOk.setVisibility(View.GONE);


        findViewById(R.id.btn_backpress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFragment = new ImagesGridFragment();
        mFragment.setOnImageItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("imageItem", (ImageItem) parent.getTag());
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFragment!=null){
            mFragment.hideCamera();
            mFragment.setShouldSelectSingle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showMasker() {
        if (v_masker != null) {
            v_masker.setVisibility(View.VISIBLE);
        }
    }

    public void hideMasker() {
        if (v_masker != null) {
            v_masker.setVisibility(View.GONE);
        }
    }
}
