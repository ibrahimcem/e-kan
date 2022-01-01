package com.icu.kandeneme;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class fragmentManager extends FragmentStatePagerAdapter {
    public fragmentManager(@NonNull androidx.fragment.app.FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment selectedFragment;

        switch (position) {
            case 0:
                selectedFragment = BloodSearch.newInstance();
                break;
            case 1:
                selectedFragment = CellSearch.newInstance();
                break;
            default:
                return null;
        }
        return selectedFragment;
    }


    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {
        CharSequence selectedTitle;
        switch (position) {
            case 0:
                selectedTitle = "Kan Ara";
                break;
            case 1:
                selectedTitle = "Kök Hücre Ara";
                break;
            default:
                return null;
        }
        return selectedTitle;
    }
}
