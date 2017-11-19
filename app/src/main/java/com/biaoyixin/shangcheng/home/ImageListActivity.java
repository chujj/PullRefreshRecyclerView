package com.biaoyixin.shangcheng.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-11-19.
 */
public class ImageListActivity extends BaseActivity {


    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.images_container)
    ViewGroup mImagesContainer;

    private String[] mImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImages = getIntent().getStringArrayExtra("images");


        this.setContentView(R.layout.image_list_activity);

        ButterKnife.bind(this, this);


        mTopbar.setTitle(getIntent().getStringExtra("title"));
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (int i = 0; i < mImages.length; i++) {
            if (mImages[i].startsWith("assets://")) {
                loadFromAssets(mImages[i].substring("assets://".length()));
            }

        }
    }

    private void loadFromAssets(String substring) {
        final ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(0xffff0000);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        try {
            final int screenWidth = getResources().getDisplayMetrics().widthPixels;

            Bitmap bitmap = BitmapFactory.decodeStream(
                    getResources().getAssets().open(substring));

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int computedHeight = (int) (1f * height / width * screenWidth);
            lp.height = computedHeight;

            imageView.setImageBitmap(bitmap);
            mImagesContainer.addView(imageView, lp);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
