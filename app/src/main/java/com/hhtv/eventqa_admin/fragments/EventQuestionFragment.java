package com.hhtv.eventqa_admin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.activities.EventDetailActivity;
import com.hhtv.eventqa_admin.adapters.SimpleQuestionAdapter;
import com.hhtv.eventqa_admin.api.APIEndpoint;
import com.hhtv.eventqa_admin.api.APIService;
import com.hhtv.eventqa_admin.helpers.DeviceUltis;
import com.hhtv.eventqa_admin.helpers.NetworkFailBuilder;
import com.hhtv.eventqa_admin.helpers.UserUtils;
import com.hhtv.eventqa_admin.helpers.listener.IOnAdapterInteractListener;
import com.hhtv.eventqa_admin.models.question.MarkQuestionResponse;
import com.hhtv.eventqa_admin.models.question.Question;
import com.hhtv.eventqa_admin.models.question.Result;
import com.hhtv.eventqa_admin.models.question.Vote;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 18/3/16.
 */
public class EventQuestionFragment extends BaseFragment implements IOnAdapterInteractListener {
    public static EventQuestionFragment newInstance(int eventId, int userId) {
        EventQuestionFragment f = new EventQuestionFragment();
        f.userId = userId;
        f.eventId = eventId;
        return f;
    }

    @Bind(R.id.event_question_recycler_view)
    UltimateRecyclerView mRecyclerView;
    MaterialDialog mDialog;
    APIEndpoint api = APIService.build();
    SimpleQuestionAdapter mAdapter = null;
    GridLayoutManager gridLayoutManager;
    public EventQuestionFragment() {
    }

    @Nullable
    @Override
    @DebugLog
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_question, container, false);

        ButterKnife.bind(this, v);
        firstLoad = true;
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getContext(), 1) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new SimpleQuestionAdapter(new ArrayList<Result>(), this);
        mRecyclerView.setEmptyView(getResources().getIdentifier("loadingview", "layout",
                getContext().getPackageName()));
        mRecyclerView.setItemAnimator(new jp.wasabeef.recyclerview.animators.SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(200);

        mRecyclerView.enableDefaultSwipeRefresh(true);
        mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firstLoad = true;
                mRecyclerView.setRefreshing(true);
                processUpdateQuestion();
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (addedItems.size() > 0) {
                        Log.d("MYTAG", "ONTOP");
                        mAdapter.insertNewItem(addedItems, new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
                            @Override
                            public void onComplete() {
                                //mRecyclerView.scrollVerticallyTo(0);
                            }
                        });
                        addedItems.clear();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        mDialog = new MaterialDialog.Builder(getContext())
            .title("Please wait")
            .content("performing your action...")
            .progress(true, 0)
            .cancelable(false).build();
        processLoadQuestion(eventId, userId, true);
        return v;
    }




    @Override
    public void onPause() {
        super.onPause();
        if (firstLoad = false)
            firstLoad = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (firstLoad = false)
            firstLoad = true;
    }

    @Override
    public void scroll(int position) {
        int firstVisible = gridLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = gridLayoutManager.findLastVisibleItemPosition();
        int visibleItem = lastVisible - firstVisible;
        //int scrollto = (firstVisible - position < 0)? 0 : firstVisible - position ;
        if (firstVisible < visibleItem){
            gridLayoutManager.scrollToPosition(0);
        }
        Log.d("MYTAG", "f: " + firstVisible + " l: " + lastVisible + "p: " + position);
    }

    public List<Result> addedItems = new ArrayList<>();
    public String updateData(final Response<Vote> response) {
        if (response.body().getChanged_questions().size() > 0) {
            if (mAdapter != null) {
                mAdapter.upDateItemChanged(response.body().getChanged_questions());
            }
        }
        if (response.body().getNew_questions().size() > 0) {
            if (mAdapter != null) {
                addedItems.addAll(response.body().getNew_questions());
                mAdapter.insertNewItem(response.body().getNew_questions(), new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
                    @Override
                    public void onComplete() {
                        //mRecyclerView.scrollVerticallyTo(0);
                        scroll(response.body().getNew_questions().size());
                    }
                });
            }
        }
        if (response.body().getRemoved_questions().size() > 0) {
            if (mAdapter != null) {
                mAdapter.removeItems(response.body().getRemoved_questions());
            }
        }
        return "new: " + response.body().getNew_questions().size()
                + " change: " + response.body().getChanged_questions().size()
                + " remove: " + response.body().getRemoved_questions().size()
                + " url: " + response.raw().request().url();
    }



    @Override
    public void onAnsBtnClick(final int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionAnswered(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                mDialog.dismiss();
                if (response.isSuccess()) {
                    mAdapter.removeItem(id);
                    Log.d("MYTAG2","ans: " + response.raw().request().url());
                    Toast.makeText(getContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if (response.body().isSuccess()) {
                        ((EventDetailActivity) getActivity()).reloadContent();
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

    @Override
    public void onDelBtnClick(final int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionDeleted(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                mDialog.dismiss();
                Log.d("MYTAG2","del: " + response.raw().request().url());
                if (response.isSuccess()) {
                    mAdapter.removeItem(id);
                    Toast.makeText(getContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if (response.body().isSuccess()) {
                        ((EventDetailActivity) getActivity()).reloadContent();
                    }
                } else {
                    Toast.makeText(getContext(), "Error occur, please try again !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mDialog.dismiss(); new NetworkFailBuilder(getContext()).show();
            }
        });
    }

    @Override
    public void onDupBtnClick(final int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionDuplicated(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                mDialog.dismiss();
                Log.d("MYTAG2","dup: " + response.raw().request().url());
                if (response.isSuccess()) {
                    mAdapter.removeItem(id);
                    Toast.makeText(getContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if (response.body().isSuccess()) {
                        ((EventDetailActivity) getActivity()).reloadContent();
                    }
                } else {
                    Toast.makeText(getContext(), "Error occur, please try again !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mDialog.dismiss(); new NetworkFailBuilder(getContext()).show();
            }
        });
    }

    @Override
    public void onItemClick(int id) {

    }
    boolean isAdapterEmpty = true;
    public void processUpdateQuestion() {
        if (isAdapterEmpty || mAdapter.getItemCount() == 0){
            firstLoad = true;
            processLoadQuestion(eventId, userId, false);
            return;
        }
        APIEndpoint api = APIService.build();
        Log.d("MYTAG","EQF update, call on: " + DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));

        Call<Vote> call = api.updateQuestionsList(DeviceUltis.getDeviceId(getContext()), eventId,
                UserUtils.getUserId(getContext()));
        call.enqueue(new Callback<Vote>() {
            @Override
            public void onResponse(Response<Vote> response, Retrofit retrofit) {
                mRecyclerView.hideEmptyView();
                mRecyclerView.setRefreshing(false);
                if (mAdapter == null) {
                    mAdapter = new SimpleQuestionAdapter(new ArrayList<Result>(), EventQuestionFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
                Log.d("MYTAG", "EQF update: " + updateData(response));
                if (mAdapter.getItemCount() == 0) {
                    mRecyclerView.getEmptyView().findViewById(R.id.loading_progressbar).setVisibility(View.INVISIBLE);
                    ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.loading_message))
                            .setText(getResources().getString(R.string.no_question_tap_to_retry));
                    mRecyclerView.getEmptyView().findViewById(R.id.loading_message).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firstLoad = true;
                            processLoadQuestion(eventId, userId, true);
                        }
                    });
                    mRecyclerView.showEmptyView();
                } else {
                    mRecyclerView.hideEmptyView();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG","EQF update fail: " + t.getMessage());
                mRecyclerView.setRefreshing(false);
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    int eventId = -1, userId = -1;
    boolean firstLoad = true;
    ArrayList<Result> mModels;

    @DebugLog
    public void processLoadQuestion(final int eventId, final int userid, boolean loading) {
        if (firstLoad) {
            firstLoad = false;
        } else {
            return;
        }
        this.eventId = eventId;
        this.userId = userid;
        if (loading){
            mRecyclerView.getEmptyView().findViewById(R.id.loading_progressbar).setVisibility(View.VISIBLE);
            ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.loading_message))
                    .setText(getResources().getString(R.string.loading));
            mRecyclerView.showEmptyView();
        }
        APIEndpoint api = APIService.build();
        Call<Question> call = api.getAllQuestions(eventId, userid, DeviceUltis.getDeviceId(getContext()));
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                Log.d("MYTAG", "EQF url: " + response.raw().request().url());
                mRecyclerView.hideEmptyView();
                if (response.body().getResults().size() == 0) {
                    mRecyclerView.getEmptyView().findViewById(R.id.loading_progressbar).setVisibility(View.INVISIBLE);
                    ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.loading_message))
                            .setText(getResources().getString(R.string.no_question_tap_to_retry));
                    mRecyclerView.getEmptyView().findViewById(R.id.loading_message).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firstLoad = true;
                            processLoadQuestion(eventId, userid, true);
                        }
                    });
                    mRecyclerView.showEmptyView();
                } else {
                    mModels = new ArrayList<>();
                    mModels.addAll(response.body().getResults());
                    mAdapter = new SimpleQuestionAdapter(response.body().getResults(), EventQuestionFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG", "EQF on fail ! " + t.getMessage());
                mRecyclerView.getEmptyView().findViewById(R.id.loading_progressbar).setVisibility(View.INVISIBLE);
                ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.loading_message))
                        .setText(getResources().getString(R.string.error_tap_to_retry));
                mRecyclerView.showEmptyView();
                mRecyclerView.getEmptyView().findViewById(R.id.loading_message).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firstLoad = true;
                        processLoadQuestion(eventId, userid, true);
                    }
                });
            }
        });
    }
}
