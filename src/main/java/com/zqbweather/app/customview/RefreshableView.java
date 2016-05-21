package com.zqbweather.app.customview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zqbweather.app.R;
import com.zqbweather.app.activity.MyApplication;

/**
 * 可进行下拉刷新的自定义控件
 */
public class RefreshableView extends LinearLayout implements View.OnTouchListener{

    public static final int STATUS_PULL_TO_REFRESH = 0;     //下拉状态
    public static final int STATUS_RELEASE_TO_REFRESH = 1;  //释放立即刷新状态
    public static final int STATUS_REFRESHING = 2;          //正在刷新状态
    public static final int STATUS_REFRESH_FINISHED = 3;    //刷新完成或未刷新状态
    public static final int SCROLL_SPEED = -16;             //下拉头回滚的速度

    public static final long ONE_MINUTE = 60 * 1000;         //一分钟的毫秒值，用于判断上次的更新时间
    public static final long ONE_HOUR = 60 * ONE_MINUTE;    //一小时的毫秒值，用于判断上次的更新时间
    public static final long ONE_DAY = 24 * ONE_HOUR;       //一天的毫秒值，用于判断上次的更新时间
    public static final long ONE_MONTH = 30 * ONE_DAY;      //一月的毫秒值，用于判断上次的更新时间
    public static final long ONE_YEAR = 12 * ONE_MONTH;     //一年的毫秒值，用于判断上次的更新时间

    private static final String UPDATE_AT = "updated_at";   //上次更新时间的字符串常量，用于作为SharePreferenced的键值


    private SharedPreferences preferences;      //用于存储上次更新时间

    private View header;                    //下拉头的View
    private ProgressBar progressBar;        //刷新时显示的进度条
    private ImageView arrow;                //指示下拉和释放的箭头
    private TextView description;           //指示下拉和释放的文字描述
    private TextView updateAt;              //上次更新时间的文字描述

    private RelativeLayout relativeLayout;  //需要下拉刷新的RelativeLayout

    private MarginLayoutParams headerLayoutParams;      //下拉头的布局参数

    private MarginLayoutParams contentLayoutParams;     //内容的布局参数

    private PullToRefreshListener mListener;            //下拉刷新的回调接口

    private int touchSlop;                  //在被判定为滚动之前用户手指可以移动的最大值

    private long lastUpdateTime;            //上次更新时间的毫秒值

    private int mId = -1;                   //为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分

    private boolean loadOnce;               //是否已加载过一次layout，这里onLayout中的初始化只需加载一次

    private int hideHeaderHeight;           //下拉头的高度

    private float yDown;                    //手指按下时屏幕纵坐标


    private int currentStatus = STATUS_REFRESH_FINISHED;        //当前处理什么状态
    private int lastStatus = currentStatus;                     //记录上一次的状态是什么，避免进行重复操作



    /**
     * 下拉控件的构造函数，会在运行时动态添加一个下拉头布局
     */
    public RefreshableView(Context context, AttributeSet attrs){
        super(context, attrs);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh, null, true);
        progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        description = (TextView) header.findViewById(R.id.description);
        updateAt = (TextView) header.findViewById(R.id.updated_at);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        refreshUpdatedAtValue();
        setOrientation(VERTICAL);
        addView(header, 0);
    }

    /**
     * 刷新下拉头中上次更新时间的文字描述
     */
    private void refreshUpdatedAtValue(){
        lastUpdateTime = preferences.getLong(UPDATE_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if(lastUpdateTime == -1){
            updateAtValue = getResources().getString(R.string.not_updated_yet);
        }else if(timePassed < 0){
            updateAtValue = getResources().getString(R.string.time_error);
        }else if(timePassed < ONE_MINUTE){
            updateAtValue = getResources().getString(R.string.updated_just_now);
        }else if(timePassed < ONE_HOUR){
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "分钟";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }else if(timePassed < ONE_DAY){
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }else if(timePassed < ONE_MONTH){
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }else if(timePassed < ONE_YEAR){
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }else{
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }
        updateAt.setText(updateAtValue);
    }

    /**
     * 进行一些关键性的初始化操作，比如：将下拉头向上偏移进行隐藏，给RelativeLayout注册touch事件。
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        super.onLayout(changed, l, t, r, b);
        if(changed && !loadOnce){
            hideHeaderHeight = - header.getHeight();
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;
            relativeLayout = (RelativeLayout) getChildAt(1);
            contentLayoutParams = (MarginLayoutParams) relativeLayout.getLayoutParams();
//            relativeLayout.setOnTouchListener(this);
            this.setOnTouchListener(this);
            loadOnce = true;
        }
    }

    /**
     * 当RelativeLayout被触摸时调用，其中处理了各种下拉刷新的具体逻辑。
     */
    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                yDown = event.getRawY();
//                Toast.makeText(MyApplication.getContext(), "按下了", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_MOVE:
//                Toast.makeText(MyApplication.getContext(), "移动了", Toast.LENGTH_SHORT).show();
                float yMove = event.getRawY();
                int distance = (int) (yMove - yDown);
                if(distance <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight){
                    return false;
                }
                if(distance < touchSlop){
                    return false;
                }
                if(currentStatus != STATUS_REFRESHING){
                    if(headerLayoutParams.topMargin > 0){
                        currentStatus = STATUS_RELEASE_TO_REFRESH;
                    }else {
                        currentStatus = STATUS_PULL_TO_REFRESH;
                    }
                    headerLayoutParams.topMargin = (distance / 2) + hideHeaderHeight;
                    contentLayoutParams.bottomMargin = - (distance / 2);
                    relativeLayout.setLayoutParams(contentLayoutParams);
                    header.setLayoutParams(headerLayoutParams);
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                if(currentStatus == STATUS_RELEASE_TO_REFRESH){
                    new RefreshingTask().execute();
                }else if(currentStatus == STATUS_PULL_TO_REFRESH){
                    new HideHeaderTask().execute();
                }
                break;
        }
        if(currentStatus == STATUS_PULL_TO_REFRESH
                || currentStatus == STATUS_RELEASE_TO_REFRESH){
            updateHeaderView();
            lastStatus = currentStatus;
            return true;
        }
        return true;
    }

    /**
     * 给下拉刷新控件注册一个监听器。
     *
     * @param listener
     *            监听器的实现。
     * @param id
     *            为了防止不同界面的下拉刷新在上次更新时间上互相有冲突， 请不同界面在注册下拉刷新监听器时一定要传入不同的id。
     */
    public void setOnRefreshListener(PullToRefreshListener listener, int id){
        mListener = listener;
        mId = id;
    }

    /**
     * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
     */
    public void finishRefreshing(){
        currentStatus = STATUS_REFRESH_FINISHED;
        preferences.edit().putLong(UPDATE_AT + mId, System.currentTimeMillis()).commit();
        new HideHeaderTask().execute();
    }

    /**
     * 更新下拉头中的信息。
     */
    private void updateHeaderView(){
        if(lastStatus != currentStatus){
            if(currentStatus == STATUS_PULL_TO_REFRESH){
                description.setText(getResources().getString(R.string.pull_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            }else if(currentStatus == STATUS_RELEASE_TO_REFRESH){
                description.setText(getResources().getString(R.string.release_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            }else if(currentStatus == STATUS_REFRESHING) {
                description.setText(getResources().getString(R.string.refreshing));
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
            refreshUpdatedAtValue();
        }
    }

    /**
     * 根据当前的状态来旋转箭头。
     */
    private void rotateArrow(){
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegress = 0f;
        float toDegress = 0f;
        if(currentStatus == STATUS_PULL_TO_REFRESH){
            fromDegress = 180f;
            toDegress = 360f;
        }else if(currentStatus == STATUS_RELEASE_TO_REFRESH){
            fromDegress = 0f;
            toDegress = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegress, toDegress, pivotX,pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }

    /**
     * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
     */
    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
                SystemClock.sleep(1);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            updateHeaderView();
            headerLayoutParams.topMargin = topMargin[0];
            contentLayoutParams.bottomMargin = - headerLayoutParams.topMargin;
//            if(headerLayoutParams.topMargin == 0){
//                contentLayoutParams.bottomMargin = hideHeaderHeight;
//            }
            relativeLayout.setLayoutParams(contentLayoutParams);
            header.setLayoutParams(headerLayoutParams);
        }

    }

    /**
     * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
     */
    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
               SystemClock.sleep(1);
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            headerLayoutParams.topMargin = topMargin[0];
            contentLayoutParams.bottomMargin = - headerLayoutParams.topMargin;
            if(contentLayoutParams.bottomMargin >= 0){
                contentLayoutParams.bottomMargin = 0;
            }
            relativeLayout.setLayoutParams(contentLayoutParams);
            header.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            headerLayoutParams.topMargin = topMargin;
            contentLayoutParams.bottomMargin = - headerLayoutParams.topMargin;
            if(contentLayoutParams.bottomMargin >= 0){
                contentLayoutParams.bottomMargin = 0;
            }
            relativeLayout.setLayoutParams(contentLayoutParams);
            header.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }

    /**
     * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
     */
    public interface PullToRefreshListener {

        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在子线程中调用的， 你可以不必另开线程来进行耗时操作。
         */
        void onRefresh();

    }
}
