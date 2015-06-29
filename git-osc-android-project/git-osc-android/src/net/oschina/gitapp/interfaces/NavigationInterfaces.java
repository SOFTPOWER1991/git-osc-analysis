package net.oschina.gitapp.interfaces;

public class NavigationInterfaces {
    private NavigationInterfaces() {
        super();
    }

    public interface SlidingMenuListener {
        void onShowAbove();
    }

    public interface NavigationDrawerListener {
        void onDrawerClosed();

        void onDrawerOpened();
    }
}
