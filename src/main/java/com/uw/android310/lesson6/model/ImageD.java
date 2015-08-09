package com.uw.android310.lesson6.model;


public class ImageD {
    public boolean success;
    public int status;
    public boolean data;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("ImageResponse {")
                .append("success=" + success)
                .append(", status=" + status)
                .append(", data=" + data)
                .append('}')
                .toString();
    }
}
