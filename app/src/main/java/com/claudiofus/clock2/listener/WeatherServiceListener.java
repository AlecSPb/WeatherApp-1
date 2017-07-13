package com.claudiofus.clock2.listener;


import com.claudiofus.clock2.model.Channel;

public interface WeatherServiceListener {
    void serviceSuccess(Channel channel);

    void serviceFailure(Exception exception);
}
