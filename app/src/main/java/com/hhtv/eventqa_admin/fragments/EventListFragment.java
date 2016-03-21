package com.hhtv.eventqa_admin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.activities.MainActivity;
import com.hhtv.eventqa_admin.adapters.EventListAdapter;
import com.hhtv.eventqa_admin.api.APIEndpoint;
import com.hhtv.eventqa_admin.api.APIService;
import com.hhtv.eventqa_admin.helpers.MyCallBack;
import com.hhtv.eventqa_admin.models.event.Event;
import com.hhtv.eventqa_admin.models.event.Result;
import com.hhtv.eventqa_admin.models.user.GetUserResponse;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 10/3/16.
 */
public class EventListFragment extends BaseFragment {
    private GetUserResponse user;
    public static EventListFragment newInstance(GetUserResponse user){
        EventListFragment f = new EventListFragment();
        f.user = user;
        return f;
    }

    @Bind(R.id.evlist_recyclerview)
    UltimateRecyclerView mRecyclerView;

    EventListAdapter mAdapter;
    Event mModel;
    APIEndpoint api = APIService.build();
    LinearLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_event_main, container, false);
        ButterKnife.bind(this, v);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setEmptyView(getResources().getIdentifier("loadingview", "layout",
                getContext().getPackageName()));
        mRecyclerView.setItemAnimator(new jp.wasabeef.recyclerview.animators.SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);

        mRecyclerView.enableDefaultSwipeRefresh(true);
        mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.setRefreshing(true);
                processLoadEventList();
            }
        });
        processLoadEventList();
        return v;
    }


    public void processLoadEventList(){

        mRecyclerView.setRefreshing(false);
        mRecyclerView.getEmptyView().findViewById(R.id.loading_progressbar).setVisibility(View.VISIBLE);
        mRecyclerView.showEmptyView();
        Call<Event> call = api.getEvents(Integer.parseInt(user.getCode()));
        call.enqueue(new MyCallBack(getContext(), new MyCallBack.IOnDataReceived() {
            @Override
            public void onReceived(Response response, Retrofit retrofit) {

                mModel = (Event) response.body();
                Log.d("MYTAG", "received: " + mModel.toString());
                if (mModel.getResults().size() > 0) {
                    mAdapter = new EventListAdapter(mModel.getResults(), EventListFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.hideEmptyView();
                } else {
                    mRecyclerView.getEmptyView().findViewById(R.id.loading_progressbar).setVisibility(View.INVISIBLE);
                    ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.loading_message))
                            .setText(getContext().getResources().getString(R.string.no_data_tap_to_reload));
                    mRecyclerView.getEmptyView().findViewById(R.id.loading_message).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            processLoadEventList();
                        }
                    });
                    mRecyclerView.showEmptyView();
                }
            }

            @Override
            public void onError(Throwable t) {
                mRecyclerView.getEmptyView().findViewById(R.id.loading_progressbar).setVisibility(View.INVISIBLE);
                ((TextView) mRecyclerView.getEmptyView().findViewById(R.id.loading_message))
                        .setText(getContext().getResources().getString(R.string.tap_to_reload));
                mRecyclerView.getEmptyView().findViewById(R.id.loading_message).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processLoadEventList();
                    }
                });
                mRecyclerView.showEmptyView();
            }
        }));
    }



    public void onViewBtnClicked(Result model) {
        ((MainActivity)getActivity()).goToEventViewPage(model);
    }

    public void onEditBtnClicked(Result model) {
        ((MainActivity)getActivity()).goToEventEditPage(Integer.parseInt(model.getId()));
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
