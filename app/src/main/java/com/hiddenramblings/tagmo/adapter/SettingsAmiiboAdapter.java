package com.hiddenramblings.tagmo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hiddenramblings.tagmo.R;
import com.hiddenramblings.tagmo.TagMo;
import com.hiddenramblings.tagmo.amiibo.Amiibo;
import com.hiddenramblings.tagmo.nfctech.TagUtils;
import com.hiddenramblings.tagmo.settings.SettingsFragment;

import java.util.ArrayList;
import java.util.Collections;

public class SettingsAmiiboAdapter extends BaseAdapter {

    ArrayList<Amiibo> items;

    public SettingsAmiiboAdapter(ArrayList<Amiibo> items) {
        this.items = items;
        Collections.sort(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).id;
    }

    @Override
    public Amiibo getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.amiibo_compact_card, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String amiiboHexId;
        String amiiboName = "";
        String amiiboSeries = "";
        String amiiboType = "";
        String gameSeries = "";
        // String character = "";
        String amiiboImageUrl;

        Amiibo amiibo = getItem(position);
        amiiboHexId = TagUtils.amiiboIdToHex(amiibo.id);
        amiiboImageUrl = amiibo.getImageUrl();
        if (amiibo.name != null)
            amiiboName = amiibo.name;
        if (amiibo.getAmiiboSeries() != null)
            amiiboSeries = amiibo.getAmiiboSeries().name;
        if (amiibo.getAmiiboType() != null)
            amiiboType = amiibo.getAmiiboType().name;
        if (amiibo.getGameSeries() != null)
            gameSeries = amiibo.getGameSeries().name;
        // if (amiibo.getCharacter() != null)
        //     character = amiibo.getCharacter().name;

        holder.txtError.setVisibility(android.view.View.GONE);
        setAmiiboInfoText(holder.txtName, amiiboName);
        setAmiiboInfoText(holder.txtTagId, amiiboHexId);
        setAmiiboInfoText(holder.txtAmiiboSeries, amiiboSeries);
        setAmiiboInfoText(holder.txtAmiiboType, amiiboType);
        setAmiiboInfoText(holder.txtGameSeries, gameSeries);
        // setAmiiboInfoText(holder.txtCharacter, character);
        holder.txtPath.setVisibility(android.view.View.GONE);

        if (holder.imageAmiibo != null) {
            holder.imageAmiibo.setVisibility(android.view.View.GONE);
            Glide.with(convertView).clear(holder.target);
            if (amiiboImageUrl != null) {
                Glide.with(convertView)
                        .setDefaultRequestOptions(onlyRetrieveFromCache())
                        .asBitmap()
                        .load(amiiboImageUrl)
                        .into(holder.target);
            }
        }

        return convertView;
    }

    private void setAmiiboInfoText(TextView textView, CharSequence text) {
        if (text.length() == 0) {
            textView.setText(R.string.unknown);
            textView.setTextColor(Color.RED);
        } else {
            textView.setText(text);
            textView.setTextColor(textView.getTextColors().getDefaultColor());
        }
    }

    private RequestOptions onlyRetrieveFromCache() {
        String imageNetworkSetting = TagMo.getPrefs().imageNetworkSetting().get();
        if (SettingsFragment.IMAGE_NETWORK_NEVER.equals(imageNetworkSetting)) {
            return new RequestOptions().onlyRetrieveFromCache(true);
        } else if (SettingsFragment.IMAGE_NETWORK_WIFI.equals(imageNetworkSetting)) {
            ConnectivityManager cm = (ConnectivityManager)
                    TagMo.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return new RequestOptions().onlyRetrieveFromCache(activeNetwork == null
                    || activeNetwork.getType() != ConnectivityManager.TYPE_WIFI);
        } else {
            return new RequestOptions().onlyRetrieveFromCache(false);
        }
    }

    protected static class ViewHolder {
        TextView txtError;
        TextView txtName;
        TextView txtTagId;
        TextView txtAmiiboSeries;
        TextView txtAmiiboType;
        TextView txtGameSeries;
        TextView txtCharacter;
        TextView txtPath;
        ImageView imageAmiibo;

        CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                imageAmiibo.setVisibility(android.view.View.GONE);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                imageAmiibo.setVisibility(android.view.View.GONE);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, Transition transition) {
                imageAmiibo.setImageBitmap(resource);
                imageAmiibo.setVisibility(android.view.View.VISIBLE);
            }
        };

        public ViewHolder(View view) {
            this.txtError = view.findViewById(R.id.txtError);
            this.txtName = view.findViewById(R.id.txtName);
            this.txtTagId = view.findViewById(R.id.txtTagId);
            this.txtAmiiboSeries = view.findViewById(R.id.txtAmiiboSeries);
            this.txtAmiiboType = view.findViewById(R.id.txtAmiiboType);
            this.txtGameSeries = view.findViewById(R.id.txtGameSeries);
            this.txtCharacter = view.findViewById(R.id.txtCharacter);
            this.txtPath = view.findViewById(R.id.txtPath);
            this.imageAmiibo = view.findViewById(R.id.imageAmiibo);

            view.setTag(this);
        }
    }
}
