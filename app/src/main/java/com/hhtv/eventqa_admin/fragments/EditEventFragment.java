package com.hhtv.eventqa_admin.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.activities.MainActivity;
import com.hhtv.eventqa_admin.api.APIEndpoint;
import com.hhtv.eventqa_admin.api.APIService;
import com.hhtv.eventqa_admin.helpers.MyCallBack;
import com.hhtv.eventqa_admin.helpers.NetworkFailBuilder;
import com.hhtv.eventqa_admin.models.event.Result;
import com.hhtv.eventqa_admin.models.user.GetUserResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 11/3/16.
 */
public class EditEventFragment extends BaseFragment implements CalendarDatePickerDialogFragment.OnDateSetListener
,RadialTimePickerDialogFragment.OnTimeSetListener{


    @Bind(R.id.eedit_title)
    TextView eeditTitle;
    @Bind(R.id.eedit_name)
    EditText eeditName;
    @Bind(R.id.eedit_description)
    EditText eeditDescription;
    @Bind(R.id.eedit_startat)
    TextView eeditStartat;
    @Bind(R.id.eedit_endat)
    TextView eeditEndat;
    @Bind(R.id.eedit_starttime)
    TextView eeditStarttime;
    @Bind(R.id.eedit_endtime)
    TextView eeditEndtime;
    @Bind(R.id.eedit_img)
    ImageView eeditImg;
    @Bind(R.id.eedit_qrcode)
    ImageView eeditQrcode;
    @Bind(R.id.eedit_back)
    Button eeditBack;
    @Bind(R.id.eedit_save)
    Button eeditSave;
    @Bind(R.id.eedit_startat_btn)
    Button eeditStartAtBtn;
    @Bind(R.id.eedit_endat_btn)
    Button eeditEndAtBtn;
    @Bind(R.id.eedit_starttime_btn)
    Button eeditStartTimeBTn;
    @Bind(R.id.eedit_endtime_btn)
    Button eeditEndTimeBTn;
    @Bind(R.id.eedit_delete)
    Button eeditDelete;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    MaterialDialog d;
    boolean isUserSelectNewImage = false;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    @OnClick({R.id.eedit_endtime_btn, R.id.eedit_endat_btn, R.id.eedit_delete, R.id.eedit_startat_btn, R.id.eedit_starttime_btn, R.id.eedit_img, R.id.eedit_qrcode, R.id.eedit_back, R.id.eedit_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.eedit_startat_btn:
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                String month  = ((monthOfYear + 1) < 10)? "0" + (monthOfYear + 1) : (monthOfYear+1)+"";
                                String day = (dayOfMonth < 10)? "0" + dayOfMonth : "" + dayOfMonth;
                                eeditStartat.setText(year + "-" + month + "-" + day);
                            }
                        })
                        .setThemeLight();
                cdp.show(getActivity().getSupportFragmentManager(),FRAG_TAG_DATE_PICKER);
                break;
            case R.id.eedit_endat_btn:
                CalendarDatePickerDialogFragment cdp2 = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                String month  = ((monthOfYear + 1) < 10)? "0" + (monthOfYear + 1) : (monthOfYear+1)+"";
                                String day = (dayOfMonth < 10)? "0" + dayOfMonth : "" + dayOfMonth;
                                eeditEndat.setText(year + "-" + month + "-" + day);
                            }
                        })
                        .setThemeLight();
                cdp2.show(getActivity().getSupportFragmentManager(),FRAG_TAG_DATE_PICKER);
                break;
            case R.id.eedit_starttime_btn:
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                String hour = (hourOfDay < 10)? "0" + hourOfDay : "" + hourOfDay;
                                String min = (minute < 10)? "0" + minute : "" + minute;
                                eeditStarttime.setText(hour + ":" +min + ":00");
                            }
                        });
                rtpd.show(getActivity().getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                break;
            case R.id.eedit_endtime_btn:
                RadialTimePickerDialogFragment rtpd2 = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                String hour = (hourOfDay < 10)? "0" + hourOfDay : "" + hourOfDay;
                                String min = (minute < 10)? "0" + minute : "" + minute;
                                eeditEndtime.setText(hour + ":" +min + ":00");
                            }
                        });
                rtpd2.show(getActivity().getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                break;
            case R.id.eedit_img:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_PICTURE);
                }
                break;
            case R.id.eedit_qrcode:
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("qrcode", model.getQrCodeLink());
                clipboard.setPrimaryClip(clip);
                break;
            case R.id.eedit_back:
                /*AlertDialog.Builder builder =
                        new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Exit");
                builder.setMessage("Your edit will be lost");
                builder.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
                        dialog.dismiss();
                    }
                });*/
                /*if(d == null){
                    d = new MaterialDialog.Builder(EditEventFragment.this.getContext())
                            .title("Exit")
                            .content("Your edit will be lost")
                            .positiveText("Stay")
                            .negativeText("Exit")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    getActivity().onBackPressed();
                                    dialog.dismiss();
                                }
                            }).build();
                }
                d.show();*/
                getActivity().onBackPressed();
                break;
            case R.id.eedit_save:
                switch (action){
                    case CREATE:
                        processAddNewEvent();
                        break;
                    case EDIT:
                        processEditEvent();
                        break;

                }
                break;
            case R.id.eedit_delete:
                new MaterialDialog.Builder(getActivity())
                        .title("Delete event")
                        .content("Your event will be delete, no one can see or discus anymore")
                        .negativeText("Dismiss")
                        .positiveText("Delete")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                processDeleteEvent();
                            }
                        }).show();
                break;
        }
    }

    //TODO implement update and create method
    private void processDeleteEvent(){

    }

    private void processEditEvent(){
        if (!isInputInvalid()){
            Call<String> call =api.updateEvent(eeditName.getText().toString(),
                    eeditDescription.getText().toString(),
                    eeditStartat.getText().toString() + " " + eeditStarttime.getText().toString(),
                    eeditStartat.getText().toString() + " " + eeditStarttime.getText().toString(),
                    isUserSelectNewImage ? RequestBody.create(MediaType.parse("multipart/form-data"), imagePath) : null);


            //RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imagePath);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Response<String> response, Retrofit retrofit) {

                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }else{
            Toast.makeText(getActivity(),"Your event name and description could not be blank !", Toast.LENGTH_SHORT).show();
        }
    }
    private void processAddNewEvent(){
        if (!isInputInvalid()){

        }else{
            Toast.makeText(getActivity(),"Your event name and description could not be blank !", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInputInvalid(){
        return eeditName.getText().toString().length() == 0 ||
                eeditDescription.getText().toString().length() == 0;
    }

    private static final int SELECT_PICTURE = 1;
    String imagePath;
    File uploadFile;
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Picasso.with(getContext()).load(selectedImageUri).into(eeditImg);
                imagePath = selectedImageUri.getPath();
                uploadFile = new File(selectedImageUri.getPath());
                isUserSelectNewImage = true;
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;
        try {
            if (Build.VERSION.SDK_INT > 19) {
                // Will return "image:x*"
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                cursor = getContext().getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, sel, new String[] { id }, null);
            } else {
                cursor = getContext().getContentResolver().query(contentUri,
                        projection, null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = null;
        try {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return path;
    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {

    }
    public enum ActionType {
        EDIT, CREATE
    }

    private ActionType action;
    private int eventId;
    private GetUserResponse user;
    public static EditEventFragment newInstance(GetUserResponse user, ActionType action, int eventId) {
        EditEventFragment f = new EditEventFragment();
        f.user = user;
        f.action = action;
        f.eventId = eventId;
        return f;
    }

    LinearLayout rootLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootLayout = new LinearLayout(getContext());
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        switch (action){
            case EDIT:
                View v1 = inflater.inflate(R.layout.loadingview, container, false);
                rootLayout.removeAllViews();
                rootLayout.addView(v1);
                prepareEditEventView(inflater, container);
                break;
            case CREATE:
                View v2 = inflater.inflate(R.layout.fragment_edit_event_main, container, false);
                rootLayout.removeAllViews();
                rootLayout.addView(v2);
                ButterKnife.bind(this, rootLayout);
                prepareCreateEventView();
                break;
            default:
                break;
        }
        return rootLayout;
    }

    private void prepareCreateEventView(){
        eeditTitle.setText(getResources().getString(R.string.create_new_event));
        rootLayout.findViewById(R.id.textView6).setVisibility(View.GONE);
        eeditQrcode.setVisibility(View.GONE);
    }


    APIEndpoint api = APIService.build();
    Result model;
    private void prepareEditEventView(final LayoutInflater inflater, final ViewGroup container){
        Call<Result> call = api.getEvent(eventId);
        call.enqueue(new MyCallBack(getContext(), new MyCallBack.IOnDataReceived() {
            @Override
            public void onReceived(Response response, Retrofit retrofit) {
                Log.d("MYTAG2","call on: " + response.raw().request().url());
                if (response.isSuccess()){
                    View v = inflater.inflate(R.layout.fragment_edit_event_main, container, false);
                    rootLayout.removeAllViews();
                    rootLayout.addView(v);
                    ButterKnife.bind(EditEventFragment.this, rootLayout);
                    model = (Result)response.body();
                    eeditTitle.setText(getResources().getString(R.string.edit_your_event) + model.getName());
                    eeditName.setText(model.getName());
                    eeditDescription.setText(model.getDescription());
                    /*eeditStartat.setText(model.getCreated_date().split(" ")[0]);
                    eeditStarttime.setText(model.getCreated_date().split(" ")[1]);*/
                    Picasso.with(getContext()).load(model.getImageLink()).error(R.drawable.side_nav_bar)
                            .into(eeditImg);
                    Picasso.with(getContext()).load(model.getQrCodeLink()).error(R.drawable.side_nav_bar)
                            .into(eeditQrcode);
                }else{
                    new NetworkFailBuilder(EditEventFragment.this.getContext())
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    ((MainActivity)getActivity()).removeCurrentFragment();
                                }
                            }).show();
                }
            }

            @Override
            public void onError(Throwable t) {
                new NetworkFailBuilder(EditEventFragment.this.getContext())
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                ((MainActivity)getActivity()).removeCurrentFragment();
                            }
                        }).show();
            }
        }));
    }
}
