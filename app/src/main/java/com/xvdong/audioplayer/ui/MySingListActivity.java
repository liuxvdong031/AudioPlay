package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.lxj.xpopup.XPopup;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.SingListAdapter;
import com.xvdong.audioplayer.databinding.ActivityMySingListMainBinding;
import com.xvdong.audioplayer.db.DbUtils;
import com.xvdong.audioplayer.db.SingListDatabase;
import com.xvdong.audioplayer.model.SingListBean;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的歌单
 */
public class MySingListActivity extends AppCompatActivity {

    private ActivityMySingListMainBinding mBinding;
    private SingListDatabase mListDatabase;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_sing_list_main);
        initDatabase();
    }

    private void initDatabase() {
        DbUtils.getSingListDataBase(this, database -> {
            mListDatabase = database;
            initData();
        });
    }

    @SuppressLint("CheckResult")
    private void initData() {
        mBinding.toolbar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(TitleBar titleBar) {
                finish();
            }

            @Override
            public void onRightClick(TitleBar titleBar) {
                new XPopup.Builder(MySingListActivity.this)
                        .asInputConfirm("新建歌单", "请输入歌单的名称。", text -> {
                            createNewSingList(text);
                        })
                        .show();
            }
        });

        mListDatabase.mSingListDao()
                .getAllPlaylists()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(singListBeans -> {
                    if (singListBeans == null && singListBeans.size() == 0) {
                    } else {
                        initSingList(singListBeans);
                    }
                });
    }

    /**
     * 创建一个新的歌单
     *
     * @param text 歌单名称
     */
    private void createNewSingList(String text) {
        SingListBean bean = new SingListBean(text, System.currentTimeMillis());
        mListDatabase.mSingListDao()
                .insertSing(bean)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void initSingList(List<SingListBean> singListBeans) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvSingList.setLayoutManager(layoutManager);
        SingListAdapter singListAdapter = new SingListAdapter(this, singListBeans);
        mBinding.rvSingList.setAdapter(singListAdapter);
    }
}