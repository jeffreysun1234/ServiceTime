package com.mycompany.servicetime.data.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.mycompany.servicetime.CHApplication;
import com.mycompany.servicetime.R;

/**
 * Created by szhx on 4/1/2016.
 */
public class AccessFirebaseAsyn extends AsyncTask {
    private ProgressDialog mProgressDialog;
    private final Context mContext;
    BackgroundAction mBackgroundAction;

    public interface BackgroundAction {
        void doActionInBackground();

        void doOnPostExecute();
    }

    public AccessFirebaseAsyn(Context context, BackgroundAction backgroundAction) {
        this.mContext = context;
        this.mBackgroundAction = backgroundAction;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(
                CHApplication.getContext().getString(R.string.progress_dialog_loading));
        mProgressDialog.setMessage(
                CHApplication.getContext().getString(R.string.progress_dialog_access_firebase));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        mBackgroundAction.doActionInBackground();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mProgressDialog.dismiss();

        mBackgroundAction.doOnPostExecute();
    }
}
