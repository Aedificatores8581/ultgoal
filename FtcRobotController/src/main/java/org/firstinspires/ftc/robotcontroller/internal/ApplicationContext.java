package org.firstinspires.ftc.robotcontroller.internal;

import android.content.Context;

public class ApplicationContext{
    private static Context currentContext;

    public static void setContext(Context c) {
        currentContext = c;
    }

    public static Context getContext(){
        return currentContext;
    }
}
