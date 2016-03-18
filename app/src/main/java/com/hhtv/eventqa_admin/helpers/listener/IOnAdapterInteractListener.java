package com.hhtv.eventqa_admin.helpers.listener;

/**
 * Created by nienb on 18/3/16.
 */
public interface IOnAdapterInteractListener {
    void scroll(int position);
    void onAnsBtnClick(int id);
    void onDelBtnClick(int id);
    void onDupBtnClick(int id);
    void onItemClick(int id);
}