package com.example.administrator.mapper.present;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.administrator.mapper.R;
import com.example.administrator.mapper.adapter.UsageAdapter;
import com.example.administrator.mapper.contact.UsageContract;
import com.example.administrator.mapper.entity.Usage;
import com.example.administrator.mapper.model.UsageModel;
import com.example.administrator.mapper.weight.CircleImageView;
import com.google.gson.Gson;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/6/25.
 */

public class UsagePresenter implements UsageContract.Presenter {
    private static final String TAG = "EntityPresenter";
    private final UsageModel mModel = new UsageModel();
    private final UsageContract.View mView;
    private Context context;
    private RecyclerView recyclerView;
    private BitmapDescriptor bitmapDescriptor;
    private AMap map;

    public UsagePresenter(UsageContract.View mView, Context context, AMap map) {
        this.mView = mView;
        this.context = context;
        this.map = map;
    }

    public UsagePresenter(UsageContract.View mView, Context context, RecyclerView recyclerView) {
        this.mView = mView;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public void showList() {
        mModel.getEntityList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Usage>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: 请求完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e);
                    }

                    @Override
                    public void onNext(Usage usage) {
                        Log.d(TAG, "onNext() called with: usage = [" + usage + "]" + usage.getData().get(0).getContent());
                        UsageAdapter adapter = new UsageAdapter(context, usage.getData());
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    @Override
    public void addMarkers() {
        mModel.addMarkers().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Usage>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "marker: 请求完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e);
                    }

                    @Override
                    public void onNext(Usage usage) {
                        System.out.println("Makeradd");
                        if (usage.isIssuccess()) {
                            List<Usage.DataBean> data = usage.getData();
                            for (int i = 0; i < data.size(); i++) {
                                addMarker(data.get(i));
                                System.out.println(data.get(i).getLongitude()+"经度");
                            }
                        }
                    }
                });
    }

    /**
     * func:view转bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * func:定制化marker的图标
     *
     * @param url
     * @param listener
     */
    private void customizeMarkerIcon(String url, final OnMarkerIconLoadListener listener) {
        final View markerView = LayoutInflater.from(context).inflate(R.layout.map_location_marker, null);
        final CircleImageView icon = (CircleImageView) markerView.findViewById(R.id.img_user_head);
        Glide.with(context)
                .load(url)
                .asBitmap()
                .thumbnail(0.2f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        //待图片加载完毕后再设置bitmapDes
                        icon.setImageBitmap(bitmap);
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(markerView));
                        listener.markerIconLoadingFinished(markerView);
                    }
                });
    }

    public interface OnMarkerIconLoadListener {
        void markerIconLoadingFinished(View view);
    }

    /**
     * 解析数据添加到地图上面
     *
     * @param bean
     */
    private void addMarker(final Usage.DataBean bean) {
        double lon = bean.getLongitude();
        double lat = bean.getLatitude();
        LatLng latLng = new LatLng(lat, lon);
        String url = bean.getUserhead();
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        customizeMarkerIcon(url, new OnMarkerIconLoadListener() {
            @Override
            public void markerIconLoadingFinished(View view) {
                markerOptions.icon(bitmapDescriptor);
                map.addMarker(markerOptions);
            }
        });

    }

}