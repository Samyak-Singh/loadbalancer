package com.example.loadbalancer.model;
public class BackendServer {
    private String url;

    public BackendServer() {
    }

    public BackendServer(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackendServer that = (BackendServer) o;
        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return "BackendServer{" +
                "url='" + url + '\'' +
                '}';
    }
}
