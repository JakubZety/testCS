package com.test.domain;

import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.*;

/**
 * Created by Jakub on 24.09.2019.
 */
@Entity
@Table(name = "event_log")
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique=true)
    private String idLog;
    private long timestamp_started;
    private long timestamp_finished;
    private long time_duration;
    private String type;
    private String host;
    @Column(columnDefinition = "boolean default false")
    private boolean alert;

    public EventLog() {}

    public EventLog(long id, String idLog, long timestamp_started, long timestamp_finished, long time_duration, String type, String host, boolean alert) {
        this.id = id;
        this.idLog = idLog;
        this.timestamp_started = timestamp_started;
        this.timestamp_finished = timestamp_finished;
        this.time_duration = time_duration;
        this.type = type;
        this.host = host;
        this.alert = alert;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdLog() {
        return idLog;
    }

    public void setIdLog(String idLog) {
        this.idLog = idLog;
    }

    public long getTimestamp_started() {
        return timestamp_started;
    }

    public void setTimestamp_started(long timestamp_started) {
        this.timestamp_started = timestamp_started;
    }

    public long getTimestamp_finished() {
        return timestamp_finished;
    }

    public void setTimestamp_finished(long timestamp_finished) {
        this.timestamp_finished = timestamp_finished;
    }

    public long getTime_duration() {
        return time_duration;
    }

    public void setTime_duration(long time_duration) {
        this.time_duration = time_duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }
}
