package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Set;


// 커스텀 클러스터 마커
public class MarkerRenderer extends DefaultClusterRenderer<MyItem> {
    public final int CAFE = 21;
    public final int FURNITURE = 22;
    public final int WIG = 23;
    public final int HOMEAPPERANCE = 24;
    public final int ADD = 25;
    public final int PC = 26;
    public final int FOODCHAIN = 27;
    public final int BANK = 28;
    public final int FLOWER = 29;
    public final int STUDY = 30;
    public final int BOOK = 31;
    public final int CHICKEN = 32;
    public final int HOUSE = 33;
    public final int BUILD = 34;
    public final int CLOTH = 35;
    public final int HOSPITAL = 36;
    public final int HAIRCUT = 37;
    public final int PETHOSPITAL = 38;
    public final int DESERT = 39;
    public final int CRAFT = 40;
    public final int FROOT = 41;
    public final int MUCHINE = 42;
    public final int NAILART = 43;
    public final int LAW = 44;
    public final int ELDER = 45;
    public final int SING = 46;
    public final int BEAUTY = 47;
    public final int PRINT = 48;
    public final int BATH = 49;
    public final int BEER = 50;
    public final int WASH = 51;
    public final int MEAT = 52;
    public final int ACCESSORIES = 53;
    public final int PET = 54;
    public final int PARMACY = 55;
    public final int INSTRUMENT = 56;
    public final int KEY = 57;
    public final int TABACO = 58;
    public final int CAR = 59;
    public final int TOOL = 60;
    public final int STORE = 61;

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
            case CAFE:{
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe2));
                break;
            }
            case FURNITURE:{
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.furniture));
                break;
            }
            case WIG: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.wig));
                break;
            }
            case HOMEAPPERANCE: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.homeapperance));
                break;
            }
            case ADD: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.add));
                break;
            }
            case PC: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pc));
                break;
            }
            case FOODCHAIN: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.foodchain));
                break;
            }
            case BANK: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bank));
                break;
            }
            case FLOWER: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.flower));
                break;
            }
            case STUDY: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.study));
                break;
            }
            case BOOK: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.book));
                break;
            }
            case CHICKEN: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.chicken));
                break;
            }
            case HOUSE: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.house));
                break;
            }
            case BUILD: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.build));
                break;
            }
            case CLOTH: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cloth));
                break;
            }
            case HOSPITAL: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital));
                break;
            }
            case HAIRCUT: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.haircut));
                break;
            }
            case PETHOSPITAL: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pethospital));
                break;
            }
            case DESERT: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.desert));
                break;
            }
            case CRAFT: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.craft));
                break;
            }
            case FROOT: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.froot));
                break;
            }
            case MUCHINE: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.muchine));
                break;
            }
            case NAILART: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.nailart));
                break;
            }
            case LAW: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.law));
                break;
            }
            case ELDER: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.elder));
                break;
            }
            case SING: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.sing));
                break;
            }
            case BEAUTY: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beauty));
                break;
            }
            case PRINT: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.print));
                break;
            }
            case BATH: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bath));
                break;
            }
            case BEER: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beer));
                break;
            }
            case WASH: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.wash));
                break;
            }
            case MEAT: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.meat));
                break;
            }
            case ACCESSORIES: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.accessories));
                break;
            }
            case PET: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pet));
                break;
            }
            case PARMACY: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.parmacy));
                break;
            }
            case INSTRUMENT: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.instrument));
                break;
            }
            case TOOL: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tool));
                break;
            }
            case CAR: {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                break;
            }
            case STORE:{
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.store));
                break;
            }



            default:{
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.store));
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