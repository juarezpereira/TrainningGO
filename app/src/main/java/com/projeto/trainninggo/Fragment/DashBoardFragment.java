package com.projeto.trainninggo.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projeto.trainninggo.Base.BaseFragment;
import com.projeto.trainninggo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashBoardFragment extends BaseFragment {

    @BindView(R.id.TabLayout) TabLayout mTabLayout;
    @BindView(R.id.ViewPager) ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this,view);

        this.mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        this.mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setupWithViewPager(mViewPager);
            }
        });

        return view;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new MapViewFragment();
                case 1: return new GroupsFragment();
                default:return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return getString(R.string.fragment_map_title);
                case 1: return getString(R.string.fragment_groups_title);
                default:return super.getPageTitle(position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

}
