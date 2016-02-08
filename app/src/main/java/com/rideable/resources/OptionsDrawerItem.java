package com.rideable.resources;

/**
 * Created by Jay on 11/19/2015.
 */
public class OptionsDrawerItem {
    private int icon;
    private String name;
    private boolean showNotify;

    public OptionsDrawerItem(){}
    // Constructor.
    public OptionsDrawerItem(int icon, String name, boolean showNotify) {

        this.icon = icon;
        this.name = name;
        this.showNotify = showNotify;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }
}
