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
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

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
public class EventHighestVoteFragment extends BaseFragment implements IOnAdapterInteractListener {
    public static EventHighestVoteFragment newInstance(int eventId, int userId) {
        EventHighestVoteFragment f = new EventHighestVoteFragment();
        f.userId = userId;
        f.eventId = eventId;
        return f;
    }

    @Bind(R.id.event_question_recycler_view)
    UltimateRecyclerView mRecyclerView;


    SimpleQuestionAdapter mAdapter = null;
    GridLayoutManager gridLayoutManager;
    APIEndpoint api = APIService.build();
    MaterialDialog mDialog;
    public EventHighestVoteFragment() {
    }

    @Nullable
    @Override
    
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
        mRecyclerView.setEmptyView(getResources().getIdentifier("loadingview", "layout",
                getContext().getPackageName()));
        mRecyclerView.setItemAnimator(new jp.wasabeef.recyclerview.animators.FadeInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(200);
        mRecyclerView.enableDefaultSwipeRefresh(true);
        mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firstLoad = true;
                mRecyclerView.setRefreshing(true);
                processLoadQuestion(eventId, userId, false);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (addedItems.size() > 0) {
                        Log.d("MYTAG", "ONTOP");
                        mAdapter.insertNewItemOnBottom(addedItems, new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
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
        processLoadQuestion(eventId, userId, true);
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


    public List<Result> addedItems = new ArrayList<>();

    public String updateData(final List<List<Result>> m) {
        List<Result> new_questions = m.get(0);
        final List<Result> removed_questions = m.get(1);

        if (mAdapter != null) {
            mAdapter.insertNewItemOnBottom(new_questions, new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
                @Override
                public void onComplete() {
                    if (mAdapter != null) {
                        mAdapter.removeItemsWithCallback(removed_questions, new SimpleQuestionAdapter.IOnUpdateItemsComplete() {
                            @Override
                            public void onComplete() {
                                mAdapter.updatePosition(m.get(2));
                            }
                        });
                    }
                }
            });
        }


        return "new: " + new_questions.size()
                + " remove: " + removed_questions.size();
    }


    @Override
    public void onAnsBtnClick(int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionAnswered(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                mDialog.dismiss();
                if (response.isSuccess()) {
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
    public void onDelBtnClick(int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionDeleted(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                mDialog.dismiss();
                Log.d("MYTAG2","del: " + response.raw().request().url());
                if (response.isSuccess()) {
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
    public void onDupBtnClick(int id) {
        //mAdapter.removeItem(id);
        mDialog.show();
        Call<MarkQuestionResponse> call = api.markQuestionDuplicated(UserUtils.getUserId(getContext()), id);
        call.enqueue(new Callback<MarkQuestionResponse>() {
            @Override
            public void onResponse(Response<MarkQuestionResponse> response, Retrofit retrofit) {
                mDialog.dismiss();
                Log.d("MYTAG2","dup: " + response.raw().request().url());
                if (response.isSuccess()) {
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

    @Override
    public void scroll(int position) {
        int firstVisible = gridLayoutManager.findFirstVisibleItemPosition();
        //int lastVisible = gridLayoutManager.findLastVisibleItemPosition();

        if (firstVisible < 2)
            gridLayoutManager.scrollToPosition(0);
    }

    int eventId = -1, userId = -1;
    boolean firstLoad = true;

    @DebugLog
    public void processLoadQuestion(final int eventId, final int userid, boolean loading) {

        this.eventId = eventId;
        this.userId = userid;
        if (loading) {
            mRecyclerView.getEmptyView().findViewById(R.id.loading_progressbar).setVisibility(View.VISIBLE);
            ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.loading_message))
                    .setText(getResources().getString(R.string.loading));
            mRecyclerView.showEmptyView();
        }
        APIEndpoint api = APIService.build();
        Call<Question> call = api.getHighestVoteQuestion(eventId, userid, DeviceUltis.getDeviceId(getContext()));
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                mRecyclerView.setRefreshing(false);
                Log.d("MYTAG", "EHVF url: " + response.raw().request().url());
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
                } else if (mAdapter == null) {
                    mAdapter = new SimpleQuestionAdapter(response.body().getResults(), EventHighestVoteFragment.this);
                    mAdapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.hideEmptyView();
                } else {
                    if (!(mRecyclerView.getAdapter() instanceof SimpleQuestionAdapter)) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    List<List<Result>> list = reArrangeModels(response.body().getResults());
                    updateData(list);
                    mRecyclerView.hideEmptyView();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("MYTAG", "EHVF on fail ! " + t.getMessage());
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

    List<List<Result>> reArrangeModels(List<Result> questions) {
        List<List<Result>> finals = new ArrayList<>();
        ResultArrayList new_questions = new ResultArrayList();
        ResultArrayList removed_questions = new ResultArrayList();
        List<Result> model = mAdapter.getmModel();

        new_questions.addAll(questions);
        new_questions.removeDiff(model);
        removed_questions.addAll(model);
        removed_questions.removeDiff(questions);
        finals.add(new_questions);
        finals.add(removed_questions);
        finals.add(questions);
        Log.d("MYTAG", "new: " + finals.get(0).size() + " removed: " + finals.get(1).size() + " original: "
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
}

