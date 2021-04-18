package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.MyItem;
import com.example.myapplication.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Set;


// 커스텀 클러스터 마커
public class MarkerRenderer extends DefaultClusterRenderer<MyItem> {
    public MarkerRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    public void onClustersChanged(Set<? extends Cluster<MyItem>> clusters) {
        super.onClustersChanged(clusters);
        //onCameraIdle();
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        switch (item.getStoreIconNum()){
            case 21:{
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe));
                break;
            }
            case 23:{
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flower));
                break;
            }
            default:{
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_xingxing));
                break;
            }
        }

    }

    @Override
    public void setOnClusterClickListener(ClusterManager.OnClusterClickListener<MyItem> listener) {
        super.setOnClusterClickListener(listener);
        Log.d("Cluster", "FFFF");
    }
}