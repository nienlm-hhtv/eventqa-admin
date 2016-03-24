package com.hhtv.eventqa_admin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.activities.EventDetailActivity;
import com.hhtv.eventqa_admin.adapters.EventHighestVoteAdapter3;
import com.hhtv.eventqa_admin.api.APIEndpoint;
import com.hhtv.eventqa_admin.api.APIService;
import com.hhtv.eventqa_admin.helpers.DeviceUltis;
import com.hhtv.eventqa_admin.helpers.NetworkFailBuilder;
import com.hhtv.eventqa_admin.helpers.UserUtils;
import com.hhtv.eventqa_admin.helpers.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa_admin.models.question.MarkQuestionResponse;
import com.hhtv.eventqa_admin.models.question.Question;
import com.hhtv.eventqa_admin.models.question.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 22/3/16.
 */
public class EventHighestVoteFragment3 extends BaseFragment implements IOnAdapterInteractListener {

    private static final String TAG = "EHVF3";
    public int eventId;
    APIEndpoint api = APIService.build();
    String deviceId;
    MaterialDialog mDialog;
    EventHighestVoteAdapter3 mAdapter;
    public static EventHighestVoteFragment3 newInstance(int eventId){
        EventHighestVoteFragment3 f = new EventHighestVoteFragment3();
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
    LinearLayoutManager mManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_question2, container, false);
        ButterKnife.bind(this, v);
        mManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };
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
        Call<Question> call = api.getHighestVoteQuestion(eventId, UserUtils.getUserId(getContext()), deviceId);
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                if(!isAdded()){
                    return;
                }
                if (response.isSuccess()) {
                    if (response.body().getResults().size() > 0) {
                        if (mAdapter == null) {
                            mAdapter = new EventHighestVoteAdapter3(response.body().getResults(), EventHighestVoteFragment3.this);
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
                new MaterialDialog.Builder(EventHighestVoteFragment3.this.getContext())
                        .title("Network fail")
                        .content("Cannot access server at moment, please try again !")
                        .positiveText("Retry")
                        .negativeText("Dismiss")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                loadQuestionList();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                changeViewOrder(false, false, true);
                            }
                        }).show();
            }
        });
    }

    public void updateQuestionList(boolean showLoading){
        if (eventQuestionRecyclerView.getVisibility() == View.GONE){
            Log.d(TAG, "updateQuestionList: list is invisible !");
            loadQuestionList();
            return;
        }
        eventQuestionSwipeRefreshLayout.setRefreshing(showLoading);
        Call<Question> call = api.getHighestVoteQuestion(eventId, UserUtils.getUserId(EventHighestVoteFragment3.this.getContext()), deviceId);
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                if(!isAdded()){
                    return;
                }
                try{
                    eventQuestionSwipeRefreshLayout.setRefreshing(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (response.isSuccess()){
                    if (response.body().getResults().size() == 0){
                        changeViewOrder(false, false, false);
                        return;
                    }
                    final List<List<Result>> data = reArrangeModels(response.body().getResults());
                    mAdapter.insertItems(data.get(0), new EventHighestVoteAdapter3.IOnUpdateItemsComplete() {
                        @Override
                        public void onComplete() {
                            mAdapter.removeItems(data.get(1), new EventHighestVoteAdapter3.IOnUpdateItemsComplete() {
                                @Override
                                public void onComplete() {
                                    mAdapter.updateItems(data.get(2), new EventHighestVoteAdapter3.IOnUpdateItemsComplete() {
                                        @Override
                                        public void onComplete() {

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

    void changeViewOrder(boolean listView, boolean loading, boolean notfound) {
        eventQuestionRecyclerView.setVisibility(listView ? View.VISIBLE : View.GONE);
        eventQuestionLoadingLayout.setVisibility(loading ? View.VISIBLE : View.GONE);
        eventQuestionNotfoundLayout.setVisibility(notfound ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onItemClick(final int id, int position) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.choose_your_action)
                .items(R.array.question_action)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /*Toast.makeText(EventHighestVoteFragment3.this.getContext(), which + ": " + text, Toast.LENGTH_SHORT).show();*/
                        dialog.dismiss();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.event_question_notfound_layout)
    public void onClick() {
        ((EventDetailActivity)getActivity()).reloadAllContent();
    }


    List<List<Result>> reArrangeModels(List<Result> questions) {
        List<List<Result>> finals = new ArrayList<>();
        ResultArrayList new_questions = new ResultArrayList();
        ResultArrayList removed_questions = new ResultArrayList();
        List<Result> model = mAdapter.mModel;

        new_questions.addAll(questions);
        new_questions.removeDiff(model);
        removed_questions.addAll(model);
        removed_questions.removeDiff(questions);
        finals.add(new_questions);
        finals.add(removed_questions);
        finals.add(questions);
        Log.d(TAG, "reArrangeModels: new: " + finals.get(0).size() + " removed: " + finals.get(1).size() + " original: "
                + finals.get(2).size());
        return finals;
    }

    public class ResultArrayList extends ArrayList<Result> {
        public boolean removeDiff(List<Result> collection) {
            for (Result result : collection
                    ) {
                int i = contain(result);
                if (i != -1)
                    remove(i);
            }
            return true;
        }

        public int contain(Result object) {
            for (Result r : this) {
                if (r.getId() == object.getId()) return indexOf(r);
            }
            return -1;
        }
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
                    Log.d("MYTAG2", "ans: " + response.raw().request().url());
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
