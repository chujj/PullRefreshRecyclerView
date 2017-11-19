package com.biaoyixin.shangcheng.api.shangcheng;

import com.biaoyixin.shangcheng.model.BaseModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by zhujj on 17-11-18.
 */
public class ShangChengAPI {


    public static interface  IShangCheng {
        @GET("/shop/index")
        public void index(Callback<ShangChengResp> cb);

        @FormUrlEncoded
        @POST("/shop/goods_details_list")
        void category(@Field("categoryId") int categoryId , Callback<CategoryResp> cb);

        @FormUrlEncoded
        @POST("/shop/goods_details_image")
        void detail(@Field("goodsDetailsId") int goodsDetailsId, Callback<DetailResp> cb);


        @FormUrlEncoded
        @POST("/shop/delivery_preview")
        void order(@Field("goodsDetailsId") int goodsDetailsId, Callback<OrderResp> cb);

        @FormUrlEncoded
        @POST("/shop/delivery_address_edit")
        void changeTihuoAddr(
                @Field("province") String province,
                @Field("city") String city,
                @Field("name") String name,
                @Field("mobile") String mobile,
                @Field("detail") String detail,
                Callback<BaseModel> cb
        );
    }

    public static class ShangChengResp extends BaseModel {
        public ShangChengInfo data;
    }

    public static class ShangChengInfo extends BaseModel {
        public Banner banner;
        public List<Category> category;
        public List<Item> recommendList;
    }

    public static class Banner extends BaseModel {
        public String icon;
        public String title;
        public String url;
    }

    public static class Category extends BaseModel {
        public String icon;
        public String title;
        public int categoryId;
    }

    public static class Item extends BaseModel {
        public String icon;
        public String name;
        public int goodsDetailsId;
        public float realPrice;
        public float deliverPrice;
        public List<String> images;
    }



    public static class CategoryResp extends BaseModel {
        public CategoryInfo data;
    }


    public static class CategoryInfo extends BaseModel {
        public String image;
        public String title;
        public List<Item> list;
    }



    public static class DetailResp extends BaseModel {

        public Item data;
    }


    public static class OrderResp extends BaseModel {
        public OrderInfo data;
    }


    public static class OrderInfo extends BaseModel {
        public Address address;
        public int credit;
        public int deliverPrice;
        public String icon;
        public String name;
        public int realPrice;
    }

    public static class  Address  extends BaseModel {
        public String city;
        public String detail;
        public String mobile;
        public String name;
        public String province;
    }

}
