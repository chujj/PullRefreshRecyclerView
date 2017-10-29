package com.ssc.weipan.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.ClosureMethod;
import com.ssc.weipan.base.CommonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhujj on 17-10-24.
 */
public class TradeHistoroyAdapter extends BaseAdapter {
    private List<GoodsApi.BuyTradeData> mData = new ArrayList<>();


    private Set<GoodsApi.BuyTradeData> mSelectedData = new HashSet<>();

    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_Today = 1;
    private int mType = TYPE_NORMAL;

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<GoodsApi.BuyTradeData> open) {
        mData = open;
        mSelectedData.clear();
    }


    public static void setTextColor(TextView tv, int buyUpDownType) {
        tv.setTextColor(buyUpDownType == 0 ? 0xFFF35833 : 0xFF2CB545);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trade_history_list_item, parent, false);

            final ImageView toggleImage = CommonUtils.findView(root, R.id.open_toggle);
            final View toggleContainer = CommonUtils.findView(root, R.id.toggle_container);
            final View detailContainer = CommonUtils.findView(root, R.id.detail_container);

            //headr
            final TextView type = CommonUtils.findView(root, R.id.buy_up_type);
            final TextView name = CommonUtils.findView(root, R.id.name);
            final TextView amount = CommonUtils.findView(root, R.id.amount);
            final TextView win_money = CommonUtils.findView(root, R.id.win_money);


            // detail
            final List<View[]> detailViews = new ArrayList<View[]>(){
                {
                    add(new View[] {
                            CommonUtils.findView(detailContainer, R.id.c_1),
                            CommonUtils.findView(detailContainer, R.id.l_1),
                            CommonUtils.findView(detailContainer, R.id.r_1),
                    });
                    add(new View[] {
                            CommonUtils.findView(detailContainer, R.id.c_2),
                            CommonUtils.findView(detailContainer, R.id.l_2),
                            CommonUtils.findView(detailContainer, R.id.r_2),

                    });
                    add(new View[] {
                            CommonUtils.findView(detailContainer, R.id.c_3),
                            CommonUtils.findView(detailContainer, R.id.l_3),
                            CommonUtils.findView(detailContainer, R.id.r_3),

                    });
                    add(new View[] {
                            CommonUtils.findView(detailContainer, R.id.c_4),
                            CommonUtils.findView(detailContainer, R.id.l_4),
                            CommonUtils.findView(detailContainer, R.id.r_4),

                    });
                    add(new View[] {
                            CommonUtils.findView(detailContainer, R.id.c_5),
                            CommonUtils.findView(detailContainer, R.id.l_5),
                            CommonUtils.findView(detailContainer, R.id.r_5),

                    });
                    add(new View[] {
                            CommonUtils.findView(detailContainer, R.id.c_6),
                            CommonUtils.findView(detailContainer, R.id.l_6),
                            CommonUtils.findView(detailContainer, R.id.r_6),

                    });
                    add(new View[] {
                            CommonUtils.findView(detailContainer, R.id.c_7),
                            CommonUtils.findView(detailContainer, R.id.l_7),
                            CommonUtils.findView(detailContainer, R.id.r_7),

                    });
                }
            };



            root.setTag(new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    final GoodsApi.BuyTradeData data = (GoodsApi.BuyTradeData) args[0];

                    { // header
                        type.setText(data.up_down_type == 0 ? "买涨" : "买跌");
                        type.setBackgroundResource(data.up_down_type == 0 ? R.drawable.trading_history_list_item_buy_type_bg_up
                                : R.drawable.trading_history_list_item_buy_type_bg_down);
                        setTextColor(type, data.up_down_type);

                        name.setText(data.goods_name);
                        amount.setText(data.amount + "");
                        win_money.setText(data.win_money + "");
                    }



                    { // detail

                        ((TextView)detailViews.get(0)[1]).setText("手续费");
                        ((TextView)detailViews.get(0)[2]).setText(String.format("%.2f元", data.serve_price));

                        ((TextView)detailViews.get(1)[1]).setText("建仓价格");
                        ((TextView)detailViews.get(1)[2]).setText(String.format("%.2f元", data.open_price));

                        ((TextView)detailViews.get(2)[1]).setText("平仓价格");
                        ((TextView)detailViews.get(2)[2]).setText(String.format("%.2f元", data.close_price));

                        if (mType == TYPE_Today) {
                            ((View)detailViews.get(3)[0]).setVisibility(View.GONE);
                        } else {
                            ((TextView)detailViews.get(3)[1]).setText("平仓时间");
                            ((TextView)detailViews.get(3)[2]).setText(data.close_time);
                        }

                        ((TextView)detailViews.get(4)[1]).setText("购买方式");
                        ((TextView)detailViews.get(4)[2]).setText(""  + data.pay_type); // TODO

                        ((TextView)detailViews.get(5)[1]).setText("平仓类型");
                        ((TextView)detailViews.get(5)[2]).setText("" + data.close_type); // TODO

                        ((TextView)detailViews.get(6)[1]).setText("定金");
                        ((TextView)detailViews.get(6)[2]).setText(data.chip);
                    }


                    toggleContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mSelectedData.contains(data)) {
                                mSelectedData.remove(data);
                            } else {
                                mSelectedData.add(data);
                            }
                            TradeHistoroyAdapter.this.notifyDataSetChanged();
                        }
                    });
                    toggleImage.setImageResource(mSelectedData.contains(data) ?
                            R.drawable.edu_ic_funflat_arrow_down : R.drawable.edu_ic_funflat_arrow_up);
                    detailContainer.setVisibility(mSelectedData.contains(data) ? View.VISIBLE : View.GONE);


                    return new Object[0];
                }
            });

            convertView = root;
        }



        ((ClosureMethod) convertView.getTag())
                .run(getItem(position));

        return convertView;
    }

    public void setType(int type) {
        mType = type;
    }
}
