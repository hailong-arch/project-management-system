package com.hli.projectmanagementsystem.bucket;

public enum BucketName {
    PROFILE_IMAGE("hli-image-upload-001");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
