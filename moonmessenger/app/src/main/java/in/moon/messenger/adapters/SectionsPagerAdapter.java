package in.moon.messenger.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.moon.messenger.fragments.MessagesFragment;
import in.moon.messenger.fragments.UsersFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter{


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                UsersFragment usersFragment = new UsersFragment();
                return usersFragment;

            case 1:
                MessagesFragment messagesFragment = new MessagesFragment();
                return messagesFragment;

            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "users";

            case 1:
                return "messages";

            default:
                return null;
        }

    }

}
