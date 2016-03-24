package com.hhtv.eventqa_admin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.activities.EventDetailActivity;
import com.hhtv.eventqa_admin.adapters.EventQuestionListAdapter3;
import com.hhtv.eventqa_admin.api.APIEndpoint;
import com.hhtv.eventqa_admin.api.APIService;
import com.hhtv.eventqa_admin.helpers.DeviceUltis;
import com.hhtv.eventqa_admin.helpers.NetworkFailBuilder;
import com.hhtv.eventqa_admin.helpers.UserUtils;
import com.hhtv.eventqa_admin.helpers.customClasses.LinearLayoutManagerWithSmoothScroller;
import com.hhtv.eventqa_admin.helpers.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa_admin.models.question.MarkQuestionResponse;
import com.hhtv.eventqa_admin.models.question.Question;
import com.hhtv.eventqa_admin.models.question.Vote;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 23/3/16.
 */
public class EventQuestionListFragment3 extends BaseFragment implements IOnAdapterInteractListener {
    private static final String TAG = "EQLF3";
    public int eventId;
    APIEndpoint api = APIService.build();
    String deviceId;
    EventQuestionListAdapter3 mAdapter;
    MaterialDialog mDialog;
    public static EventQuestionListFragment3 newInstance(int eventId){
        EventQuestionListFragment3 f = new EventQuestionListFragment3();
        f.eventId = eventId;
        return f;
    }

    @Bind(R.id.event_question_recycler_view)
    RecyclerView eventQuestionRecyclerView;
    @Bind(R.id.event_question_loading_layout)
    LinearLayout eventQuestionLoadingLayout;
    @Bind(R.id.event_question_notfound_layout)
    LinearLayout eventQuestionNotfoundLayout;
    @Bind(R.id.event_question_swipe_refresh_layout)
    SwipeRefreshLayout eventQuestionSwipeRefreshLayout;
    LinearLayoutManagerWithSmoothScroller mManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_question2, container, false);
        ButterKnife.bind(this, v);
        mManager = new LinearLayoutManagerWithSmoothScroller(getContext());
        eventQuestionRecyclerView.setLayoutManager(mManager);
        eventQuestionRecyclerView.setHasFixedSize(true);
        eventQuestionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventQuestionRecyclerView.getItemAnimator().setAddDuration(300);
        eventQuestionRecyclerView.getItemAnimator().setRemoveDuration(300);
        eventQuestionRecyclerView.getItemAnimator().setChangeDuration(300);
        eventQuestionRecyclerView.getItemAnimator().setMoveDuration(300);

        eventQuestionSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((EventDetailActivity) getActivity()).reloadAllContent();
            }
        });
        eventQuestionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int firstPos = mManager.findFirstCompletelyVisibleItemPosition();
                if (firstPos > 0) {
                    eventQuestionSwipeRefreshLayout.setEnabled(false);
                } else {
                    eventQuestionSwipeRefreshLayout.setEnabled(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        deviceId = DeviceUltis.getDeviceId(getContext());
        mDialog = new MaterialDialog.Builder(getContext())
                .title("Please wait")
                .content("performing your action...")
                .progress(true, 0)
                .cancelable(false).build();
        loadQuestionList();
        return v;
    }

    public void loadQuestionList(){
        changeViewOrder(false, true, false);
        Call<Question> call = api.getAllQuestions(eventId, UserUtils.getUserId(getContext()), deviceId);
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                if(!isAdded()){
                    return;
                }
                if (response.isSuccess()) {
                    if (response.body().getResults().size() > 0) {
                        if (mAdapter == null) {
                            mAdapter = new EventQuestionListAdapter3(response.body().getResults(), EventQuestionListFragment3.this);
                            eventQuestionRecyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.mModel.clear();
                            mAdapter.mModel.addAll(response.body().getResults());
                            mAdapter.notifyDataSetChanged();
                        }
                        changeViewOrder(true, false, false);
                    } else {
                        changeViewOrder(false, false, true);
                    }
                } else {
                    changeViewOrder(false, false, true);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure() returned: " + t.getMessage());
                changeViewOrder(false, false, true);
            }
        });
    }

    public void updateQuestion(boolean showLoading){
        if (eventQuestionRecyclerView.getVisibility() == View.GONE){
            Log.d(TAG, "updateQuestionList: list is invisible !");
            loadQuestionList();
            return;
        }
        eventQuestionSwipeRefreshLayout.setRefreshing(showLoading);
        Call<Vote> call = api.updateQuestionsList(deviceId, eventId, UserUtils.getUserId(this.getContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(final Response<Vote> response, Retrofit retrofit) {
                if(!isAdded()){
                    return;
                }
                try{
                    eventQuestionSwipeRefreshLayout.setRefreshing(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (response.isSuccess()){
                    Log.d(TAG, response.body().getNew_questions().size() + " - " +
                            response.body().getChanged_questions().size() + " - " +
                            response.body().getRemoved_questions().size());
                    final boolean isNewItemAddFlag = response.body().getNew_questions().size() > 0;
                    mAdapter.updateItems(response.body().getChanged_questions(), new EventQuestionListAdapter3.IOnUpdateItemsComplete() {
                        @Override
                        public void onComplete() {
                            mAdapter.removeItems(response.body().getRemoved_questions(), new EventQuestionListAdapter3.IOnUpdateItemsComplete() {
                                @Override
                                public void onComplete() {
                                    mAdapter.insertItems(response.body().getNew_questions(), new EventQuestionListAdapter3.IOnUpdateItemsComplete() {
                                        @Override
                                        public void onComplete() {
                                            if (isNewItemAddFlag)
                                            eventQuestionRecyclerView.smoothScrollToPosition(0);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                eventQuestionSwipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "updateQuestionList onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    public void onItemClick(final int id, int position) {
        Log.d(TAG, "onItemClick: " + id + " - " + position);
        new MaterialDialog.Builder(getContext())
                .title(R.string.choose_your_action)
                .items(R.array.question_action)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /*Toast.makeText(EventQuestionListFragment3.this.getContext(), which + ": " + text, Toast.LENGTH_SHORT).show();*/
                        //dialog.dismiss();
                        switch (which){
                            case 0:
                                onAnsBtnClick(id);
                                break;
                            case 1:
                                onDelBtnClick(id);
                                break;
                            case 2:
                                onDupBtnClick(id);
                                break;
                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.event_question_notfound_layout)
    public void onClick() {
        ((EventDetailActivity)getActivity()).reloadAllContent();
    }

    void changeViewOrder(boolean listView, boolean loading, boolean notfound) {
        eventQuestionRecyclerView.setVisibility(listView ? View.VISIBLE : View.GONE);
        eventQuestionLoadingLayout.setVisibility(loading ? View.VISIBLE : View.GONE);
        eventQuestionNotfoundLayout.setVisibility(notfound ? View.VISIBLE : View.GONE);
    }


    public void onAnsBtnClick(final int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionAnswered(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                if(!isAdded()){
                    return;
                }
                mDialog.dismiss();
                if (response.isSuccess()) {
                    mAdapter.removeItem(id);
                    Log.d("MYTAG2","ans: " + response.raw().request().url());
                    Toast.makeText(getContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if (response.body().isSuccess()) {
                        ((EventDetailActivity) getActivity()).reloadContent(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Error occur, please try again !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mDialog.dismiss();
                new NetworkFailBuilder(getContext()).show();
            }
        });
    }


    public void onDelBtnClick(final int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionDeleted(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                if(!isAdded()){
                    return;
                }
                mDialog.dismiss();
                Log.d("MYTAG2", "del: " + response.raw().request().url());
                if (response.isSuccess()) {
                    mAdapter.removeItem(id);
                    Toast.makeText(getContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if (response.body().isSuccess()) {
                        ((EventDetailActivity) getActivity()).reloadContent(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Error occur, please try again !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mDialog.dismiss();
                new NetworkFailBuilder(getContext()).show();
            }
        });
    }


    public void onDupBtnClick(final int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionDuplicated(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                if(!isAdded()){
                    return;
                }
                mDialog.dismiss();
                Log.d("MYTAG2", "dup: " + response.raw().request().url());
                if (response.isSuccess()) {
                    mAdapter.removeItem(id);
                    Toast.makeText(getContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if (response.body().isSuccess()) {
                        ((EventDetailActivity) getActivity()).reloadContent(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Error occur, please try again !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mDialog.dismiss();
                new NetworkFailBuilder(getContext()).show();
            }
        });
    }
}
