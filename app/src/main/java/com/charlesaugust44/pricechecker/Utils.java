package com.charlesaugust44.pricechecker;

import java.util.Stack;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Utils {
    public static FragmentManager fragmentManager;
    public static Stack<Bundle> bundleStack = new Stack<>();
    public static Stack<Fragment> backStack = new Stack<>();

    public static void openFragment(Fragment fragment, Bundle data) {
        if (data != null)
            fragment.setArguments(data);

        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_index, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public static void addToStack(Fragment fragment, Bundle data) {
        backStack.add(fragment);
        bundleStack.add(data);
    }

    public static void goBack(Bundle data) {
        openFragment(backStack.pop(), data);
    }

    public static void cleanBackStack() {
        bundleStack = new Stack<>();
        backStack = new Stack<>();
    }

    public static void backToStart() {
        Bundle b = new Bundle();
        b.putInt("mode", 1);

        Utils.backStack.push(new ProductFragment());
        Utils.bundleStack.push(b);
    }
}

