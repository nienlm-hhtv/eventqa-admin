package com.hhtv.eventqa_admin.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.fragments.EventListFragment;
import com.hhtv.eventqa_admin.models.event.Result;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

/**
 * Created by nienb on 10/3/16.
 */
public class EventListAdapter extends UltimateViewAdapter<EventListAdapter.ViewHolder> {
    List<Result> mModels = new ArrayList<>();
    EventListFragment mFragment;

    public EventListAdapter(List<Result> mModels, EventListFragment mFragment) {
        this.mModels.addAll(mModels);
        this.mFragment = mFragment;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_event_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    @DebugLog
    public int getAdapterItemCount() {
        return mModels.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Result s = mModels.get(position);
        Picasso.with(mFragment.getContext())
                .load(s.getImageLink())
                .placeholder(R.drawable.side_nav_bar)
                .error(R.drawable.side_nav_bar)
                .into(holder.mEvlistitemImg);
        holder.mEvlistitemName.setText(s.getName());
        switch (Integer.parseInt(s.getStatus())){
            case 0:
                holder.mEvlistitemStatus.setText(mFragment.getResources().getString(R.string.deleted));
                break;
            case 1:
                holder.mEvlistitemStatus.setText(mFragment.getResources().getString(R.string.on_air));
                break;
            case 2:
                holder.mEvlistitemStatus.setText(mFragment.getResources().getString(R.string.not_yet_started));
                break;
            case 3:
                holder.mEvlistitemStatus.setText(mFragment.getResources().getString(R.string.finished));
                break;
        }
        holder.mEvlistitemBtnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.onViewBtnClicked(s);
            }
        });
        holder.mEvlistitemBtnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.onEditBtnClicked(s);
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    static class ViewHolder extends UltimateRecyclerviewViewHolder{
        @Bind(R.id.evlistitem_img)
        ImageView mEvlistitemImg;
        @Bind(R.id.evlistitem_name)
        TextView mEvlistitemName;
        @Bind(R.id.evlistitem_btnedit)
        TextView mEvlistitemBtnedit;
        @Bind(R.id.evlistitem_btnview)
        TextView mEvlistitemBtnview;
        @Bind(R.id.evlistitem_status)
        TextView mEvlistitemStatus;
        @Bind(R.id.card_view)
        CardView mCardView;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
