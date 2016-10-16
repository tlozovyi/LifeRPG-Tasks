package com.levor.liferpgtasks.view.fragments.hero;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.model.Misc;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChangeHeroIconFragment extends DefaultFragment{
    private AssetManager assets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_hero_icon, container, false);
        assets = getContext().getAssets();
        RecyclerView recyclerView = (RecyclerView) v;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        String[] assetsNames = null;
        try {
            assetsNames = assets.list("");
        } catch (IOException e) {
            Log.e("Assets", "Could not list assets", e);
        }

        List<String> imageNames = new ArrayList<>();
        //sort out all non icons
        for (int i = 0; i < assetsNames.length; i++) {
            if (assetsNames[i].endsWith(".png")) {
                imageNames.add(assetsNames[i]);
            }
        }

        recyclerView.setAdapter(new ImageAdapter(imageNames.toArray(new String[imageNames.size()])));

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getString(R.string.edit_hero_fragment_title));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Change Hero Icon Fragment");
    }

    private class ImageHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ImageHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.recycler_view_item_change_image, container, false));
            image = (ImageView) itemView.findViewById(R.id.change_hero_image_item);
        }

        public void bindImage(final String name){
            try {
                InputStream is = assets.open(name);
                Drawable d = Drawable.createFromStream(is, null);
                image.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCurrentActivity().setHeroImageName(name, Misc.ASSETS_ICON);
                    getCurrentActivity().showNthPreviousFragment(2);
                    getController().getGATracker().send(new HitBuilders.EventBuilder()
                            .setCategory(getContext().getString(R.string.GA_action))
                            .setAction(getContext().getString(R.string.GA_change_hero_icon))
                            .build());
                }
            });
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {
        private String[] names;

        public ImageAdapter(String[] names){
            this.names = names;
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ImageHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {
            String name = names[position];
            holder.bindImage(name);
        }

        @Override
        public int getItemCount() {
            return names.length;
        }
    }
}
