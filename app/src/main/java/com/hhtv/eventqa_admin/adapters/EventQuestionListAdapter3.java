package com.hhtv.eventqa_admin.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.helpers.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa_admin.models.question.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by nienb on 22/3/16.
 */
public class EventQuestionListAdapter3 extends RecyclerView.Adapter<EventQuestionListAdapter3.ItemViewViewHolder>{
    public List<Result> mModel;
    public IOnAdapterInteractListener mFragment;

    private static final String TAG = "EQLA3";
    public EventQuestionListAdapter3(List<Result> mModel, IOnAdapterInteractListener mFragment) {
        this.mModel = mModel;
        this.mFragment = mFragment;
    }

    @Override
    public ItemViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_detail_item, parent, false));
    }

    public Date getPostDate(String src) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = format.parse(src);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }
    @Override
    public void onBindViewHolder(final ItemViewViewHolder holder, final int position) {
        final Result model = mModel.get(position);
        holder.mId.setText(model.getId() + "");
        holder.mUserName.setText((model.getCreator_name().equals(""))? "Guest" : mModel.get(position).getCreator_name());
        holder.mVoteUpCount.setText(model.getvote_up_count() + "");
        holder.mVoteDownCount.setText(model.getvote_down_count() + "");
        holder.mContent.setText(model.getBody());
        holder.mPostFrom.setText(android.text.format.DateFormat.format("dd/MM/yyyy hh:mm",
                getPostDate(model.getcreate_at())));
        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.onItemClick(model.getId(), position);
            }
        });
    }



    public int positionOf(int id){
        for (int i =0; i < mModel.size(); i++){
            if (mModel.get(i).getId() == id) return i;
        }
        return -1;
    }
    public int positionOf(String body){
        for (int i =0; i < mModel.size(); i++){
            if (mModel.get(i).getBody().equals(body)) return i;
        }
        return -1;
    }
    public int updateModelValue(Result item, int position){
        if (position < 0 || position > mModel.size()) return position;

        Result r = mModel.get(position);
        if (r.getvote_up_count() == item.getvote_up_count()
                && r.getvote_down_count() == item.getvote_down_count()
                && r.getIsVoted() == item.getIsVoted())
            return position;
        r.setId(item.getId());
        r.setName(item.getName());
        r.setBody(item.getBody());
        r.setcreate_at(item.getcreate_at());
        r.setcreator_id(item.getcreator_id());
        r.setCreator_name(item.getCreator_name());
        r.setStatus(item.getStatus());
        r.setvote_down_count(item.getvote_down_count());
        r.setvote_up_count(item.getvote_up_count());
        r.setIsVoted(item.getIsVoted());
        position = mModel.indexOf(r);
        notifyItemChanged(position);
        return position;
    }
    public void insertItems(List<Result> results, IOnUpdateItemsComplete listener){
        for (int i = 0; i < results.size(); i ++ ){
            Result result = results.get(i);
            int p = positionOf(result.getId());
            if (p != -1)
                updateModelValue(result, p);
            else{
                mModel.add(0, result);
                notifyItemInserted(0);
                notifyItemRangeChanged(0, getItemCount());
            }
        }
        listener.onComplete();
    }

    public void removeItem(int id){
        int p = positionOf(id);
        if (p != -1){
            mModel.remove(p);
            notifyItemRemoved(p);
            notifyItemRangeChanged(p, getItemCount());
        }
    }

    public void removeItems(List<Result> results, IOnUpdateItemsComplete listener){
        for (int i = 0; i < results.size(); i ++ ){
            Result result = results.get(i);
            int p = positionOf(result.getId());
            if (p != -1){
                mModel.remove(p);
                notifyItemRemoved(p);
                notifyItemRangeChanged(p, getItemCount());
            }
            else{
                Log.e(TAG, "removeItems: cannot find item - " + result.getBody());
            }
        }
        listener.onComplete();
    }
    public void updateItems(List<Result> results, IOnUpdateItemsComplete listener){
        for (int i = 0; i < results.size(); i ++){
            Result result = results.get(i);
            int pos = positionOf(result.getId());
            if (pos == -1){
                Log.e(TAG, "updateItems: cannot find item - " + result.getBody());
                continue;
            }
            updateModelValue(result, pos);
        }
        listener.onComplete();
    }
    public void instantSwitch(int id, int voteValue, IOnSwitchItemComplete listener){
        int p = positionOf(id);
        if (p == -1){
            Log.e(TAG, "instantSwitch: cannot find item with id - " + id);
            listener.onComplete(p);
            return;
        }
        if (p == 0){
            Result r = mModel.get(0);
            r.setvote_up_count(voteValue);
            updateModelValue(r, 0);
            listener.onComplete(p);
            return;
        }
        for (int i=p-1; i >= 0; i--){
            Result r = mModel.get(i);
            if (i == 0){
                if (r.getvote_up_count() > voteValue) {
                    listener.onComplete(i);
                    return;
                }
                else{
                    moveItem(p, i);
                    listener.onComplete(i);
                    return;
                }
            }
            if (r.getvote_up_count() <= voteValue) continue;
            if (r.getvote_up_count() > voteValue){
                moveItem(p, i + 1);
                listener.onComplete(i+1);
                return;
            }
        }
        listener.onComplete(p);
    }

    private int moveItem(int from, int to) {
        if (from < 0 || to < 0)
            return -1;
        if (from == to)
            return -1;
        try{
            Result r = mModel.remove(from);
            mModel.add(to, r);
            notifyItemMoved(from, to);
        }catch (Exception e){
            Log.e(TAG, "switchItem: " + e.getMessage());
        }
        return 1;
    }

    public interface IOnUpdateItemsComplete{
        void onComplete();
    }
    public interface IOnSwitchItemComplete{
        void onComplete(int newPos);
    }
    @Override
    public int getItemCount() {
        return mModel.size();
    }

    public class ItemViewViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName, mVoteUpCount, mVoteDownCount, mContent, mPostFrom, mId;
        View item_view;

        public ItemViewViewHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(
                    R.id.qdetail_username);
            mVoteUpCount = (TextView) itemView.findViewById(
                    R.id.qdetail_voteupcount);
            mVoteDownCount = (TextView) itemView.findViewById(
                    R.id.qdetail_votedowncount);
            mContent = (TextView) itemView.findViewById(
                    R.id.qdetail_content);
            mPostFrom = (TextView) itemView.findViewById(R.id.qdetail_postfrom);
            mId = (TextView) itemView.findViewById(R.id.qdetail_id);
            item_view = itemView.findViewById(R.id.qdetail_main);
        }
    }
}
