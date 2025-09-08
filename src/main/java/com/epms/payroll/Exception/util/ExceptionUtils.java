package com.project.hms.Exception.util;


import java.util.UUID;

public class ExceptionUtils {

    // Generate correlation traceId (could integrate with Sleuth/Zipkin in microservices)
    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}