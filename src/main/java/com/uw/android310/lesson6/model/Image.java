package com.uw.android310.lesson6.model;


public class Image {
    public boolean success;
    public int status;
    public Data data;

    public static class Data {
        public String id;
        public String title;
        public String description;
        public String type;
        public boolean animated;
        public int width;
        public int height;
        public int size;
        public int views;
        public int bandwidth;
        public String vote;
        public boolean favorite;
        public String account_url;
        public String deletehash;
        public String name;
        public String link;


        @Override
        public String toString() {
            return new StringBuilder()
                    .append("Data{")
                    .append("id='" + id + '\'')
                    .append(", title='" + title + '\'')
                    .append(", description='" + description + '\'')
                    .append(", type='" + type + '\'')
                    .append(", animated=" + animated)
                    .append(", width=" + width)
                    .append(", height=" + height)
                    .append(", size=" + size)
                    .append(", views=" + views)
                    .append(", bandwidth=" + bandwidth)
                    .append(", vote='" + vote + '\'')
                    .append(", favorite=" + favorite)
                    .append(", account_url='" + account_url + '\'')
                    .append(", deletehash='" + deletehash + '\'')
                    .append(", name='" + name + '\'')
                    .append(", link='" + link + '\'')
                    .append('}')
                    .toString();
        }
    }


    public String getLink() {
        return data.link;
    }

    public String getId() {
        return data.id;
    }

    public String getDeletehash() {
        return data.deletehash;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("ImageResponse {")
                .append("success=" + success)
                .append(", status=" + status)
                .append(", data=" + data.toString())
                .append('}')
                .toString();
    }
}
