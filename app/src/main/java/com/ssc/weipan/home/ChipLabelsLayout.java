package com.ssc.weipan.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.WrapLabelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujj on 17-10-21.
 */
public class ChipLabelsLayout extends WrapLabelLayout<String> {


    public OnChipSelected mListener;

    public ChipLabelsLayout(Context context) {
        super(context);
    }

    public ChipLabelsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChipLabelsLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private List<View> toggles = new ArrayList<View>();

    @Override
    public View getItemView(final String s) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View itemRoot = inflater.inflate(R.layout.trade_buy_chip_item, null, false);
        TextView chip = CommonUtils.findView(itemRoot, R.id.chip);
        chip.setText(s + "元");


        toggles.add(itemRoot);
        itemRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChipItem(s, v, false);
            }
        });

        return itemRoot;
    }



    public void selectChipItem(String chip, View v, boolean fromCoupon) {

        for(View view : toggles) {
            view.setSelected(view == v);
            view.findViewById(R.id.chip).setSelected(view == v);

            if (view == v && mListener != null) {
                mListener.onChipSelected(chip, fromCoupon);
            }
        }

        if (fromCoupon && mListener != null) {
            mListener.onChipSelected(chip, fromCoupon);
        }
    }


    public static interface OnChipSelected {
        public void onChipSelected(String chip, boolean fromCoupon);
    }
    public void setOnChipSelected(OnChipSelected listener) {

        mListener = listener;
    }
}
