package com.philliphsu.clock2.listener;


import com.philliphsu.clock2.model.Channel;

public interface WeatherServiceListener {
    void serviceSuccess(Channel channel);

    void serviceFailure(Exception exception);
}
