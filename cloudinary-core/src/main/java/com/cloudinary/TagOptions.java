package com.cloudinary;

import java.util.Map;

public class TagOptions {
    private Map<? extends String, ? extends String> attributes;
    private Srcset srcset;

    public TagOptions attributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public TagOptions srcset(Srcset srcset){
        this.srcset = srcset;
        return this;
    }

    Map<? extends String, ? extends String> getAttributes() {
        return attributes;
    }

    public Srcset getSrcset() {
        return srcset;
    }
}
