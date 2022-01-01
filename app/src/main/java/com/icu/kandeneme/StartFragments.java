package com.icu.kandeneme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class StartFragments extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;

    public static StartFragments newInstance() {
        return new StartFragments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.start_fragments, container, false);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabLayout = rootView.findViewById(R.id.tab_layout);
        fragmentManager manager2 = new fragmentManager(getParentFragmentManager());
        viewPager.setAdapter(manager2);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }
}
