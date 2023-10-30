package com.xvdong.audioplayer.ui;

import android.os.Bundle;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.databinding.ActivityMainBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by xvDong on 2023/10/27.
 */

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
    }

    private Fragment currentFragment;

    private void initView() {

        MusicFragment musicFragment = new MusicFragment();
        CollectionFragment collectionFragment = new CollectionFragment();
        ProfileFragment profileFragment = new ProfileFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_root, profileFragment).hide(profileFragment);
        transaction.add(R.id.fl_root, collectionFragment).hide(collectionFragment);
        transaction.add(R.id.fl_root, musicFragment).show(musicFragment);
        currentFragment = musicFragment;
        transaction.commit();

        mBinding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_music){
                changeFragment(musicFragment);
                return true;
            }else if (item.getItemId() == R.id.navigation_collection){
                changeFragment(collectionFragment);
                return true;
            }else if (item.getItemId() == R.id.navigation_profile){
                changeFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(currentFragment);
        transaction.show(fragment);
        currentFragment = fragment;
        transaction.commit();
    }

}
